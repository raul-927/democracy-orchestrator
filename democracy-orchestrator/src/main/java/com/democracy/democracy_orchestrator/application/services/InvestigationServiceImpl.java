package com.democracy.democracy_orchestrator.application.services;

import com.democracy.democracy_orchestrator.domain.models.Investigation;
import com.democracy.democracy_orchestrator.domain.ports.in.investigation.SelectInvestigationIn;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;


@Service
public class InvestigationServiceImpl implements InvestigationService{

    private final SelectInvestigationIn selectInvestigation;

    public InvestigationServiceImpl(SelectInvestigationIn selectInvestigation) {
        this.selectInvestigation = selectInvestigation;
    }

    @Override
    public Flux<Investigation> selectInvestigation(Investigation investigation) {
        return this.selectInvestigation.selectInvestigation(investigation);
    }
}
