package io.github.kaustubhkhati.runbook.automation.agents;

import io.github.kaustubhkhati.runbook.automation.graph.RunbookState;
import io.github.kaustubhkhati.runbook.automation.model.Runbook;
import io.github.kaustubhkhati.runbook.automation.model.ZendeskTicket;
import io.github.kaustubhkhati.runbook.automation.service.RunbookService;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import lombok.extern.slf4j.Slf4j;
import org.bsc.langgraph4j.action.AsyncNodeAction;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class RunbookMatchingAgentService implements AsyncNodeAction<RunbookState> {

   private final RunbookService runbookService;

   public RunbookMatchingAgentService(RunbookService runbookService) {
      this.runbookService = runbookService;
   }

   @Override
   public CompletableFuture<Map<String, Object>> apply(RunbookState state) {
      log.info("{} Invoking AI with state", this.getClass().getName());
      ZendeskTicket zendeskTicket = state.getZendeskTicket();
      Optional<Runbook> matchRunbook = runbookService.matchRunbook(zendeskTicket);
      if (matchRunbook.isPresent()) {
         log.info("{} AI responded with: {}", this.getClass().getName(), matchRunbook.get().id());
      } else {
         log.info("{} No matching runbook found.", this.getClass().getName());
      }
      return matchRunbook
          .<CompletableFuture<Map<String, Object>>>map(
              runbook -> CompletableFuture.completedFuture(Map.of(RunbookState.KEY_RUNBOOK, runbook)))
          .orElseGet(() -> CompletableFuture.completedFuture(Map.of()));
   }
}
