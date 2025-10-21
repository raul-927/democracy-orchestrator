package com.democracy.democracy_orchestrator.application.usecases.investigation;

import com.democracy.democracy_orchestrator.domain.models.Investigation;
import com.democracy.democracy_orchestrator.domain.ports.in.investigation.SendInvestigationIn;
import com.democracy.democracy_orchestrator.domain.ports.out.InvestigationOut;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

@Component
public class SendInvestigationUseCase implements SendInvestigationIn {

    private final InvestigationOut investigationOut;

    public SendInvestigationUseCase(InvestigationOut investigationOut) {
        this.investigationOut = investigationOut;
    }

    @Override
    public Flux<Investigation> sendInvestigation(Investigation investigation) {
        return investigationOut.sendInvestigation(investigation);
    }
}
