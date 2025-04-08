package com.democracy.validatedepartment.domain.ports.out.humanresources;

import com.democracy.validatedepartment.domain.models.Department;
import reactor.core.publisher.Flux;

import java.util.List;

public interface DepartmentOut {

    Flux<Department> selectAllDepartment();
}
