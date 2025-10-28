package io.github.kaustubhkhati.runbook.automation.repo;

import java.util.Optional;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class RunbookRepository {
   private final NamedParameterJdbcTemplate namedJdbc;

   public RunbookRepository(NamedParameterJdbcTemplate namedJdbc) {
      this.namedJdbc = namedJdbc;
   }

   public void upsert(String id, String markdown) {
      MapSqlParameterSource params = new MapSqlParameterSource()
          .addValue("id", id)
          .addValue("markdown", markdown);

      namedJdbc.update(
          "INSERT INTO runbooks (id, markdown) VALUES (:id, :markdown) " +
              "ON CONFLICT(id) DO UPDATE SET markdown = excluded.markdown",
          params
      );
   }

   public Optional<String> findMarkdownById(String id) {
      MapSqlParameterSource params = new MapSqlParameterSource()
          .addValue("id", id);

      return namedJdbc.query(
          "SELECT markdown FROM runbooks WHERE id = :id",
          params,
          rs -> rs.next() ? Optional.of(rs.getString("markdown")) : Optional.empty()
      );
   }
}