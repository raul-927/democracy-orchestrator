package com.democracy.democracy_orchestrator.application.services;

import com.democracy.democracy_orchestrator.domain.ports.in.investigation.SelectInvestigationIn;
import com.democracy.democracy_orchestrator.domain.ports.in.investigation.SendInvestigationIn;

public interface InvestigationService extends SelectInvestigationIn, SendInvestigationIn {
}
