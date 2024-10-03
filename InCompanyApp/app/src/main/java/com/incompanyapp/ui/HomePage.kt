package com.incompanyapp.ui

import android.app.Activity
import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.incompanyapp.db.fb.FBDatabase
import com.incompanyapp.db.fb.toFBUser
import com.incompanyapp.model.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun HomePage(
    modifier: Modifier = Modifier,
    viewModel: MainViewModel,
    context: Context,
    fbDB: FBDatabase,
) {
    val activity = LocalContext.current as? Activity
    var openDialog by remember { mutableStateOf(true) }
    var companyCode by rememberSaveable { mutableStateOf("") }

    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Bem-vindo/a!",
            fontSize = 24.sp
        )
        Text(
            text = if(viewModel.selectedCompany?.name?.isNotEmpty() == true) "Empresa: ${viewModel.selectedCompany?.name}" else "Selecione uma empresa",
            fontSize = 18.sp,
            modifier = Modifier.padding(top = 8.dp)
        )
        if (!viewModel.selectedCompany?.name.equals(null)) {
            ClockBankBox()
        }

        if (openDialog && viewModel.companies.isEmpty()) {
            AlertDialog(
                onDismissRequest = {},
                title = { Text(text = "Novo usuário!") },
                text = {
                    Column(
                        modifier = Modifier.padding(16.dp),
                    ) {
                        Text("Você não está associado a nenhuma empresa, por favor entre com o código informado pelo administrador:")
                        Spacer(modifier = Modifier.size(24.dp))
                        OutlinedTextField(
                            value = companyCode,
                            label = { Text(text = "Digite o código da empresa") },
                            onValueChange = { companyCode = it }
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            for (avaCompany in viewModel.availableCompanies) {
                                if (avaCompany.code == companyCode) {
                                    fbDB.add(avaCompany)
                                    viewModel.selectedCompany = avaCompany
                                    openDialog = false
                                }
                            }
                        },
                        enabled = companyCode.isNotEmpty()
                    ) {
                        Text("Enter")
                    }
                }
            )
        }
    }
}
