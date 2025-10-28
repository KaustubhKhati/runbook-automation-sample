package io.github.kaustubhkhati.runbook.automation.model;

import java.io.Serializable;
import java.util.List;

public record Runbook(
    String id,
    String title,
    String description,
    List<String> tags,
    String executionSteps,
    List<String> requiredVariables
) implements Serializable {
}
