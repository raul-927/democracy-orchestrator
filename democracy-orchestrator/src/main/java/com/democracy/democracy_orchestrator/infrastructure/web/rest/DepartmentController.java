package com.democracy.democracy_orchestrator.infrastructure.web.rest;

import com.democracy.democracy_orchestrator.application.services.DepartmentService;
import com.democracy.democracy_orchestrator.domain.models.Department;
import feign.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("/democracyorchestrator")
public class DepartmentController {

    @Autowired
    private DepartmentService departmentService;


   // @GetMapping("/department/select-all")
    public ResponseEntity<List<Department>> getAllDepartments(){
        List<Department> departments = this.departmentService.selectAllDepartment();

        return new ResponseEntity<>(departments, null, HttpStatus.OK);
    }

    @GetMapping("/department/select-all")
    public Mono<List<Department>> getAllDepartmentss(){
        List<Department> departments = this.departmentService.selectAllDepartment();

        return Mono.just(departments);
    }


}
