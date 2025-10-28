package io.github.kaustubhkhati.runbook.automation.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "authentication")
@Getter
@Setter
public class AuthenticationConfiguration {
   private String origin;
   private String username;
   private String password;
   private Endpoints endpoints;

   @Getter
   @Setter
   @ConfigurationProperties("endpoints")
   public static class Endpoints {
      private String issueJWTToken;
   }
}
