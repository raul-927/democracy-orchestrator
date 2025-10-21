package com.democracy.democracy_orchestrator.domain.ports.in.investigation;

import com.democracy.democracy_orchestrator.domain.models.Investigation;
import reactor.core.publisher.Flux;

public interface SendInvestigationIn {

    Flux<Investigation> sendInvestigation(Investigation investigation);
}
