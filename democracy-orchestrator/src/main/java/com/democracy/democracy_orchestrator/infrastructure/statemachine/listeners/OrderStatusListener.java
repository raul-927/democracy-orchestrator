package com.democracy.democracy_orchestrator.infrastructure.statemachine.listeners;


import com.democracy.democracy_orchestrator.application.services.InvestigationService;
import com.democracy.democracy_orchestrator.application.services.TokenService;
import com.democracy.democracy_orchestrator.domain.models.Document;
import com.democracy.democracy_orchestrator.domain.models.Profession;
import com.democracy.democracy_orchestrator.infrastructure.statemachine.trigers.PostulantTrigger;
import com.democracy.democracy_orchestrator.infrastructure.statemachine.trigers.PostulantTriggerImpl;
import org.springframework.messaging.Message;
import org.springframework.web.reactive.function.client.WebClient;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @description: state listener
 */
//@Component
//@WithStateMachine
//@Transactional
@Slf4j
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

    Logger LOGGER = LoggerFactory.getLogger(PostulantTriggerImpl.class);


    //@OnStateChanged(source = "NEW", target = "PERSON_VALIDATED")
    public void newToPersonValidated(Message message) {
        LOGGER.info("Listener from NEW to PERSON_VALIDATED");
    }

    //@OnTransition(source = "PERSON_VALIDATED", target = "IS_VALIDATED_PERSON")
    public void personValidatedToIsValidatedPerson(Message message) {

        LOGGER.info("Listener from PERSON_VALIDATED to IS_VALIDATED_PERSON");
    }
    //@OnTransition(source = "IS_VALIDATED_PERSON", target = "PROFESSION_VALIDATED")
    public void isValidatedPersonToProfessionValidated(Message message) {

        LOGGER.info("Listener from IS_VALIDATED_PERSON to PROFESSION_VALIDATED");
    }

    //@OnTransition(source = "PROFESSION_VALIDATED", target = "RESULT_PROFESSION_VALIDATED")
    public void receiveTransition(Message message) {

        LOGGER.info("Listener from PROFESSION_VALIDATED to RESULT_PROFESSION_VALIDATED");
    }

}