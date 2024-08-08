package com.incompanyapp

import android.app.Activity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.incompanyapp.data.model.Company
import com.incompanyapp.data.repository.CompanyRepository
import com.incompanyapp.data.repository.registerCompany
import com.incompanyapp.ui.theme.InCompanyAppTheme
import kotlinx.coroutines.launch

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
    var selectedCompany by remember { mutableStateOf<Company?>(null) }
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    var shouldLogout by remember { mutableStateOf(false) }
    var companies by remember { mutableStateOf(CompanyRepository.getCompanies()) }

    if (shouldLogout) {
        val activity = LocalContext.current as? Activity
        LaunchedEffect(Unit) {
            activity?.finish()
        }
    }

    Box {
        ModalNavigationDrawer(
            drawerState = drawerState,
            gesturesEnabled = true,
            drawerContent = {
                DrawerContent(
                    companies = companies,
                    selectedCompany = selectedCompany,
                    onCompanySelected = { company ->
                        selectedCompany = company
                        scope.launch { drawerState.close() }
                    },
                    onLogout = {
                        shouldLogout = true
                    }
                )
            },
            content = {
                Scaffold(
                    topBar = {
                        TopAppBar(
                            title = {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    Spacer(modifier = Modifier.weight(1f))
                                    Logo()
                                    Spacer(modifier = Modifier.weight(1f))
                                }
                            },
                            navigationIcon = {
                                IconButton(onClick = { scope.launch { drawerState.open() } }) {
                                    Icon(Icons.Filled.List, contentDescription = "Open Drawer")
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            actions = { Spacer(modifier = Modifier.size(48.dp)) }
                        )
                    },
                    bottomBar = {
                        BottomNavigationBar(selectedScreen = selectedScreen) { screen ->
                            selectedScreen = screen
                        }
                    }
                ) { padding ->
                    when (selectedScreen) {
                        "home" -> HomePage(
                            modifier = Modifier.padding(padding),
                            selectedCompany = selectedCompany,
                            companies = companies,
                            onCompanyAdded = { newCompany ->
                                companies = CompanyRepository.getCompanies()
                                selectedCompany = newCompany
                            }
                        )
                        "clock" -> ClockPage(Modifier.padding(padding))
                        "activityList" -> ActivityListPage(Modifier.padding(padding))
                    }
                }
            }
        )
    }
}

@Composable
fun DrawerContent(
    companies: List<Company>,
    selectedCompany: Company?,
    onCompanySelected: (Company) -> Unit,
    onLogout: () -> Unit
) {
    Column(
        modifier = Modifier
            .width(240.dp)
            .fillMaxHeight()
            .background(MaterialTheme.colorScheme.surface)
            .padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start
    ) {
        Text("Companies", fontSize = 20.sp, modifier = Modifier.padding(bottom = 8.dp))

        for (company in companies) {
            Text(
                text = company.name,
                fontSize = 16.sp,
                modifier = Modifier
                    .padding(vertical = 8.dp)
                    .clickable { onCompanySelected(company) }
                    .background(if (selectedCompany?.id == company.id) MaterialTheme.colorScheme.primary else Color.Transparent)
                    .padding(8.dp)
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = { onLogout() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Logout")
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
            icon = { Icon(Icons.Filled.PlayArrow, contentDescription = "Clock") },
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
fun HomePage(
    modifier: Modifier = Modifier,
    selectedCompany: Company?,
    companies: List<Company>,
    onCompanyAdded: (Company) -> Unit
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
        selectedCompany?.let {
            Text(
                text = "Empresa: ${it.name}",
                fontSize = 18.sp,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
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
                        onClick = {
                            // Registrar uma nova companhia com o código inserido
                            val newCompany = registerCompany(companyCode, "Endereço Desconhecido")
                            onCompanyAdded(newCompany)
                            openDialog = false
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

@Composable
fun Logo() {
    Image(
        painter = painterResource(id = R.drawable.logo),
        contentDescription = "App Logo",
        modifier = Modifier.size(32.dp),
        contentScale = ContentScale.Crop
    )
}
