package com.democracy.democracy_orchestrator.application.usecases.investigationresult;

import com.democracy.democracy_orchestrator.domain.models.*;
import com.democracy.democracy_orchestrator.domain.ports.in.investigationresult.CalculateScoreIn;
import com.democracy.democracy_orchestrator.domain.ports.out.InvestigationResultOut;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

@Component
public class CalculateScoreUseCase implements CalculateScoreIn {
    private int score;
    private final InvestigationResultOut investigationResultOut;

    public CalculateScoreUseCase(InvestigationResultOut investigationResultOut) {
        this.investigationResultOut = investigationResultOut;
    }

    @Override
    public Mono<Integer> calculateScore(Investigation investigation) {
        InvestigationResult investigationResult = new InvestigationResult();
        investigationResult.setInvestigationId(UUID.randomUUID().toString());
        investigationResult.setPersonId(investigation.getPerson().getPersonId());
        investigationResult.setInvestigationId(investigation.getInvestigationId());
        investigationResult.setCedula(investigation.getPerson().getCedula());
        investigationResult.setObservation(investigation.getObservation());
        investigationResult.setIsApprove(investigation.getQualifications().get(0).isApproved());
        List<CriminalRecord> criminalRecords = investigation.getCriminalRecords();
        List<Qualification> qualifications = investigation.getQualifications();
        score = 0;
        qualifications.forEach( q ->{
            if(q.isApproved()){
                score ++;
            }
        });
        System.out.println("SCORE++: "+score);
        criminalRecords.forEach(cr ->{
            if(cr.getCriminalRecordId()!=null){
                score--;
            }
        });
        System.out.println("SCORE--: "+score);
        investigationResult.setScore(score);
        System.out.println("SCORE: "+score);
        return investigationResultOut.calculateScore(investigationResult);
    }
}
