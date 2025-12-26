package com.democracy.democracy_orchestrator.application.usecases.investigationresult;

import com.democracy.democracy_orchestrator.domain.models.InvestigationResult;
import com.democracy.democracy_orchestrator.domain.ports.in.investigationresult.SendInvestigationResultIn;
import com.democracy.democracy_orchestrator.domain.ports.out.InvestigationResultOut;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Component
public class SendInvestigationResultUseCase implements SendInvestigationResultIn {

    private final InvestigationResultOut investigationResultOut;

    public SendInvestigationResultUseCase(InvestigationResultOut investigationResultOut) {
        this.investigationResultOut = investigationResultOut;
    }

    @Override
    public Mono<Integer> sendInvestigationResult(InvestigationResult investigationResult) {
        investigationResult.setInvestigationId(UUID.randomUUID().toString());
        return this.investigationResultOut.sendInvestigationResult(investigationResult);
    }
}
