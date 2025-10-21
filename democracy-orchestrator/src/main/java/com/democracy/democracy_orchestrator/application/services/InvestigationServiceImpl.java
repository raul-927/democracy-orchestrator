package com.democracy.democracy_orchestrator.application.services;

import com.democracy.democracy_orchestrator.domain.models.Investigation;
import com.democracy.democracy_orchestrator.domain.ports.in.investigation.SelectInvestigationIn;
import com.democracy.democracy_orchestrator.domain.ports.in.investigation.SendInvestigationIn;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;


@Service
public class InvestigationServiceImpl implements InvestigationService{

    private final SelectInvestigationIn selectInvestigation;
    private final SendInvestigationIn sendInvestigationIn;

    public InvestigationServiceImpl(SelectInvestigationIn selectInvestigation, SendInvestigationIn sendInvestigationIn) {
        this.selectInvestigation = selectInvestigation;
        this.sendInvestigationIn = sendInvestigationIn;
    }

    @Override
    public Flux<Investigation> selectInvestigation(Investigation investigation) {
        return this.selectInvestigation.selectInvestigation(investigation);
    }

    @Override
    public Flux<Investigation> sendInvestigation(Investigation investigation) {
        return sendInvestigationIn.sendInvestigation(investigation);
    }
}
