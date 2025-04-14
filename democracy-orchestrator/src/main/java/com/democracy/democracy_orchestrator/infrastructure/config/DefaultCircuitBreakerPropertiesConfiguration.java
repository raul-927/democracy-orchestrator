package com.democracy.democracy_orchestrator.infrastructure.config;

import io.github.resilience4j.springboot3.circuitbreaker.autoconfigure.CircuitBreakerProperties;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

import java.util.Objects;

public class DefaultCircuitBreakerPropertiesConfiguration implements BeanPostProcessor {
    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (bean instanceof CircuitBreakerProperties) {
            CircuitBreakerProperties circuitBreakerProperties = (CircuitBreakerProperties) bean;
            circuitBreakerProperties.getInstances()
                    .values()
                    .stream()
                    .filter(instanceProperties -> Objects.isNull(instanceProperties.getBaseConfig()))
                    .forEach(instanceProperties -> instanceProperties.setBaseConfig("default"));
        }
        return bean;
    }
}
