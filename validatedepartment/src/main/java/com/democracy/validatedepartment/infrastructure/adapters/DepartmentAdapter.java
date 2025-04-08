package com.democracy.validatedepartment.infrastructure.adapters;

import com.democracy.validatedepartment.application.services.TokenService;
import com.democracy.validatedepartment.domain.models.Department;
import com.democracy.validatedepartment.domain.ports.out.humanresources.DepartmentOut;
import com.democracy.validatedepartment.infrastructure.client.DepartmentFeingClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import java.util.List;

@Component
public class DepartmentAdapter implements DepartmentOut {

    @Autowired
    private DepartmentFeingClient departmentFeingClient;
    @Autowired
    private TokenService tokenService;

    @Autowired
    private WebClient webClient;

    @Override
    public Flux<Department> selectAllDepartment() {
        return webClient.get()
                .uri("http://localhost:8082/humanresources/department/select-all")
                .headers((headers) -> headers.add("authorization", tokenService.obtainToken()))
                .retrieve()
                .bodyToFlux(Department.class);//departmentFeingClient.getAllDepartment(tokenService.obtainToken());
    }
}
