package com.democracy.democracy_orchestrator.application.services;

import com.democracy.democracy_orchestrator.domain.models.Investigation;
import com.democracy.democracy_orchestrator.domain.models.InvestigationResult;
import com.democracy.democracy_orchestrator.domain.ports.in.investigationresult.CalculateScoreIn;
import com.democracy.democracy_orchestrator.domain.ports.in.investigationresult.SendInvestigationResultIn;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class InvestigationResultServiceImpl implements InvestigationResultService{

    private final SendInvestigationResultIn sendInvestigationResultIn;
    private final CalculateScoreIn calculateScoreIn;

    public InvestigationResultServiceImpl(SendInvestigationResultIn sendInvestigationResultIn, CalculateScoreIn calculateScoreIn) {
        this.sendInvestigationResultIn = sendInvestigationResultIn;
        this.calculateScoreIn = calculateScoreIn;
    }

    @Override
    public Mono<Integer> sendInvestigationResult(InvestigationResult investigationResult) {
        return this.sendInvestigationResultIn.sendInvestigationResult(investigationResult);
    }

    @Override
    public Mono<Integer> calculateScore(Investigation investigation) {
        return calculateScoreIn.calculateScore(investigation);
    }
}
