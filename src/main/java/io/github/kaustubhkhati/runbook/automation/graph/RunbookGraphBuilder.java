package io.github.kaustubhkhati.runbook.automation.graph;

import static org.bsc.langgraph4j.StateGraph.END;
import static org.bsc.langgraph4j.StateGraph.START;

import io.github.kaustubhkhati.runbook.automation.agents.ExecutionAgentService;
import io.github.kaustubhkhati.runbook.automation.agents.ExtractionAgentService;
import io.github.kaustubhkhati.runbook.automation.agents.FeasibilityAgentService;
import io.github.kaustubhkhati.runbook.automation.agents.PlanningAgentService;
import io.github.kaustubhkhati.runbook.automation.agents.RunbookMatchingAgentService;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import org.apache.commons.lang3.ObjectUtils;
import org.bsc.langgraph4j.CompiledGraph;
import org.bsc.langgraph4j.GraphStateException;
import org.bsc.langgraph4j.StateGraph;
import org.bsc.langgraph4j.action.AsyncEdgeAction;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

@Component
public class RunbookGraphBuilder {

   private final ExtractionAgentService extractionAgentService;
   private final RunbookMatchingAgentService matchingAgentService;
   private final FeasibilityAgentService feasibilityAgentService;
   private final PlanningAgentService planningAgentService;
   private final ExecutionAgentService executionAgentService;

   public RunbookGraphBuilder(
       ExtractionAgentService extractionAgentService,
       RunbookMatchingAgentService matchingAgentService,
       FeasibilityAgentService feasibilityAgentService,
       PlanningAgentService planningAgentService,
       ExecutionAgentService executionAgentService) {
      this.extractionAgentService = extractionAgentService;
      this.matchingAgentService = matchingAgentService;
      this.feasibilityAgentService = feasibilityAgentService;
      this.planningAgentService = planningAgentService;
      this.executionAgentService = executionAgentService;
   }

   @NotNull
   private static AsyncEdgeAction<RunbookState> getRunbookStateAsyncEdgeAction() {
      return state ->
          ObjectUtils.isEmpty(state.getRunbook())
              ? CompletableFuture.completedFuture(Boolean.FALSE.toString())
              : CompletableFuture.completedFuture(Boolean.TRUE.toString());
   }

   public CompiledGraph<RunbookState> build() throws GraphStateException {
      StateGraph<RunbookState> stateGraph = new StateGraph<>(RunbookState::new);

      // Add Nodes
      stateGraph.addNode("extract", extractionAgentService);
      stateGraph.addNode("matchWithRunbook", matchingAgentService);
      stateGraph.addNode("planning", planningAgentService);
      stateGraph.addNode("execution", executionAgentService);

      // Build Graph
      stateGraph.addEdge(START, "matchWithRunbook");
      stateGraph.addConditionalEdges(
          "matchWithRunbook",
          getRunbookStateAsyncEdgeAction(),
          Map.of("true", "extract", "false", END));
      stateGraph.addConditionalEdges(
          "extract",
          feasibilityAgentService,
          Map.of("sufficient", "planning", "insufficient", END));
      stateGraph.addEdge("planning", "execution");
      stateGraph.addEdge("execution", END);
      return stateGraph.compile();
   }
}
