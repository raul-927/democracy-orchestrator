package com.democracy.democracy_orchestrator.domain.ports.out;

import com.democracy.democracy_orchestrator.domain.models.Investigation;
import com.democracy.democracy_orchestrator.domain.models.InvestigationResult;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface InvestigationResultOut {
    Mono<Integer> calculateScore(InvestigationResult investigationResult);
    Mono<Integer> sendInvestigationResult(InvestigationResult investigationResult);
}
