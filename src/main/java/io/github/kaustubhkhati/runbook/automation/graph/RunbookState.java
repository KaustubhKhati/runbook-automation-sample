package io.github.kaustubhkhati.runbook.automation.graph;

import io.github.kaustubhkhati.runbook.automation.model.ExtractedData;
import io.github.kaustubhkhati.runbook.automation.model.Runbook;
import io.github.kaustubhkhati.runbook.automation.model.ZendeskTicket;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.bsc.langgraph4j.state.AgentState;

public class RunbookState extends AgentState {

   public static final String KEY_TICKET = "ticket";
   public static final String KEY_RUNBOOK = "runbook";
   public static final String KEY_PLAN = "executionPlan";
   public static final String KEY_RESULT = "executionResult";
   public static final String KEY_STATUS = "executionStatus";
   public static final String KEY_DATA = "data";

   public RunbookState(Map<String, Object> initData) {
      super(initData);
   }

   public static RunbookState of(ZendeskTicket zendeskTicket) {
      Map<String, Object> init = new HashMap<>();
      init.put(KEY_TICKET, zendeskTicket);
      return new RunbookState(init);
   }

   public Runbook getRunbook() {
      Optional<Object> optionalRunbook = value(KEY_RUNBOOK);
      return (Runbook) optionalRunbook.orElse(null);
   }

   public ExtractedData getData() {
      return (ExtractedData) value(KEY_DATA).get();
   }

   public ZendeskTicket getZendeskTicket() {
      return (ZendeskTicket) value(KEY_TICKET).get();
   }
}
