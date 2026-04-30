package com.democracy.democracy_orchestrator.application.usecases.investigationresult;

import com.democracy.democracy_orchestrator.domain.models.*;
import com.democracy.democracy_orchestrator.domain.ports.in.investigationresult.CalculateScoreIn;
import com.democracy.democracy_orchestrator.domain.ports.out.InvestigationResultOut;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

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
        investigationResult.setIsApprove(true);
        String observation = "";
        score = 0;
        if(investigation.getCriminalRecords() == null || investigation.getCriminalRecords().isEmpty()){
            observation = "Se observa que no contiene registros de antecedentes delictivos";
        }
        else{
            observation = "Se investiga y se obtiene que existen registro de antecedentes delictivos";
            investigation.getCriminalRecords().forEach(cr ->{
                if(!cr.getCriminalRecordId().isEmpty()){
                    score--;
                    investigationResult.setIsApprove(false);
                }
            });
        }
        if(investigation.getQualifications() == null || investigation.getQualifications().isEmpty()){
            observation = observation.concat(" / Se verifica que no contiene Calificaciones en sus diplomas");
        }
        else{
            observation = observation.concat(" / Se verifica y se aprueban las calificaciones de sus diplomas");
            investigation.getQualifications().forEach( q ->{
                if(q.getApproved()){
                    score ++;
                }
            });
        }
        if(score <=0){
            investigationResult.setIsApprove(false);
            observation = observation.concat("/ El puntaje no supera el límite mínimo necesario");
        }
        investigationResult.setObservation(observation);
        investigationResult.setScore(score);
        return investigationResultOut.calculateScore(investigationResult);
    }
}
