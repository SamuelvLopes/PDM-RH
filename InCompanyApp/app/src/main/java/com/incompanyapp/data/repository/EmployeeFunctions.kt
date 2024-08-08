package com.incompanyapp.data.repository

import com.incompanyapp.data.model.Employee

fun registerEmployee(name: String, email: String, password: String, companyIds: List<Int>): Employee {
    val employee = Employee(id = EmployeeRepository.getEmployees().size + 1, name = name, email = email, password = password, companyIds = companyIds)
    EmployeeRepository.addEmployee(employee)
    return employee
}
