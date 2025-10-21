package com.democracy.democracy_orchestrator.infrastructure.adapters;


import com.democracy.democracy_orchestrator.application.services.TokenService;
import com.democracy.democracy_orchestrator.domain.models.Department;
import com.democracy.democracy_orchestrator.domain.ports.out.DepartmentOut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ReactiveHttpOutputMessage;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserter;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

@Component
public class DepartmentAdapter implements DepartmentOut {

    @Autowired
    private WebClient webClient;

    @Autowired
    private TokenService tokenService;

    @Override
    public Flux<Department> selectDepartment(Department department) {
        BodyInserter<Department, ReactiveHttpOutputMessage> selectDepartment = BodyInserters.fromValue(department);
        return webClient.post()
                .uri("http://localhost:8082/humanresources/department/select")
                .headers((headers) -> headers.add("authorization", tokenService.obtainToken()))
                .body(selectDepartment)
                .retrieve()
                .bodyToFlux(Department.class);
    }
}
