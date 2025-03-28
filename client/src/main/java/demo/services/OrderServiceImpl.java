package demo.services;


import demo.client.DepartmentFeingClient;
import demo.client.HelloClient;
import demo.domain.Department;
import demo.domain.KeyCloakToken;
import demo.domain.Order;
import demo.events.OrderEvents;


import demo.states.OrderStates;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;


@Service
public class OrderServiceImpl implements OrderService{
    @Autowired
    private StateMachineFactory<OrderStates, OrderEvents> machineFactory;
    private StateMachine<OrderStates, OrderEvents> stateMachine;

    @Autowired
    private HelloClient client;

    @Autowired
    private DepartmentFeingClient feingClient;


    private final String BORED_API = "http://localhost:8082/humanresources/department/select-count";

    @Override
    public Order newOrder(Order order) {
        initOrderSaga();
        validateOrder(order);
        payOrder();
        shipOrder();
        completeOrder();
        return order;
    }
    @Override
    public void initOrderSaga(){
        System.out.println("Initializing order saga");
        stateMachine = machineFactory.getStateMachine();
        stateMachine.startReactively().subscribe();
        System.out.println("Final state initOrderSaga: "+stateMachine.getState().getId());
    }
    @Override
    public void validateOrder(Order order) {
        String clientString = client.hello();
        System.out.println("STRING_CLIENT: "+clientString);
        System.out.println("Validating order");

       List<Department> departments = getDepartmentbyRestTemplate();
        departments.forEach(dep->{
            System.out.println("DEPARTMENT_ID: " +dep.getDepartmentId());
            System.out.println("DEPARTMENT_NAME: "+dep.getDepartmentName());
        });


        stateMachine.sendEvent(Mono.just(
                        MessageBuilder.withPayload(OrderEvents.VALIDATE)
                                .setHeader("order",order)
                                .setHeader("departmentList", departments).build()))
                .subscribe(result -> System.out.println("RESULT validateOrder: "+result.getResultType()));
        System.out.println("Final state validateOrder: "+stateMachine.getState().getId());
    }
    @Override
    public void payOrder() {
        System.out.println("Paying order...");
        stateMachine.sendEvent(Mono.just(
                        MessageBuilder.withPayload(OrderEvents.PAY).build()))
                .subscribe(result -> System.out.println("RESULT payOrder: "+result.getResultType()));
        System.out.println("Final state payOrder: "+stateMachine.getState().getId());
    }
    @Override
    public void shipOrder() {
        System.out.println("Shipping order...");
        stateMachine.sendEvent(Mono.just(
                        MessageBuilder.withPayload(OrderEvents.SHIP).build()))
                .subscribe(result -> System.out.println("RESULT shipOrder: "+result.getResultType()));
        System.out.println("Final state: shipOrder "+stateMachine.getState().getId());
    }
    @Override
    public void completeOrder() {
        System.out.println("Completing order.");
        stateMachine.sendEvent(Mono.just(
                        MessageBuilder.withPayload(OrderEvents.COMPLETE).build()))
                .subscribe(result -> System.out.println("RESULT completeOrder: "+result.getResultType()));
        System.out.println("Final state completeOrder: "+stateMachine.getState().getId());
        stopOrderSaga();
    }
    @Override
    public void stopOrderSaga(){
        System.out.println("Stopping saga...");
        System.out.println("------------------------");
        stateMachine.stopReactively().subscribe();
    }

    //@GetMapping(value = "/select-count", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
   /*
    public Flux<Department> getDepartment() {
        KeyCloakToken token = obtainToken();
        System.out.println("ENTRA");
        WebClient webClient = WebClient.builder()
                .baseUrl("http://localhost:8082")
                .defaultHeader("Authorization", token.getToken_type() + " "+token.getAccess_token()).build();

        return webClient.get()
                .uri("/humanresources/department/select-all")
                .accept(MediaType.APPLICATION_JSON)
                .exchangeToFlux(response -> {
                    if (response.statusCode().equals(HttpStatus.OK)) {
                        return response.bodyToFlux(Department.class);
                    }else{
                        System.out.println("STATUS: "+response.statusCode());
                        return response.bodyToFlux(Department.class);
                    }
                });
    }
*/
    private List<Department> getDepartmentbyRestTemplate(){
        KeyCloakToken token = obtainToken();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", token.getToken_type() + " "+token.getAccess_token());
        RestTemplate restTemplate = new RestTemplate();
        String resourceUrl = "http://localhost:8082/humanresources/department/select-all";
        HttpEntity<String> entity = new HttpEntity<>(null, headers);
        ResponseEntity<List<Department>> response
                = restTemplate.exchange(resourceUrl, HttpMethod.GET, entity, new ParameterizedTypeReference<List<Department>>() {});
        return response.getBody();
    }
    private KeyCloakToken obtainToken(){
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("client_id","democracy_client");
        map.add("client_secret","YYcdVQO1lB9F5IjxjN6ljHueBWhZz1aZ");
        map.add("grant_type","password");
        map.add("username","raraherher9274");
        map.add("password","raraherher9274");
        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(map, headers);
        ResponseEntity<KeyCloakToken> response =
                restTemplate.exchange("http://localhost:8181/realms/democracy_realm/protocol/openid-connect/token",
                        HttpMethod.POST,
                        entity,
                        KeyCloakToken.class);
        return response.getBody();
    }
}