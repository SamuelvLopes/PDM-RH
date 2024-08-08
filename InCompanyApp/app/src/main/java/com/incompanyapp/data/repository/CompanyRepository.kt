package com.incompanyapp.data.repository

import com.incompanyapp.data.model.Company

object CompanyRepository {
    private val companies = mutableListOf<Company>()

    fun addCompany(company: Company) {
        companies.add(company)
    }

    fun getCompanies(): List<Company> {
        return companies
    }

    fun getCompanyById(id: Int): Company? {
        return companies.find { it.id == id }
    }

    fun getCompanyByName(name: String): Company? {
        return companies.find { it.name == name }
    }
}
