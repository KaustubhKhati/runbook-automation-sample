package io.github.kaustubhkhati.runbook.automation.repo;

import jakarta.annotation.PostConstruct;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class SchemaInitializer {
   final JdbcTemplate jdbc;

   public SchemaInitializer(JdbcTemplate jdbc) {
      this.jdbc = jdbc;
   }

   @PostConstruct
   public void ensure() {
      jdbc.execute(
          "CREATE TABLE IF NOT EXISTS runbooks (id TEXT PRIMARY KEY, title TEXT, description TEXT, tags TEXT, steps TEXT, created_at TEXT)");

      jdbc.execute(
          "CREATE TABLE IF NOT EXISTS processed_tickets (id TEXT PRIMARY KEY, processed_at TEXT)");

      // Create index for faster cleanup queries
      jdbc.execute(
          "CREATE INDEX IF NOT EXISTS idx_processed_at ON processed_tickets(processed_at)");
   }
}
