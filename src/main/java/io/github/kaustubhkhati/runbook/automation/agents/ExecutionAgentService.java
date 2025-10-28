package io.github.kaustubhkhati.runbook.automation.agents;

import static io.github.kaustubhkhati.runbook.automation.graph.RunbookState.KEY_PLAN;

import io.github.kaustubhkhati.runbook.automation.graph.RunbookState;
import io.github.kaustubhkhati.runbook.automation.tools.AuthenticationTool;
import io.github.kaustubhkhati.runbook.automation.tools.N8NTool;
import io.github.kaustubhkhati.runbook.automation.tools.ZendeskTool;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import lombok.extern.slf4j.Slf4j;
import org.bsc.langgraph4j.action.AsyncNodeAction;
import org.jetbrains.annotations.NotNull;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.Generation;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.SystemPromptTemplate;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ExecutionAgentService implements AsyncNodeAction<RunbookState> {

   private final ChatModel chatModel;
   private final AuthenticationTool authenticationTool;
   private final N8NTool n8NTool;
   private final ZendeskTool zendeskTool;

   public ExecutionAgentService(
       ChatModel chatModel, AuthenticationTool authenticationTool, N8NTool n8NTool, ZendeskTool zendeskTool) {
      this.chatModel = chatModel;
      this.authenticationTool = authenticationTool;
      this.n8NTool = n8NTool;
      this.zendeskTool = zendeskTool;
   }

   @NotNull
   private static Message getSystemPromptTemplate() {
      String systemText =
          """
              You are a AI Agent executor, You will be given a list of steps to execute in sequence, use the tools at your disposal to complete the steps provided.
              If the tools cannot be executed at this time, please return to the planning stage to gather more data.
              If there were no errors in the execution, return a message "EXECUTION_SUCCESS".
              else if there were errors in the execution or you failed to get expected response from tools return a message "EXECUTION_FAILED" with reason as a JSON response.
              """;

      return new SystemPromptTemplate(systemText).createMessage();
   }

   @Override
   public CompletableFuture<Map<String, Object>> apply(RunbookState state) {
      // Build AI prompt — no need to embed the tool list in text anymore
      Message systemMessage = getSystemPromptTemplate();
      UserMessage userMessage = new UserMessage(state.value(KEY_PLAN).get().toString());
      Prompt prompt = new Prompt(List.of(userMessage, systemMessage));

      log.info("{} Invoking AI", this.getClass().getName());
      // 🧠 Let AI run tools directly
      List<Generation> executionResult =
          ChatClient.builder(chatModel)
              .build()
              .prompt(prompt)
              .tools(
                  authenticationTool, n8NTool, zendeskTool) // 🔹 executable tool definitions given to LLM
              .call()
              .chatResponse()
              .getResults(); // Could be Map, List, or String depending on AI return
      log.info("{} AI execution completed.", this.getClass().getName());
      log.info("{} AI responded with: {}", this.getClass().getName(), executionResult.getFirst().toString());
      // Store results & status in RunbookState
      return CompletableFuture.completedFuture(
          Map.of(RunbookState.KEY_STATUS, "COMPLETED"));
   }
}
