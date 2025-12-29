package com.democracy.democracy_orchestrator.application.services;

import com.democracy.democracy_orchestrator.domain.ports.in.investigationresult.CalculateScoreIn;
import com.democracy.democracy_orchestrator.domain.ports.in.investigationresult.SendInvestigationResultIn;

public interface InvestigationResultService extends SendInvestigationResultIn, CalculateScoreIn {
}
