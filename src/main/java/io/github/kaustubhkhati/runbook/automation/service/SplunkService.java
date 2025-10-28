package io.github.kaustubhkhati.runbook.automation.service;

import io.github.kaustubhkhati.runbook.automation.config.SplunkProps;
import io.github.kaustubhkhati.runbook.automation.model.request.SplunkExportSearchRequest;
import io.github.kaustubhkhati.runbook.automation.model.response.SplunkExportSearchResponse;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;

@Service
public class SplunkService {

   private final RestTemplate restTemplate;
   private final SplunkProps splunkProps;

   public SplunkService(RestTemplate restTemplate, SplunkProps splunkProps) {
      this.restTemplate = restTemplate;
      this.splunkProps = splunkProps;
   }

   /**
    * Common method to call Splunk /search/v2/jobs/export with given search parameter
    */
   private ResponseEntity<SplunkExportSearchResponse[]> exportSearchResults(String searchQuery) {
      String url = new DefaultUriBuilderFactory()
          .builder()
          .host(splunkProps.getBaseUrl())
          .path(splunkProps.getEndpoints().getExportSearch())
          .toUriString();

      HttpHeaders headers = new HttpHeaders();
      headers.setContentType(MediaType.APPLICATION_JSON);
      headers.setBearerAuth(splunkProps.getAuthToken());

      var request = SplunkExportSearchRequest.builder(searchQuery)
          .outputMode("json")
          .execMode("oneshot")
          .maxCount(500)
          .build();

      HttpEntity<SplunkExportSearchRequest> entity = new HttpEntity<>(request, headers);

      return restTemplate.exchange(url, HttpMethod.POST, entity, SplunkExportSearchResponse[].class);
   }

   /**
    * Example public method to search logs by orderId
    */
   public SplunkExportSearchResponse[] searchByOrderId(String orderId) {
      String query = "search index=my_index orderId=" + orderId;
      return exportSearchResults(query).getBody();
   }

   /**
    * Example public method to search logs by traceId
    */
   public SplunkExportSearchResponse[] searchByTraceId(String traceId) {
      String query = "search index=my_index traceId=" + traceId;
      return exportSearchResults(query).getBody();
   }

   /**
    * Example public method to search with a custom query
    */
   public SplunkExportSearchResponse[] searchCustom(String customQuery) {
      return exportSearchResults(customQuery).getBody();
   }
}