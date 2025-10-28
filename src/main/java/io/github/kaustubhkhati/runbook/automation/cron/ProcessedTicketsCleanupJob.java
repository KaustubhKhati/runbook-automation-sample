package io.github.kaustubhkhati.runbook.automation.cron;

import io.github.kaustubhkhati.runbook.automation.repo.ProcessedTicketRepository;
import io.micrometer.core.annotation.Timed;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ProcessedTicketsCleanupJob {

   private final ProcessedTicketRepository repository;

   @Value("${jobs.cleanup.retentionDays:30}")
   private int retentionDays;

   public ProcessedTicketsCleanupJob(ProcessedTicketRepository repository) {
      this.repository = repository;
   }

   @Scheduled(cron = "${jobs.cleanup.cron:0 0 2 * * *}") // Default: runs daily at 2AM
   @Timed(value = "job.processedTickets.cleanup", histogram = true)
   public void run() {
      log.info("Starting processed tickets cleanup job... RetentionDays={}", retentionDays);
      try {
         int deletedCount = repository.cleanupOlderThanDays(retentionDays);
         log.info("Cleanup job removed {} old processed ticket records", deletedCount);
      } catch (Exception e) {
         log.error("Processed tickets cleanup job failed: {}", e.getMessage(), e);
      }
      log.info("Processed tickets cleanup job finished.");
   }
}