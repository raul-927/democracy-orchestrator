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
import reactor.core.scheduler.Schedulers;

import java.time.Duration;
import java.util.List;


@RestController
@RequestMapping("/democracyorchestrator")
public class PostulantController {

    private static final Logger LOGGER = LoggerFactory.getLogger(PostulantController.class);

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
            LOGGER.info("Petición HTTP recibida. Desacoplando procesamiento secuencial...");

            // Ejecutamos en un pool de hilos elástico para no bloquear la respuesta HTTP
            Executors.newSingleThreadExecutor().submit(() -> {
                try {
                    executeDynamicBatchProcessing(0, maxRetries);
                } catch (Exception e) {
                    LOGGER.error("Error crítico en el lote asíncrono dinámico", e);
                }
            });

            return Mono.empty();
        });
    }

    private void executeDynamicBatchProcessing(final int currentRetry, final int maxRetries) {
        LOGGER.info("Iniciando verificación de registros pendientes en base de datos...");

        while (true) {
            // 1. IMPORTANTE: Consultamos SIEMPRE el flujo fresco de la BD.
            // Tomamos únicamente el primer registro que encuentre con 'isProcessed = false'
            var nextPerson = personService.selectPerson(new Person().setIsProcessed(false))
                    .next() // Toma solo el primer elemento emitido (Mono<Person>)
                    .block();

            // Si ya no quedan registros con 'false', el lote ha terminado con éxito
            if (nextPerson == null) {
                LOGGER.info("SAGA TOTAL FINALIZADA: No quedan más personas por procesar en la BD.");
                forkTrigger.stopForkSaga();
                return;
            }

            LOGGER.info("Procesando de forma aislada la cédula: {}", nextPerson.getCedula());

            try {
                // 2. Inicializamos la máquina de estados exclusivamente para esta persona
                forkTrigger.initForkSaga();

                var message = MessageBuilder.withPayload(ForkJoinEvents.START_FORK)
                        .setHeader("person", nextPerson)
                        .setHeader("totalRetries", currentRetry)
                        .build();

                // 3. Despachamos el evento
                forkTrigger.sendEventFork("START_FORK", Mono.just(message));

                // 4. Tiempo de espera físico para que los Actions ejecuten las ramas y actualicen la BD a true
                Thread.sleep(100);

                LOGGER.info("Ventana de tiempo cerrada para cédula: {}. Limpiando máquina...", nextPerson.getCedula());

                // 5. Destruimos la instancia de la máquina para eliminar cualquier residuo de estado o índices
                forkTrigger.stopForkSaga();

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                LOGGER.error("El proceso por lotes dinámico fue interrumpido", e);
                return;
            } catch (Exception e) {
                LOGGER.error("Error procesando de forma aislada la cédula: {}", nextPerson.getCedula(), e);
                // Si un registro falla críticamente y no cambia a 'true', rompemos el bucle para evitar bucles infinitos
                break;
            }
        }

        // 6. Zona de Control de Reintentos (si quedaron registros huérfanos por errores o caídas)
        Long remainingCount = personService.selectCount().block();
        if (remainingCount != null && remainingCount > 0 && currentRetry < maxRetries) {
            LOGGER.warn("Se detectaron {} registros estancados. Iniciando reintento del lote. Intento: {}", remainingCount, currentRetry + 1);
            executeDynamicBatchProcessing(currentRetry + 1, maxRetries);
        } else if (remainingCount != null && remainingCount > 0) {
            LOGGER.error("Se alcanzó el límite de reintentos ({}) y aún quedan {} registros sin procesar.", maxRetries, remainingCount);
        }
    }

}
