package com.democracy.democracy_orchestrator.domain.ports.in.investigation;

import com.democracy.democracy_orchestrator.domain.models.Investigation;
import reactor.core.publisher.Flux;

public interface SelectInvestigationIn {
    Flux<Investigation> selectInvestigation(Investigation investigation);
}
