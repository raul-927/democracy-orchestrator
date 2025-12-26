package com.democracy.democracy_orchestrator.application.services;

import com.democracy.democracy_orchestrator.domain.models.InvestigationResult;
import com.democracy.democracy_orchestrator.domain.ports.in.investigationresult.SendInvestigationResultIn;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class InvestigationResultServiceImpl implements InvestigationResultService{

    private final SendInvestigationResultIn sendInvestigationResultIn;

    public InvestigationResultServiceImpl(SendInvestigationResultIn sendInvestigationResultIn) {
        this.sendInvestigationResultIn = sendInvestigationResultIn;
    }

    @Override
    public Mono<Integer> sendInvestigationResult(InvestigationResult investigationResult) {
        return this.sendInvestigationResultIn.sendInvestigationResult(investigationResult);
    }
}
