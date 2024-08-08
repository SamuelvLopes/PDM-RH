package com.incompanyapp.data.repository

import com.incompanyapp.data.model.Company

fun registerCompany(name: String, address: String): Company {
    val company = Company(id = CompanyRepository.getCompanies().size + 1, name = name, address = address)
    CompanyRepository.addCompany(company)
    return company
}
