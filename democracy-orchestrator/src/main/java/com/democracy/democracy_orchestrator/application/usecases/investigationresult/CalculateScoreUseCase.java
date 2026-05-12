package com.democracy.democracy_orchestrator.application.usecases.investigationresult;

import com.democracy.democracy_orchestrator.domain.models.*;
import com.democracy.democracy_orchestrator.domain.ports.in.investigationresult.CalculateScoreIn;
import com.democracy.democracy_orchestrator.domain.ports.out.InvestigationResultOut;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Component
public class CalculateScoreUseCase implements CalculateScoreIn {

    private static final String  NO_CRIMINAL_RECORD= "Se observa que no contiene registros de antecedentes delictivos";
    private static final String POSITIVE_CRIMINAL_RECORD = "Se investiga y se obtiene que existen registro de antecedentes delictivos";
    private static final String NOT_CALIFICATIONS_TITLE = "/ Se verifica que no contiene Calificaciones en sus diplomas";
    private static final String YES_CALIFICATIONS_TITLE = " / Se verifica y se aprueban las calificaciones de sus diplomas";
    private static final String DOCUMENTATION_NO_OK= " / Se verifica que no contiene documentación";
    private static final String DOCUMENTATION_OK = " / Se verifica y se aprueban los documentos presentados";
    private static final String SCORE_NO_OK = "/ El puntaje no supera el límite mínimo necesario";

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
            observation = NO_CRIMINAL_RECORD;
        }
        else{
            observation = POSITIVE_CRIMINAL_RECORD;
            investigation.getCriminalRecords().forEach(cr ->{
                if(!cr.getCriminalRecordId().isEmpty()){
                    score--;
                    investigationResult.setIsApprove(false);
                }
            });
        }
        if(investigation.getQualifications() == null || investigation.getQualifications().isEmpty()){
            observation = observation.concat(NOT_CALIFICATIONS_TITLE);
        }
        else{
            observation = observation.concat(YES_CALIFICATIONS_TITLE);
            investigation.getQualifications().forEach( q ->{
                if(q.getApproved()){
                    score ++;
                }
            });
        }
        if(investigation.getDocuments()!=null && !investigation.getDocuments().isEmpty()){
            observation = observation.concat(DOCUMENTATION_OK);
            investigation.getDocuments().forEach(d ->{
                if(d.isDocumentApproved()){
                    score ++;
                }
            });
        }else {
            observation = observation.concat(DOCUMENTATION_NO_OK);
        }
        if(score <=0){
            investigationResult.setIsApprove(false);
            observation = observation.concat(SCORE_NO_OK);
        }
        investigationResult.setObservation(observation);
        investigationResult.setScore(score);
        return investigationResultOut.calculateScore(investigationResult);
    }
}
