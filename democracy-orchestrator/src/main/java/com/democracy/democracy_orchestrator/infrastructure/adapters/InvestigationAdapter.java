package com.democracy.democracy_orchestrator.infrastructure.adapters;

import com.democracy.democracy_orchestrator.domain.models.Investigation;
import com.democracy.democracy_orchestrator.domain.ports.out.InvestigationOut;
import org.springframework.stereotype.Component;

@Component
public class InvestigationAdapter implements InvestigationOut {



    @Override
    public Investigation selectInvestigation(Investigation investigation) {
        return null;
    }
}
