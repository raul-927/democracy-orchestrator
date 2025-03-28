package com.democracy.democracy_orchestrator.domain.ports.in.investigation;

import com.democracy.democracy_orchestrator.domain.models.Investigation;

public interface SelectInvestigationIn {
    Investigation selectInvestigation(Investigation investigation);
}
