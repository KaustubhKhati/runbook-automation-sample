package io.github.kaustubhkhati.runbook.automation.cron;

import io.github.kaustubhkhati.runbook.automation.graph.RunbookGraphBuilder;
import io.github.kaustubhkhati.runbook.automation.graph.RunbookState;
import io.github.kaustubhkhati.runbook.automation.model.ZendeskTicket;
import io.github.kaustubhkhati.runbook.automation.repo.ProcessedTicketRepository;
import io.github.kaustubhkhati.runbook.automation.service.AuthenticationService;
import io.github.kaustubhkhati.runbook.automation.service.ZendeskService;
import io.micrometer.core.annotation.Timed;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.bsc.langgraph4j.CompiledGraph;
import org.bsc.langgraph4j.GraphStateException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ZendeskPollJob {

   private final ZendeskService zendesk;
   private final CompiledGraph<RunbookState> runbookGraph;
   private final AuthenticationService authentication;
   private final ProcessedTicketRepository processedRepo;

   public ZendeskPollJob(
       ZendeskService zendesk,
       RunbookGraphBuilder graphBuilder,
       AuthenticationService authentication,
       ProcessedTicketRepository processedRepo)
       throws GraphStateException {
      this.zendesk = zendesk;
      // Build the graph once at startup
      this.runbookGraph = graphBuilder.build();
      this.authentication = authentication;
      this.processedRepo = processedRepo;
   }

   @Scheduled(cron = "${jobs.zendeskPoll.cron}")
   @Timed(value = "job.zendesk.poll", histogram = true)
   public void run() {
      log.info("Starting Zendesk poll job...");
      try {
         String token = authentication.getAuthToken();
         List<ZendeskTicket> tickets = zendesk.fetchTicketsFromView(token);

         if (tickets == null || tickets.isEmpty()) {
            log.info("No new tickets found.");
            return;
         }

         log.info("Fetched {} tickets from Zendesk", tickets.size());

         // Batch check for already processed tickets
         List<String> ids = tickets.stream().map(ZendeskTicket::id).map(String::valueOf).toList();
         Set<String> alreadyProcessed = processedRepo.findProcessedIds(ids);

         List<ZendeskTicket> newTickets =
             tickets.stream()
                 .filter(ticket -> !alreadyProcessed.contains(String.valueOf(ticket.id())))
                 .toList();

         if (newTickets.isEmpty()) {
            log.info("All tickets are already processed — skipping run.");
            return;
         }

         log.info("Processing {} new tickets via Runbook Automation Pipeline", newTickets.size());

         for (ZendeskTicket ticket : newTickets) {
            try {
               RunbookState initialState = RunbookState.of(ticket);
               Optional<RunbookState> finalState = runbookGraph.invoke(initialState.data());

               String status =
                   finalState.flatMap(state -> state.value(RunbookState.KEY_STATUS)).stream()
                       .findFirst()
                       .map(Object::toString)
                       .orElse("UNKNOWN");
               String result = finalState.flatMap(state -> state.value(RunbookState.KEY_RESULT)).toString();

               log.info(
                   "Ticket {} execution completed. Status: {}, Result: {}",
                   ticket.id(),
                   status,
                   result);
            } catch (Exception e) {
               log.error("Failed processing ticket {}: {}", ticket.id(), e.getMessage(), e);
            }
            // Save processed ticket to DB
            processedRepo.save(ticket.id());
         }
      } catch (Exception ex) {
         log.error("Zendesk poll job failed: {}", ex.getMessage(), ex);
      }
      log.info("Zendesk poll job finished.");
   }
}
