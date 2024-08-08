package com.incompanyapp.data.repository

import com.incompanyapp.data.model.Employee

object EmployeeRepository {
    private val employees = mutableListOf<Employee>()

    fun addEmployee(employee: Employee) {
        employees.add(employee)
    }

    fun getEmployees(): List<Employee> {
        return employees
    }

    fun getEmployeeById(id: Int): Employee? {
        return employees.find { it.id == id }
    }

    fun getEmployeeByEmail(email: String): Employee? {
        return employees.find { it.email == email }
    }
}
