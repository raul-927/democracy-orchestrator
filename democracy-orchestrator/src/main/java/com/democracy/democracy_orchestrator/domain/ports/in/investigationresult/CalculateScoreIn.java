package com.democracy.democracy_orchestrator.domain.ports.in.investigationresult;

import com.democracy.democracy_orchestrator.domain.models.Investigation;
import com.democracy.democracy_orchestrator.domain.models.InvestigationResult;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface CalculateScoreIn {
    Mono<Integer> calculateScore(Investigation investigation);
}
