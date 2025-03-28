package com.democracy.democracy_orchestrator.infrastructure.web.handlers;

import com.democracy.democracy_orchestrator.application.services.DepartmentService;
import com.democracy.democracy_orchestrator.domain.models.Department;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
public class DepartmentHandler {

    @Autowired
    private DepartmentService departmentService;

    /*public Mono<ServerResponse> selectDepartment(ServerRequest request){
        var obtainDepartment = request.bodyToMono(Department.class);
        Department sendDepartment  = new Department();
        obtainDepartment.map( str ->{
            sendDepartment.setDepartmentId(str.getDepartmentId());
            sendDepartment.setDepartmentName(str.getDepartmentName());
            sendDepartment.setCityList(str.getCityList());
            return sendDepartment;
        });
        return ServerResponse
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(departmentService.selectDepartment(sendDepartment), Department.class);
    }*/

    /*public Mono<ServerResponse> selectAllDepartment(ServerRequest request){
        System.out.println("LLEGO DEPARTMENT");
        return ServerResponse
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(departmentService.selectAllDepartment(), Department.class);
    }*/

    /*public Mono<ServerResponse> createDepartment(ServerRequest request){
        Mono<Department> department = request.bodyToMono(Department.class);

        return department.flatMap(
                d ->ServerResponse
                        .ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(departmentService.createDepartment(d), Department.class));
    }*/

    /*public Mono<ServerResponse> createMultipleDepartment(ServerRequest request){
        List<Department>departmentList = new ArrayList<>();

        var obtainListDepartments = request.bodyToFlux(Department.class);
        obtainListDepartments.collectList().map(
                dprt ->{
                    System.out.println("DPRT: "+dprt);
                    departmentList.addAll(dprt);
                    System.out.println("DEPARTMENT_LIST: "+departmentList);
                    return departmentList;
                }
        ).subscribe(System.out::println).dispose();
        return ServerResponse
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(departmentService.createMultipleDepartment(departmentList), Department.class);
    }*/

    /*public Mono<ServerResponse> updateDepartment(ServerRequest request){
        Mono<Department> department = request.bodyToMono(Department.class);
        return department.flatMap(
                d ->ServerResponse
                        .ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(departmentService.updateDepartment(d), Department.class));
    }*/

    /*public Mono<ServerResponse> selectCount(ServerRequest request){
        Mono<Long> countResult = departmentService.selectCount();
        return ServerResponse
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(countResult, Department.class);
    }*/
}
