package io.github.kaustubhkhati.runbook.automation.model.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Map;

/**
 * Represents one event returned from Splunk's /search/v2/jobs/export endpoint in JSON mode.
 */
public record SplunkExportSearchResponse(
    /**
     * Whether this is a preview event (before search completes).
     */
    @JsonProperty("preview") boolean preview,

    /**
     * Map of result fields to their values for this event.
     */
    @JsonProperty("result") Map<String, Object> result
) {
}
