package io.github.kaustubhkhati.runbook.automation.config;

import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "zendesk-groups")
@Getter
@Setter
public class ZendeskGroupsProps {

   private List<ZendeskGroup> groups;

   @Getter
   @Setter
   public static class ZendeskGroup {
      private String url;
      private long id;
      private boolean isPublic;
      private String name;
      private String description;
      private boolean isDefault;
      private boolean deleted;
      private String createdAt;
      private String updatedAt;
   }
}