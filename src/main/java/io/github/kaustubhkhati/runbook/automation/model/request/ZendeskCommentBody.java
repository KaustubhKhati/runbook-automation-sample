package io.github.kaustubhkhati.runbook.automation.model.request;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ZendeskCommentBody(ZendeskTicketCommentUpdate ticket) {

   public record CommentRequest(String body, @JsonProperty("public") boolean publicComment) {
   }

   public record ZendeskTicketCommentUpdate(
       CommentRequest comment,
       @JsonProperty("group_id") long groupId) {
   }
}
