package demo.controller;


import demo.HelloClientApplication;
import demo.client.HelloClient;
import demo.domain.Order;
import demo.services.OrderService;
import demo.services.OrderServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/client")
public class ClientController {

    @Autowired
    private HelloClient client;

    @Autowired
    private OrderService orderService;

    @GetMapping("/validate")
    public String hello() {
        Order returnOrder = orderService.newOrder(new Order());
        return "OK";
    }

    @PostMapping("/validate")
    @ResponseStatus(HttpStatus.OK)
    public Mono<Order> newOrder(@RequestBody Order order){
        return Mono.just(orderService.newOrder(order));
    }


}
