package io.github.kaustubhkhati.runbook.automation.model.response;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.io.Serial;
import java.io.Serializable;
import java.time.OffsetDateTime;
import java.util.List;

public record ZendeskCommentsResponse(List<ZendeskComment> comments) implements Serializable {

   @JsonSerialize
   public record ZendeskComment(
       long id,
       String body,
       Long author_id,
       OffsetDateTime created_at,
       OffsetDateTime updated_at,
       Boolean public_,
       List<String> attachments
   ) implements Serializable {
      @Serial
      private static final long serialVersionUID = 1L;
   }
}
