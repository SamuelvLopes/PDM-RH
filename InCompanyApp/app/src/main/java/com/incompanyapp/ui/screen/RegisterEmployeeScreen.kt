package com.incompanyapp.ui.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.tooling.preview.Preview
import com.incompanyapp.data.repository.registerEmployee

@Composable
fun RegisterEmployeeScreen() {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var companyIds by remember { mutableStateOf("") }

    Column {
        OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Nome do Funcionário") })
        OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("Email") })
        OutlinedTextField(value = password, onValueChange = { password = it }, label = { Text("Senha") })
        OutlinedTextField(value = companyIds, onValueChange = { companyIds = it }, label = { Text("IDs das Empresas (separados por vírgula)") })
        Button(onClick = { registerEmployee(name, email, password, companyIds.split(",").map { it.trim().toInt() }) }) {
            Text("Cadastrar Funcionário")
        }
    }
}

@Preview
@Composable
fun PreviewRegisterEmployeeScreen() {
    RegisterEmployeeScreen()
}
