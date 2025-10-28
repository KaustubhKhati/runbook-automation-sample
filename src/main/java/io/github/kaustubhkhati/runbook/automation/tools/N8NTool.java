package io.github.kaustubhkhati.runbook.automation.tools;

import io.github.kaustubhkhati.runbook.automation.service.N8NService;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;

@Component
public class N8NTool {

   private final N8NService n8nService;

   public N8NTool(N8NService n8nService) {
      this.n8nService = n8nService;
   }

   @Tool(
       name = "n8n",
       description = "Trigger N8N workflow for awaiting payment and return queue & comment")
   public <T> T callGenericN8NFlow(String endpointKey,
                                   Object[] urlParams,
                                   Object payload,
                                   HttpMethod httpMethod,
                                   Class<T> responseType) {
      return n8nService.callGenericN8NFlow(endpointKey,
          urlParams,
          payload,
          httpMethod,
          responseType);
   }
}
