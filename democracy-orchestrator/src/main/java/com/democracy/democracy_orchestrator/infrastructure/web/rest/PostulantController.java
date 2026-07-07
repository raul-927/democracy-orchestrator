package com.democracy.democracy_orchestrator.infrastructure.web.rest;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.democracy.democracy_orchestrator.application.services.PersonService;
import com.democracy.democracy_orchestrator.domain.models.Person;
import com.democracy.democracy_orchestrator.infrastructure.statemachine.events.forkjoin.ForkJoinEvents;
import com.democracy.democracy_orchestrator.infrastructure.statemachine.events.postulation.PostulationEvents;
import com.democracy.democracy_orchestrator.infrastructure.statemachine.trigers.forks.ForkTrigger;
import com.democracy.democracy_orchestrator.infrastructure.statemachine.trigers.postulation.PostulantTrigger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.List;


@RestController
@RequestMapping("/democracyorchestrator")
public class PostulantController {

    private static final Logger LOGGER = LoggerFactory.getLogger(PostulantController.class);
    private final ExecutorService virtualThreadExecutor = Executors.newVirtualThreadPerTaskExecutor();


    @Autowired
    private PostulantTrigger postulantTrigger;

    @Autowired
    private ForkTrigger forkTrigger;

    @Autowired
    private PersonService personService;

    private int retries;

    @PostMapping(
            value = "/investigation/select",
            produces = {MediaType.TEXT_EVENT_STREAM_VALUE})
    public  Flux<Person> getInvestigation(@RequestBody List<Person> person) {
        Flux<Person> personFlux = personService.selectPerson(new Person().setIsProcessed(false));
        personFlux
                .map(item->{
                    postulantTrigger.initPostulationSaga();
                    postulantTrigger.sendEvent("VALIDATE_PERSON", Mono.just(
                            MessageBuilder.withPayload(PostulationEvents.VALIDATE_PERSON)
                                    .setHeader("cedula", item.getCedula())
                                    .build()));
                    return item;
                })
                .thenEmpty(em ->{
                    LOGGER.info("EMPTY personFlux");
                })
                .subscribe();
        return personFlux;
    }

    @PostMapping(
            value = "/investigation/fork",
            produces = MediaType.TEXT_EVENT_STREAM_VALUE
    )
    public Mono<Void> startForkSaga() {
        final int maxRetries = 3;

        return Mono.defer(() -> {
            LOGGER.info("Petición HTTP recibida. Despachando lote en un Virtual Thread de Java 22...");

            // Asignamos la tarea al Executor de Hilos Virtuales
            virtualThreadExecutor.submit(() -> {
                try {
                    executeDynamicBatchProcessing(0, maxRetries);
                } catch (Exception e) {
                    LOGGER.error("Error crítico en el procesamiento asíncrono con Virtual Threads", e);
                }
            });

            // Liberamos el hilo de la petición HTTP inmediatamente
            return Mono.empty();
        });
    }

    private void executeDynamicBatchProcessing(final int currentRetry, final int maxRetries) {
        LOGGER.info("Virtual Thread activo. Verificando registros pendientes...");

        while (true) {
            // Consultamos el registro fresco de la BD de forma no bloqueante para el sistema operativo
            var nextPerson = personService.selectPerson(new Person().setIsProcessed(false))
                    .next()
                    .block();

            if (nextPerson == null) {
                LOGGER.info("SAGA TOTAL FINALIZADA: No quedan más personas pendientes en la BD.");
                forkTrigger.stopForkSaga();
                return;
            }

            LOGGER.info("Procesando de forma aislada la cédula: {}", nextPerson.getCedula());

            try {
                forkTrigger.initForkSaga();

                var message = MessageBuilder.withPayload(ForkJoinEvents.START_FORK)
                        .setHeader("person", nextPerson)
                        .setHeader("totalRetries", currentRetry)
                        .build();

                forkTrigger.sendEventFork("START_FORK", Mono.just(message));

                // En Java 22, este sleep cede el control inmediatamente a otros procesos de la JVM.
                // Cero consumo de hilos reales del sistema operativo durante los 1.5 segundos.
                Thread.sleep(Duration.ofMillis(100));

                LOGGER.info("Ventana de tiempo cerrada para cédula: {}. Reiniciando máquina...", nextPerson.getCedula());
                forkTrigger.stopForkSaga();

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                LOGGER.error("El Virtual Thread fue interrumpido", e);
                return;
            } catch (Exception e) {
                LOGGER.error("Error procesando cédula: {}", nextPerson.getCedula(), e);
                break;
            }
        }

        Long remainingCount = personService.selectCount().block();
        if (remainingCount != null && remainingCount > 0 && currentRetry < maxRetries) {
            LOGGER.warn("Registros estancados detectados ({}). Reintentando lote. Intento: {}", remainingCount, currentRetry + 1);
            executeDynamicBatchProcessing(currentRetry + 1, maxRetries);
        } else if (remainingCount != null && remainingCount > 0) {
            LOGGER.error("Límite de reintentos alcanzado ({}). Quedan {} registros sin procesar.", maxRetries, remainingCount);
        }
    }
}
