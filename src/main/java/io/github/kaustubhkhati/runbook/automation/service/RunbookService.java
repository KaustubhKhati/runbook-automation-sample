package io.github.kaustubhkhati.runbook.automation.service;

import io.github.kaustubhkhati.runbook.automation.model.Runbook;
import io.github.kaustubhkhati.runbook.automation.model.SearchResult;
import io.github.kaustubhkhati.runbook.automation.model.ZendeskTicket;
import io.github.kaustubhkhati.runbook.automation.repo.RunbookRepository;
import io.github.kaustubhkhati.runbook.automation.util.MarkdownRunbookParser;
import io.micrometer.core.annotation.Timed;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class RunbookService {
   private final RunbookRepository repo;
   private final QdrantService qdrantService;

   public RunbookService(RunbookRepository repo, QdrantService qdrant) {
      this.repo = repo;
      this.qdrantService = qdrant;
   }

   /**
    * Ingests a Markdown runbook:
    * - Extracts runbook ID from the Markdown file.
    * - Upserts into Qdrant vector store.
    * - Stores the raw Markdown in the database.
    *
    * @param markdown raw Markdown contents
    * @return runbook ID extracted from file
    */
   @Timed(value = "runbook.ingest", histogram = true)
   public String ingestMarkdown(String markdown) {
      Runbook rb = MarkdownRunbookParser.parse(markdown);
      String id = rb.id();

      qdrantService.upsertMarkdownRunbook(rb); // embedding + vector store
      repo.upsert(id, markdown); // raw markdown storage in DB
      return id;
   }

   public Optional<Runbook> findById(String id) {
      // Return parsed Runbook from stored markdown
      return repo.findMarkdownById(id)
          .map(MarkdownRunbookParser::parse);
   }

   public Optional<Runbook> matchRunbook(ZendeskTicket zendeskTicket) {
      String query = zendeskTicket.subject() + " " + zendeskTicket.description();
      Optional<SearchResult> matched = qdrantService.semanticSearchBest(query);
      return matched.flatMap(searchResult -> findById(searchResult.runbookId()));
   }
}