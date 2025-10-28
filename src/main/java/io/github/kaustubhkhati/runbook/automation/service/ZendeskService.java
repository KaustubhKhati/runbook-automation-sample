package io.github.kaustubhkhati.runbook.automation.service;

import io.github.kaustubhkhati.runbook.automation.config.ZendeskProps;
import io.github.kaustubhkhati.runbook.automation.model.ZendeskTicket;
import io.github.kaustubhkhati.runbook.automation.model.request.ZendeskCommentBody;
import io.github.kaustubhkhati.runbook.automation.model.response.ZendeskCommentResponse;
import io.github.kaustubhkhati.runbook.automation.model.response.ZendeskCommentsResponse;
import io.github.kaustubhkhati.runbook.automation.model.response.ZendeskTicketsResponse;
import io.micrometer.core.annotation.Timed;
import java.util.List;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class ZendeskService {

   private final RestTemplate restTemplate;
   private final ZendeskProps props;

   public ZendeskService(RestTemplate restTemplate, ZendeskProps props) {
      this.restTemplate = restTemplate;
      this.props = props;
   }

   @Timed(value = "zendesk.fetchTickets", histogram = true)
   public List<ZendeskTicket> fetchTicketsFromView(String token) {
      String path =
          UriComponentsBuilder.fromPath(props.getEndpoints().getTicketsByView())
              .build()
              .toUriString();

      HttpEntity<String> requestEntity = new HttpEntity<>(getHeaders(token));
      ResponseEntity<ZendeskTicketsResponse> responseEntity = callZendeskApi(path, HttpMethod.GET, requestEntity, ZendeskTicketsResponse.class);

      ZendeskTicketsResponse response = responseEntity.getBody();
      return (response != null && response.getTickets() != null) ? response.getTickets() : List.of();
   }

   public ZendeskCommentResponse commentZendesk(String token, long ticketId, String comment, Long groupId) {
      String path =
          UriComponentsBuilder.fromPath(props.getEndpoints().getAddComment())
              .buildAndExpand(ticketId)
              .toUriString();

      ZendeskCommentBody payload =
          new ZendeskCommentBody(
              new ZendeskCommentBody.ZendeskTicketCommentUpdate(
                  new ZendeskCommentBody.CommentRequest(comment, true), // public comment
                  groupId
              )
          );

      HttpEntity<ZendeskCommentBody> requestEntity = new HttpEntity<>(payload, getHeaders(token));
      ResponseEntity<ZendeskCommentResponse> responseEntity = callZendeskApi(path, HttpMethod.PUT, requestEntity, ZendeskCommentResponse.class);

      ZendeskCommentResponse response = responseEntity.getBody();
      if (response == null) {
         throw new RuntimeException("ZendeskService.commentZendesk failed: empty response for ticketId=" + ticketId);
      }
      return response;
   }

   public List<ZendeskCommentsResponse.ZendeskComment> getComments(String token, String ticketId) {
      String path =
          UriComponentsBuilder.fromPath(props.getEndpoints().getCommentsByTicket())
              .buildAndExpand(ticketId)
              .toUriString();

      HttpEntity<String> requestEntity = new HttpEntity<>(getHeaders(token));
      ResponseEntity<ZendeskCommentsResponse> responseEntity = callZendeskApi(path, HttpMethod.GET, requestEntity, ZendeskCommentsResponse.class);

      ZendeskCommentsResponse response = responseEntity.getBody();
      return (response != null && response.comments() != null)
          ? response.comments()
          : List.of();
   }

   private HttpHeaders getHeaders(String token) {
      HttpHeaders headers = new HttpHeaders();
      headers.setContentType(MediaType.APPLICATION_JSON);
      headers.set(HttpHeaders.AUTHORIZATION, "Basic " + token);
      return headers;
   }

   private <T> ResponseEntity<T> callZendeskApi(String endpoint, HttpMethod method, HttpEntity<?> requestEntity, Class<T> responseClass) {
      String url = UriComponentsBuilder.fromUriString(props.getBaseUrl()).path(endpoint).toUriString();
      try {
         return restTemplate.exchange(url, method, requestEntity, responseClass);
      } catch (HttpStatusCodeException ex) {
         // Handle non-2xx responses
         throw new RestClientException(ex.getMessage());
      }
   }
}