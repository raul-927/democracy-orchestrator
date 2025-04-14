package com.democracy.democracy_orchestrator.infrastructure.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.circuitbreaker.resilience4j.ReactiveResilience4JCircuitBreakerFactory;
import org.springframework.cloud.client.circuitbreaker.ReactiveCircuitBreaker;
import org.springframework.stereotype.Component;
import reactivefeign.cloud2.ReactiveFeignCircuitBreakerFactory;

@Component
public class HttpBinClientConfiguration implements ReactiveFeignCircuitBreakerFactory {

    @Autowired
    private ReactiveResilience4JCircuitBreakerFactory reactiveResilience4JCircuitBreakerFactory;

    @Override
    public ReactiveCircuitBreaker apply(String reactiveFeignClientName) {
        String circuitBreakerId = reactiveFeignClientName.replaceAll("[#(,]+", "_")
                .replaceAll("\\)+", "")
                .replaceAll("_+$", "");
        return reactiveResilience4JCircuitBreakerFactory.create(circuitBreakerId);
    }
}
