package io.github.kaustubhkhati.runbook.automation.tools;

import io.github.kaustubhkhati.runbook.automation.config.ZendeskGroupsProps;
import io.github.kaustubhkhati.runbook.automation.model.response.ZendeskCommentResponse;
import io.github.kaustubhkhati.runbook.automation.service.ZendeskService;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Component;

@Component
public class ZendeskTool {
   private final ZendeskService zendeskService;
   private final ZendeskGroupsProps zendeskGroupsProps;

   public ZendeskTool(ZendeskService zendeskService, ZendeskGroupsProps zendeskGroupsProps) {
      this.zendeskService = zendeskService;
      this.zendeskGroupsProps = zendeskGroupsProps;
   }

   @Tool(name = "zendesk_comment", description = "Update Zendesk ticket with ticketId, group and the given comment")
   public String comment(Long ticketId, String oauthToken, String groupName, String comment) {
      if (ticketId == null || oauthToken == null || groupName == null || comment == null) {
         throw new IllegalArgumentException("Missing required parameters: ticketId, oauthToken, groupName, comment");
      }
      long groupId = zendeskGroupsProps.getGroups().stream()
          .filter(g -> g.getName().equalsIgnoreCase(groupName))
          .map(ZendeskGroupsProps.ZendeskGroup::getId)
          .findFirst()
          .orElseThrow(() -> new IllegalArgumentException("Unknown group: " + groupName));
      ZendeskCommentResponse response = zendeskService.commentZendesk(oauthToken, ticketId, comment, groupId);
      if (response.getTicket().getId().equals(ticketId)) {
         return "Comment added for group " + groupName + " (ID: " + groupId + ")";
      } else {
         throw new RuntimeException("Failed");
      }
   }
}
