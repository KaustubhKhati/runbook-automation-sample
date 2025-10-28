package io.github.kaustubhkhati.runbook.automation.model;

import java.io.Serial;
import java.io.Serializable;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;

public record ZendeskTicket(
    String url,
    long id,
    String external_id,
    ZendeskVia via,
    OffsetDateTime created_at,
    OffsetDateTime updated_at,
    Long generated_timestamp,
    String type,
    String subject,
    String raw_subject,
    String description,
    String priority,
    String status,
    String recipient,
    Long requester_id,
    Long submitter_id,
    Long assignee_id,
    Long organization_id,
    Long group_id,
    List<Long> collaborator_ids,
    List<Long> follower_ids,
    List<Long> email_cc_ids,
    Long forum_topic_id,
    Long problem_id,
    Boolean has_incidents,
    Boolean is_public,
    OffsetDateTime due_at,
    List<String> tags
//    List<ZendeskCustomField> custom_fields
) implements Serializable {
   @Serial
   private static final long serialVersionUID = 1L;

   public record ZendeskVia(String channel, ZendeskViaSource source) implements Serializable {
      @Serial
      private static final long serialVersionUID = 1L;

      public record ZendeskViaSource(
          Map<String, Object> from,
          Map<String, Object> to,
          String rel
      ) implements Serializable {
         @Serial
         private static final long serialVersionUID = 1L;
      }
   }

   public record ZendeskCustomField(long id, Object value) implements Serializable {
      @Serial
      private static final long serialVersionUID = 1L;
   }
}