package com.democracy.democracy_orchestrator.domain.ports.in.investigationresult;

import com.democracy.democracy_orchestrator.domain.models.InvestigationResult;
import reactor.core.publisher.Mono;

public interface SendInvestigationResultIn {
    Mono<Integer> sendInvestigationResult(InvestigationResult investigationResult);
}
