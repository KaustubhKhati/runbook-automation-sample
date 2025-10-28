package io.github.kaustubhkhati.runbook.automation.agents;

import io.github.kaustubhkhati.runbook.automation.tools.AuthenticationTool;
import io.github.kaustubhkhati.runbook.automation.tools.N8NTool;
import io.github.kaustubhkhati.runbook.automation.tools.ZendeskTool;
import io.github.kaustubhkhati.runbook.automation.graph.RunbookState;
import io.github.kaustubhkhati.runbook.automation.model.Runbook;
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
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.SystemPromptTemplate;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class PlanningAgentService implements AsyncNodeAction<RunbookState> {

   private final ChatModel chatModel;
   private final AuthenticationTool authenticationTool;
   private final N8NTool n8NTool;
   private final ZendeskTool zendeskTool;

   public PlanningAgentService(
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
              You are a AI planning agent, You will make a action plan that can be executed by other AI agent.
              You will receive a guide by the user, You have access to {extracted} data, {zendeskTicket} data.
              Making use of the tools at your disposal generate a prompt that other AI can understand on how to execute the runbook.
              You are not execute any tools, Just generate the prompt.
              """;

      return new SystemPromptTemplate(systemText);
   }

   @Override
   public CompletableFuture<Map<String, Object>> apply(RunbookState state) {
      Message systemMessage = getSystemPromptTemplate().createMessage(
          Map.of("extracted", state.getData(),
              "zendeskTicket", state.getZendeskTicket()));
      Runbook runbook = state.getRunbook();
      UserMessage userMessage = new UserMessage(runbook.executionSteps());
      Prompt prompt = new Prompt(List.of(systemMessage, userMessage));

      log.info("{} Invoking AI with state: {}", this.getClass().getName(), prompt.getContents());
      ChatClient.CallResponseSpec responseSpec =
          ChatClient.builder(chatModel)
              .build()
              .prompt(prompt)
              .tools(authenticationTool, n8NTool, zendeskTool)
              .call();
      String plan = responseSpec.chatResponse().getResult().getOutput().getText();
      log.info("{} AI execution completed.", this.getClass().getName());
      log.info("{} AI responded with: {}", this.getClass().getName(), plan);
      return CompletableFuture.completedFuture(Map.of(RunbookState.KEY_PLAN, plan));
   }
}
