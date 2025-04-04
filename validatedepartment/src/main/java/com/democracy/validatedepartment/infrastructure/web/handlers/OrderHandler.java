package com.democracy.validatedepartment.infrastructure.web.handlers;



import com.democracy.validatedepartment.application.services.OrderService;
import com.democracy.validatedepartment.domain.models.Order;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

//@Component
//@Slf4j
public class OrderHandler {

    //@Autowired
    private OrderService orderService;

    public Mono<ServerResponse> selectAllOrders(ServerRequest request){
        var obtainOrder = request.bodyToMono(Order.class);
        Order sendDOrder  = new Order();
        obtainOrder.map( ord ->{
            sendDOrder.setOrderId(ord.getOrderId());
            sendDOrder.setOrderType(ord.getOrderType());
            sendDOrder.setProduct(ord.getProduct());
            return sendDOrder;
        });

        return ServerResponse
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(orderService.newOrder(sendDOrder), Order.class);
    }
}