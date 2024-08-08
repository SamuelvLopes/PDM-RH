// Path: app/src/main/java/com/incompanyapp/data/model/Employee.kt

package com.incompanyapp.data.model

data class Employee(
    val id: Int,
    val name: String,
    val email: String,
    val password: String,
    val companyIds: List<Int>
)
