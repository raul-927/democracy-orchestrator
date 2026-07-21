package com.democracy.democracy_orchestrator.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.core.task.VirtualThreadTaskExecutor;

@Configuration
public class ExecutorConfig {

    @Bean(name = "democracy-task-executor")
    public TaskExecutor stateMachineTaskExecutor() {
        // Ejecutor nativo optimizado para hilos virtuales en Spring Boot 3.x
        return new VirtualThreadTaskExecutor("sm-virtual-");
    }
}
