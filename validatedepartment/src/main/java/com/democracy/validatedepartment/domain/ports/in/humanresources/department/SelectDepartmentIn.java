package com.democracy.validatedepartment.domain.ports.in.humanresources.department;

import com.democracy.validatedepartment.domain.models.Department;
import reactor.core.publisher.Flux;

import java.util.List;

public interface SelectDepartmentIn {

    Flux<Department> selectAllDepartment();
}
