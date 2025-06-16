package com.democracy.democracy_orchestrator.infrastructure.statemachine.listeners;


import com.democracy.democracy_orchestrator.application.services.InvestigationService;
import com.democracy.democracy_orchestrator.application.services.TokenService;
import com.democracy.democracy_orchestrator.domain.models.Document;
import com.democracy.democracy_orchestrator.domain.models.Investigation;
import com.democracy.democracy_orchestrator.domain.models.Person;
import com.democracy.democracy_orchestrator.domain.models.Profession;
import com.democracy.democracy_orchestrator.infrastructure.statemachine.events.PostulationEvents;
import com.democracy.democracy_orchestrator.infrastructure.statemachine.states.PostulationStates;
import com.democracy.democracy_orchestrator.infrastructure.statemachine.trigers.PostulantTrigger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ReactiveHttpOutputMessage;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.annotation.OnStateChanged;
import org.springframework.statemachine.annotation.OnTransition;
import org.springframework.statemachine.annotation.WithStateMachine;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.BodyInserter;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * @description: state listener
 */
//@Component
//@WithStateMachine
//@Transactional
public class OrderStatusListener {
    //@Autowired
    private PostulantTrigger postulantTrigger;

    //@Autowired
    private InvestigationService investigationService;

    //@Autowired
    private WebClient webClient;

    //@Autowired
    private TokenService tokenService;

    private Profession profession;

    private Document document;


    //@OnStateChanged(source = "NEW", target = "PERSON_VALIDATED")
    public void newToPersonValidated(Message message) {
        System.out.println("Listener from NEW to PERSON_VALIDATED");
    }

    //@OnTransition(source = "PERSON_VALIDATED", target = "IS_VALIDATED_PERSON")
    public void personValidatedToIsValidatedPerson(Message message) {

        System.out.println("Listener from PERSON_VALIDATED to IS_VALIDATED_PERSON");
    }
    //@OnTransition(source = "IS_VALIDATED_PERSON", target = "PROFESSION_VALIDATED")
    public void isValidatedPersonToProfessionValidated(Message message) {

        System.out.println("Listener from IS_VALIDATED_PERSON to PROFESSION_VALIDATED");
    }

    //@OnTransition(source = "PROFESSION_VALIDATED", target = "RESULT_PROFESSION_VALIDATED")
    public void receiveTransition(Message message) {

        System.out.println("Listener from PROFESSION_VALIDATED to RESULT_PROFESSION_VALIDATED");
    }

}