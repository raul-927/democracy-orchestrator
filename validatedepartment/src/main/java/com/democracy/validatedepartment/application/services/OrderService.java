package com.democracy.validatedepartment.application.services;


import com.democracy.validatedepartment.application.statemachine.events.OrderEvents;
import com.democracy.validatedepartment.domain.models.Order;
import org.springframework.messaging.Message;
import reactor.core.publisher.Mono;

public interface OrderService {
    Order newOrder(Order order);
    void initOrderSaga();
    void validateOrder(Order order);
    void payOrder(Mono<Message<OrderEvents>> event);
    void shipOrder(Mono<Message<OrderEvents>> event);
    void completeOrder(Mono<Message<OrderEvents>> event);
    void stopOrderSaga();
}
