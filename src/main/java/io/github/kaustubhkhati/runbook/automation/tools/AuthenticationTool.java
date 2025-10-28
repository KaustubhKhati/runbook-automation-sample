package io.github.kaustubhkhati.runbook.automation.tools;

import io.github.kaustubhkhati.runbook.automation.service.AuthenticationService;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Component;

@Component
public class AuthenticationTool {

   private final AuthenticationService authenticationService;

   public AuthenticationTool(AuthenticationService authenticationService) {
      this.authenticationService = authenticationService;
   }

   @Tool(name = "authentication", description = "Retrieve authentication token for API calls")
   public String getAuthenticationToken() {
      return authenticationService.getAuthToken();
   }
}
