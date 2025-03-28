package com.democracy.validatedepartment.domain.ports.out.humanresources;

import com.democracy.validatedepartment.domain.models.Department;

import java.util.List;

public interface DepartmentOut {

    List<Department> selectAllDepartment();
}
