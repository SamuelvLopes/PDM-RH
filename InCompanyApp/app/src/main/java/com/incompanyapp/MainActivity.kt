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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
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
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainScreen()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen() {
    var selectedScreen by remember { mutableStateOf("home") }

    Scaffold(
        topBar = {
            Text(
                text = "Sidebar",
                modifier = Modifier.padding(16.dp),
                fontSize = 20.sp
            )
        },
        bottomBar = {
            BottomNavigationBar(selectedScreen = selectedScreen) { screen ->
                selectedScreen = screen
            }
        }
    ) { padding ->
        when (selectedScreen) {
            "home" -> HomePage(Modifier.padding(padding))
            "clock" -> ClockPage(Modifier.padding(padding))
            "activityList" -> ActivityListPage(Modifier.padding(padding))
        }
    }
}

@Composable
fun BottomNavigationBar(selectedScreen: String, onScreenSelected: (String) -> Unit) {
    NavigationBar {
        NavigationBarItem(
            icon = { Icon(Icons.Filled.Home, contentDescription = "Home") },
            label = { Text("Home") },
            selected = selectedScreen == "home",
            onClick = { onScreenSelected("home") }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Filled.PlayArrow, contentDescription = "Clock") }, // Placeholder icon for "Clock"
            label = { Text("Clock") },
            selected = selectedScreen == "clock",
            onClick = { onScreenSelected("clock") }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Filled.List, contentDescription = "Activity List") },
            label = { Text("Activities") },
            selected = selectedScreen == "activityList",
            onClick = { onScreenSelected("activityList") }
        )
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
fun HomePage(modifier: Modifier = Modifier) {
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

        if (openDialog) {
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
                        onClick = { openDialog = false },
                        enabled = companyCode.isNotEmpty()
                    ) {
                        Text("Enter")
                    }
                }
            )
        }
    }
}

@Composable
fun ClockPage(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Clock Page", fontSize = 24.sp)
    }
}

@Composable
fun ActivityListPage(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Activity List Page", fontSize = 24.sp)
    }
}
