package com.democracy.democracy_orchestrator.domain.ports.out;

import com.democracy.democracy_orchestrator.domain.models.Investigation;
import reactor.core.publisher.Flux;

public interface InvestigationOut {
    Flux<Investigation> selectInvestigation(Investigation investigation);
}
