package io.github.kaustubhkhati.runbook.automation.model;

import java.io.Serializable;
import java.util.Map;

public record ExtractedData(Map<String, String> data)
    implements Serializable {
}
