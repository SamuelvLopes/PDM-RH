package com.incompanyapp.ui.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.tooling.preview.Preview
import com.incompanyapp.data.repository.registerCompany

@Composable
fun RegisterCompanyScreen() {
    var name by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }

    Column {
        OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Nome da Empresa") })
        OutlinedTextField(value = address, onValueChange = { address = it }, label = { Text("Endereço da Empresa") })
        Button(onClick = { registerCompany(name, address) }) {
            Text("Cadastrar Empresa")
        }
    }
}

@Preview
@Composable
fun PreviewRegisterCompanyScreen() {
    RegisterCompanyScreen()
}
