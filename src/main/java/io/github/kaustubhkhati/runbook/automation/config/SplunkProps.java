package io.github.kaustubhkhati.runbook.automation.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "splunk")
@Getter
@Setter
public class SplunkProps {
   private String baseUrl;
   private String authToken;
   private Endpoints endpoints;

   @ConfigurationProperties(prefix = "endpoints")
   @Getter
   @Setter
   public static class Endpoints {
      private String exportSearch;
   }
}