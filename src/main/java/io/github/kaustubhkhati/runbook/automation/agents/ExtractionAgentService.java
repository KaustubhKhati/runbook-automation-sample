package io.github.kaustubhkhati.runbook.automation.agents;

import io.github.kaustubhkhati.runbook.automation.graph.RunbookState;
import io.github.kaustubhkhati.runbook.automation.model.ExtractedData;
import io.github.kaustubhkhati.runbook.automation.model.ZendeskTicket;
import io.github.kaustubhkhati.runbook.automation.util.Jsons;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.bsc.langgraph4j.action.AsyncNodeAction;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.SystemPromptTemplate;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ExtractionAgentService implements AsyncNodeAction<RunbookState> {

   private static final String systemText =
       """
           You are a data extraction agent. You are responsible for looking at the data provided in a zendesk ticket and get relevant information.
           This information can include orderId, traceId, splunk urls etc.
           Extract them in the structured output.
           """;
   private final Jsons jsons;
   private final Message systemMessage;
   private final ChatClient chatClient;

   public ExtractionAgentService(Jsons jsons, ChatModel chatModel) {
      this.jsons = jsons;
      systemMessage = new SystemPromptTemplate(systemText).createMessage();
      chatClient = ChatClient.builder(chatModel).build();
   }

   @Override
   public CompletableFuture<Map<String, Object>> apply(RunbookState state) {
      ZendeskTicket zendeskTicket = state.getZendeskTicket();
      UserMessage userMessage = new UserMessage(zendeskTicket.description());
      Prompt prompt = new Prompt(List.of(systemMessage, userMessage));
      log.info("{} Invoking AI", this.getClass().getName());

      ExtractedData extracted = chatClient.prompt(prompt).call().entity(ExtractedData.class);
      log.info("{} AI execution completed.", this.getClass().getName());
      log.info("{} AI responded with: {}", this.getClass().getName(), jsons.write(extracted));

      if (ObjectUtils.isEmpty(extracted)) {
         return CompletableFuture.completedFuture(Map.of(RunbookState.KEY_STATUS, "NO_DATA_FOUND"));
      }
      return CompletableFuture.completedFuture(Map.of(RunbookState.KEY_DATA, extracted));
   }
}
