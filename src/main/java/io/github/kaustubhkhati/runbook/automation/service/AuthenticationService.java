package io.github.kaustubhkhati.runbook.automation.service;

import io.github.kaustubhkhati.runbook.automation.config.AuthenticationConfiguration;
import io.github.kaustubhkhati.runbook.automation.model.request.AuthenticationTokenRequest;
import io.github.kaustubhkhati.runbook.automation.model.response.AuthenticationTokenResponse;
import io.micrometer.core.annotation.Timed;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class AuthenticationService {
   private final AuthenticationConfiguration authenticationConfiguration;
   private final RestTemplate restTemplate;

   public AuthenticationService(AuthenticationConfiguration authenticationConfiguration, RestTemplate restTemplate) {
      this.authenticationConfiguration = authenticationConfiguration;
      this.restTemplate = restTemplate;
   }

   @Timed(value = "authentication.token.issue", histogram = true)
   public synchronized String getAuthToken() {
      String url = UriComponentsBuilder
          .fromUriString(authenticationConfiguration.getOrigin())
          .path(authenticationConfiguration.getEndpoints().getIssueJWTToken())
          .toUriString();
      AuthenticationTokenRequest request = new AuthenticationTokenRequest(authenticationConfiguration.getUsername(), authenticationConfiguration.getPassword());
      HttpEntity<AuthenticationTokenRequest> requestEntity = new HttpEntity<>(request, getHeaders());
      AuthenticationTokenResponse response = callAuthenticationApi(url, HttpMethod.POST, requestEntity, AuthenticationTokenResponse.class);
      return response.token();
   }

   private HttpHeaders getHeaders() {
      HttpHeaders headers = new HttpHeaders();
      headers.setContentType(MediaType.APPLICATION_JSON);
      return headers;
   }

   public <T> T callAuthenticationApi(String url, HttpMethod method, HttpEntity<?> requestEntity, Class<T> responseClass) {
      ResponseEntity<T> exchange = restTemplate.exchange(url, method, requestEntity, responseClass);
      if (exchange.hasBody()) {
         return exchange.getBody();
      } else {
         throw new HttpClientErrorException(HttpStatusCode.valueOf(400), "Authentication replied with empty body");
      }
   }
}
