package io.github.kaustubhkhati.runbook.automation.model.response;

import io.github.kaustubhkhati.runbook.automation.model.ZendeskTicket;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ZendeskTicketsResponse {
   private List<ZendeskTicket> tickets;
}
