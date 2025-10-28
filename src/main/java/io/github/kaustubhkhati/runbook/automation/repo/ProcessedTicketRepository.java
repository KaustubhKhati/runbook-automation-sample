package io.github.kaustubhkhati.runbook.automation.repo;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class ProcessedTicketRepository {

   private final NamedParameterJdbcTemplate namedJdbc;

   public ProcessedTicketRepository(NamedParameterJdbcTemplate namedJdbc) {
      this.namedJdbc = namedJdbc;
   }

   public Set<String> findProcessedIds(List<String> ids) {
      if (ids == null || ids.isEmpty()) {
         return Set.of();
      }
      String sql = "SELECT id FROM processed_tickets WHERE id IN (:ids)";
      MapSqlParameterSource params = new MapSqlParameterSource("ids", ids);

      return new HashSet<>(
          namedJdbc.query(sql, params, (rs, rowNum) -> rs.getString("id"))
      );
   }

   public void save(Long ticketId) {
      String sql =
          "INSERT INTO processed_tickets (id, processed_at) VALUES (:id, :processedAt)";
      MapSqlParameterSource params =
          new MapSqlParameterSource()
              .addValue("id", String.valueOf(ticketId))
              .addValue("processedAt", LocalDateTime.now().toString());

      namedJdbc.update(sql, params);
   }

   public int cleanupOlderThanDays(int days) {
      String sql =
          "DELETE FROM processed_tickets WHERE processed_at < datetime('now', :daysAgo)";
      MapSqlParameterSource params =
          new MapSqlParameterSource("daysAgo", String.format("-%d days", days));
      return namedJdbc.update(sql, params);
   }
}