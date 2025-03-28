package com.democracy.democracy_orchestrator.domain.ports.out;

import com.democracy.democracy_orchestrator.domain.models.Investigation;

public interface InvestigationOut {
    Investigation selectInvestigation(Investigation investigation);
}
