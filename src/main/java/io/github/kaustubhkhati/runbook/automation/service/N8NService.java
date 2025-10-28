package io.github.kaustubhkhati.runbook.automation.service;

import io.github.kaustubhkhati.runbook.automation.config.N8NProps;
import io.micrometer.core.annotation.Timed;
import org.jetbrains.annotations.NotNull;
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

/**
 * Generic service to interact with N8N workflows via HTTP.
 * Accepts any payload type and returns any desired response type.
 */
@Service
public class N8NService {

   private final RestTemplate restTemplate;
   private final N8NProps props;

   public N8NService(RestTemplate restTemplate, N8NProps props) {
      this.restTemplate = restTemplate;
      this.props = props;
   }

   /**
    * Calls a configured N8N workflow endpoint using a dynamic payload and response type.
    *
    * @param endpointKey  Key from N8NProps config to resolve the endpoint path.
    * @param urlParams    Path variables for building endpoint URL.
    * @param payload      Request body payload (POJO, Map, or primitive types).
    * @param httpMethod   The HTTP method to use (GET, POST, PUT, DELETE).
    * @param responseType Class of the expected response type.
    * @return Parsed response object of type T.
    */
   @Timed(value = "n8n.genericCall", histogram = true)
   public <T> T callGenericN8NFlow(
       String endpointKey,
       Object[] urlParams,
       Object payload,
       HttpMethod httpMethod,
       Class<T> responseType) {

      String endpointPath = props.getEndpoints().get(endpointKey);
      if (endpointPath == null) {
         throw new IllegalArgumentException("Endpoint not found for key: " + endpointKey);
      }

      String url = UriComponentsBuilder
          .fromUriString(props.getOrigin())
          .path(endpointPath)
          .buildAndExpand(urlParams)
          .toUriString();

      HttpEntity<Object> requestEntity = new HttpEntity<>(payload, getHttpHeaders());
      ResponseEntity<T> responseEntity = callN8NApi(url, httpMethod, requestEntity, responseType);

      if (responseEntity == null || responseEntity.getBody() == null) {
         throw new RuntimeException("N8N call failed: empty response");
      }
      return responseEntity.getBody();
   }

   @NotNull
   private HttpHeaders getHttpHeaders() {
      HttpHeaders headers = new HttpHeaders();
      headers.set("Authorization", "Basic " + props.getWebhookCredentials());
      headers.setContentType(MediaType.APPLICATION_JSON);
      return headers;
   }

   public <T> ResponseEntity<T> callN8NApi(String url, HttpMethod method, HttpEntity<?> requestEntity, Class<T> responseType) {
      try {
         return restTemplate.exchange(url, method, requestEntity, responseType);
      } catch (HttpStatusCodeException ex) {
         throw new RestClientException(ex.getMessage());
      }
   }
}