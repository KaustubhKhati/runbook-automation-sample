package io.github.kaustubhkhati.runbook.automation.service;

import io.github.kaustubhkhati.runbook.automation.model.Runbook;
import io.github.kaustubhkhati.runbook.automation.model.SearchResult;
import io.micrometer.core.annotation.Timed;
import java.util.List;
import java.util.Optional;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

@Service
public class QdrantService {

   private final VectorStore vectorStore;

   public QdrantService(VectorStore vectorStore) {
      this.vectorStore = vectorStore;
   }

   @Timed(value = "vector.upsert", histogram = true)
   public void upsertMarkdownRunbook(Runbook runbook) {
      String embeddingText =
          (runbook.description() + " " + String.join(" ", runbook.tags())).trim();

      Document doc = new Document(embeddingText);
      doc.getMetadata().put("id", runbook.id());
      doc.getMetadata().put("title", runbook.title());
      doc.getMetadata().put("tags", String.join(",", runbook.tags()));

      vectorStore.add(List.of(doc));
   }

   @Timed(value = "vector.search", histogram = true)
   public Optional<SearchResult> semanticSearchBest(String query) {
      List<Document> results =
          vectorStore.similaritySearch(SearchRequest.builder().query(query).similarityThreshold(0.85).topK(1).build());
      if (results == null || results.isEmpty()) {
         return Optional.empty();
      }
      Document topDoc = results.getFirst();
      String runbookId = topDoc.getMetadata().get("id").toString();
      double score = topDoc.getScore();
      return Optional.of(new SearchResult(runbookId, score));
   }
}