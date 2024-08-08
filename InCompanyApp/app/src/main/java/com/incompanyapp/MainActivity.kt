package com.incompanyapp

import android.app.Activity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.incompanyapp.ui.theme.InCompanyAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            InCompanyAppTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    HomePage()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
fun HomePage(modifier: Modifier = Modifier) {
    val activity = LocalContext.current as? Activity
    val openDialog = remember { mutableStateOf(true) }
    var companyCode by rememberSaveable { mutableStateOf("") }

    // Opens the dialog when the component starts
    LaunchedEffect(Unit) {
        openDialog.value = true
    }

    Scaffold(
        topBar = {
            Text(
                text = "Sidebar",
                modifier = Modifier.padding(16.dp),
                fontSize = 20.sp
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Bem-vindo/a!",
                fontSize = 24.sp
            )
            Spacer(modifier = Modifier.size(24.dp))
            Row(modifier = modifier) {
                Button(
                    onClick = {
                        activity?.finish()
                    }
                ) {
                    Text("Sair")
                }
            }
        }

        // Window dialog
        if (openDialog.value) {
            androidx.compose.material3.AlertDialog(
                onDismissRequest = {},
                title = {
                    Text(text = "Novo usuário!")
                },
                text = {
                    Column(
                        modifier = Modifier.padding(16.dp),
                    ) {
                        Text("Você não está associado a nenhuma empresa, por favor entre com o código informado pelo administrador:")
                        Spacer(modifier = Modifier.size(24.dp))
                        OutlinedTextField(
                            value = companyCode,
                            label = { Text(text = "Digite o codigo da empresa") },
                            modifier = Modifier,
                            onValueChange = { companyCode = it }
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = { openDialog.value = false},
                        enabled = companyCode.isNotEmpty()
                    ) {
                        Text("Enter")
                    }
                }
            )
        }
    }
}
