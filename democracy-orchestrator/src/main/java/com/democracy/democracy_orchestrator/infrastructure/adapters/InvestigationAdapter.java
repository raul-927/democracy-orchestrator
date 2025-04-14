package com.democracy.democracy_orchestrator.infrastructure.adapters;

import com.democracy.democracy_orchestrator.domain.models.Investigation;
import com.democracy.democracy_orchestrator.domain.ports.out.InvestigationOut;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

@Component
public class InvestigationAdapter implements InvestigationOut {



    @Override
    public Flux<Investigation> selectInvestigation(Investigation investigation) {
        return null;
    }
}
