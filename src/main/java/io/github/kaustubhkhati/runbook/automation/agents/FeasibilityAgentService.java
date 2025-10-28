package io.github.kaustubhkhati.runbook.automation.agents;

import io.github.kaustubhkhati.runbook.automation.tools.AuthenticationTool;
import io.github.kaustubhkhati.runbook.automation.tools.N8NTool;
import io.github.kaustubhkhati.runbook.automation.tools.ZendeskTool;
import io.github.kaustubhkhati.runbook.automation.graph.RunbookState;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import lombok.extern.slf4j.Slf4j;
import org.bsc.langgraph4j.action.AsyncEdgeAction;
import org.jetbrains.annotations.NotNull;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.SystemPromptTemplate;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class FeasibilityAgentService implements AsyncEdgeAction<RunbookState> {

   private final ChatModel chatModel;
   private final AuthenticationTool authenticationTool;
   private final N8NTool n8NTool;
   private final ZendeskTool zendeskTool;

   public FeasibilityAgentService(
       ChatModel chatModel, AuthenticationTool authenticationTool, N8NTool n8NTool, ZendeskTool zendeskTool) {
      this.chatModel = chatModel;
      this.authenticationTool = authenticationTool;
      this.n8NTool = n8NTool;
      this.zendeskTool = zendeskTool;
   }

   @NotNull
   private static SystemPromptTemplate getSystemPromptTemplate() {
      String systemText =
          """
                  You are a helpful assistant who take a look at the variables required to execute a Runbook provided in user massage,
                  Then takes a look at the data that has been provided in extracted in {extracted}. If you find all the variables match the once that have been extracted
                  you will reply with "sufficient" else "insufficient".
                  Do not make up new variables or fields inside extracted. Only reply in "sufficient" or "insufficient".
              """;

      return new SystemPromptTemplate(systemText);
   }

   @Override
   public CompletableFuture<String> apply(RunbookState state) {
      // Build available tool metadata
      SystemPromptTemplate template = getSystemPromptTemplate();
      Message systemMessage = template.createMessage(Map.of("extracted", state.getData()));
      String variables = String.join("", state.getRunbook().requiredVariables());
      UserMessage userMessage =
          new UserMessage(String.format("These are the required fields: %s", variables));
      Prompt prompt = new Prompt(systemMessage, userMessage);
      log.info(
          "{} Invoking AI with extractedData: {} and requiredVariables: {}",
          this.getClass().getName(),
          state.getData(),
          state.getRunbook().requiredVariables());
      String decision =
          ChatClient.builder(chatModel)
              .build()
              .prompt(prompt)
              .tools(authenticationTool, n8NTool, zendeskTool)
              .call()
              .entity(String.class);
      log.info("{} AI execution completed.", this.getClass().getName());
      log.info("{} AI responded with: {}", this.getClass().getName(), decision);

      return CompletableFuture.completedFuture(decision);
   }
}
