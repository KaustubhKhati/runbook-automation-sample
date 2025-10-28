package io.github.kaustubhkhati.runbook.automation.model.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ZendeskCommentResponse implements Serializable {

   private Audit audit;
   private Ticket ticket;

   @Getter
   @Setter
   public static class Audit {
      private List<Event> events;
   }

   @Getter
   @Setter
   public static class Event {
      @JsonProperty("field_name")
      private String fieldName;

      private Long id;
      private String type;
      private String value; // present for create events
      private String body;  // present for comment events
   }

   @Getter
   @Setter
   public static class Ticket {
      @JsonProperty("custom_status_id")
      private Integer customStatusId;

      private Long id;

      @JsonProperty("requester_id")
      private Long requesterId;

      private String status;
      private String subject;
   }
}