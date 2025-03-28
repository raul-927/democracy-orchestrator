package com.democracy.validatedepartment.domain.ports.in.humanresources.department;

import com.democracy.validatedepartment.domain.models.Department;

import java.util.List;

public interface SelectDepartmentIn {

    List<Department> selectAllDepartment();
}
