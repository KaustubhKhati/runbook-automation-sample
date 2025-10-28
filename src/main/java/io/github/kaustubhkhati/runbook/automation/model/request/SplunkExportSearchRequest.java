package io.github.kaustubhkhati.runbook.automation.model.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Request body for Splunk's /search/v2/jobs/export endpoint.
 * Refer to: https://help.splunk.com/en/splunk-enterprise/rest-api-reference/10.0/search-endpoints
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record SplunkExportSearchRequest(
    /**
     * Required search string.
     * Example: "search index=my_index orderId=12345"
     */
    @JsonProperty("search") String search,

    /**
     * Earliest time for search, e.g., "-24h" or an ISO timestamp.
     */
    @JsonProperty("earliest") String earliest,

    /**
     * Latest time for search.
     */
    @JsonProperty("latest") String latest,

    /**
     * Output mode for returned results.
     * Allowed: "json", "xml", "csv"
     */
    @JsonProperty("output_mode") String outputMode,

    /**
     * Execution mode: normal, oneshot, blocking.
     */
    @JsonProperty("exec_mode") String execMode,

    /**
     * Comma-separated list of fields to return.
     */
    @JsonProperty("field_list") String fieldList,

    /**
     * Max number of results to return.
     */
    @JsonProperty("max_count") Integer maxCount,

    /**
     * Maximum time in seconds to run the search.
     */
    @JsonProperty("search_timeout") Integer searchTimeout,

    /**
     * Time format for returned events.
     */
    @JsonProperty("time_format") String timeFormat
) {

   /**
    * Static builder method for creating simplified requests.
    */
   public static Builder builder(String search) {
      return new Builder(search);
   }

   /**
    * Compact builder record for optional chaining.
    */
   public static final class Builder {
      private final String search;
      private String earliest;
      private String latest;
      private String outputMode;
      private String execMode;
      private String fieldList;
      private Integer maxCount;
      private Integer searchTimeout;
      private String timeFormat;

      private Builder(String search) {
         this.search = search;
      }

      public Builder earliest(String earliest) {
         this.earliest = earliest;
         return this;
      }

      public Builder latest(String latest) {
         this.latest = latest;
         return this;
      }

      public Builder outputMode(String outputMode) {
         this.outputMode = outputMode;
         return this;
      }

      public Builder execMode(String execMode) {
         this.execMode = execMode;
         return this;
      }

      public Builder fieldList(String fieldList) {
         this.fieldList = fieldList;
         return this;
      }

      public Builder maxCount(Integer maxCount) {
         this.maxCount = maxCount;
         return this;
      }

      public Builder searchTimeout(Integer searchTimeout) {
         this.searchTimeout = searchTimeout;
         return this;
      }

      public Builder timeFormat(String timeFormat) {
         this.timeFormat = timeFormat;
         return this;
      }

      public SplunkExportSearchRequest build() {
         return new SplunkExportSearchRequest(
             search,
             earliest,
             latest,
             outputMode,
             execMode,
             fieldList,
             maxCount,
             searchTimeout,
             timeFormat
         );
      }
   }
}