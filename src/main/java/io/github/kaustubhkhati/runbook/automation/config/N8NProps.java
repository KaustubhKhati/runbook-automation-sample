package io.github.kaustubhkhati.runbook.automation.config;

import java.util.Map;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "n8n")
@Getter
@Setter
public class N8NProps {
   private String origin;
   private String webhookCredentials;
   private Map<String, String> endpoints;
}
