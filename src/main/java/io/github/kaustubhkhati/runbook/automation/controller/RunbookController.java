package io.github.kaustubhkhati.runbook.automation.controller;

import io.github.kaustubhkhati.runbook.automation.cron.ZendeskPollJob;
import io.github.kaustubhkhati.runbook.automation.service.RunbookService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/runbooks")
public class RunbookController {
   private final RunbookService service;
   private final ZendeskPollJob zendeskPollJob;

   public RunbookController(RunbookService service, ZendeskPollJob zendeskPollJob) {
      this.service = service;
      this.zendeskPollJob = zendeskPollJob;
   }

   @GetMapping("/{id}")
   public ResponseEntity<?> get(@PathVariable String id) {
      return service
          .findById(id)
          .<ResponseEntity<?>>map(ResponseEntity::ok)
          .orElse(ResponseEntity.notFound().build());
   }

   @PostMapping("/poll-zendesk")
   public ResponseEntity<?> pollZendesk() {
      zendeskPollJob.run();
      return ResponseEntity.accepted().build();
   }

   @PostMapping("/ingest")
   public ResponseEntity<String> uploadMarkdownRunbook(@RequestParam("file") MultipartFile file) {
      try {
         String markdownContent = new String(file.getBytes(), java.nio.charset.StandardCharsets.UTF_8);
         String id = service.ingestMarkdown(markdownContent);
         return ResponseEntity.ok(id);
      } catch (Exception e) {
         return ResponseEntity.status(500).body("Error processing markdown runbook: " + e.getMessage());
      }
   }
}