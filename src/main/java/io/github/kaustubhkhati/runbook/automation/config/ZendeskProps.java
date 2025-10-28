package io.github.kaustubhkhati.runbook.automation.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "zendesk")
@Getter
@Setter
public class ZendeskProps {
   private String baseUrl;
   private Endpoints endpoints;

   @ConfigurationProperties(prefix = "endpoints")
   @Getter
   @Setter
   public static class Endpoints {
      private String ticketsByView;
      private String updateTicket;
      private String commentsByTicket;
      private String addComment;
   }
}
