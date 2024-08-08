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
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
<<<<<<< HEAD
=======
import androidx.compose.ui.draw.shadow
>>>>>>> 2f554ab1e48c1c4f3d832f2a5a4c40aabdb39a78
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.incompanyapp.data.model.Company
import com.incompanyapp.data.repository.CompanyRepository
import com.incompanyapp.data.repository.registerCompany
import com.incompanyapp.ui.theme.InCompanyAppTheme
import kotlinx.coroutines.delay
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
                Text(
                    text = "Banco de Horas:",
                    fontSize = 18.sp,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "+ 03:45:30",
                    fontSize = 32.sp,
                    textAlign = TextAlign.Center
                )
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
    var isRunning by remember { mutableStateOf(false) }
    var elapsedTime by remember { mutableStateOf(0) }
    var isPaused by remember { mutableStateOf(false) }
    val timer = remember { mutableStateOf(0L) }
    val coroutineScope = rememberCoroutineScope()

    // Timer logic
    LaunchedEffect(isRunning) {
        if (isRunning) {
            timer.value = System.currentTimeMillis()
            while (isRunning) {
                if (!isPaused) {
                    delay(1000) // Delay for 1 second
                    elapsedTime = ((System.currentTimeMillis() - timer.value) / 1000).toInt()
                }
            }
        }
    }

    fun formatTime(seconds: Int): String {
        val minutes = seconds / 60
        val displaySeconds = seconds % 60
        return String.format("%02d:%02d", minutes, displaySeconds)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .padding(16.dp)
                .background(Color.LightGray)
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = formatTime(elapsedTime),
                    fontSize = 24.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Button(
                        onClick = {
                            if (!isRunning) {
                                timer.value = System.currentTimeMillis()
                                isRunning = true
                                isPaused = false
                            }
                        }
                    ) {
                        Icon(Icons.Filled.PlayArrow, contentDescription = "Start")
                    }
                    Button(
                        onClick = {
                            if (isRunning) {
                                isPaused = !isPaused
                            }
                        }
                    ) {
                        Icon(Icons.Filled.Clear, contentDescription = "Pause")
                    }
                    Button(
                        onClick = {
                            isRunning = false
                            isPaused = false
                            elapsedTime = 0
                        }
                    ) {
                        Icon(Icons.Filled.Refresh, contentDescription = "Stop")
                    }
                }
            }
        }
    }
}

@Composable
fun ActivityListPage(modifier: Modifier = Modifier) {
    val hoursWorked = listOf(
        "25/06/24 - 07:20:10",
        "26/06/24 - 08:50:11",
        "27/06/24 - 07:50:11",
        "28/06/24 - 08:10:11",
        "29/06/24 - 05:12:11",
        "30/06/24 - 09:26:11",
        "31/06/24 - 08:32:11",
        "01/07/24 - 08:40:11",
        "02/07/24 - 08:43:11",
        "03/07/24 - 07:50:11",
        "04/07/24 - 07:51:11",
        "05/07/24 - 07:56:11"
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface)
                .padding(16.dp)
                .border(1.dp, MaterialTheme.colorScheme.onSurface)
                .shadow(4.dp, RoundedCornerShape(8.dp))
                .padding(16.dp)
        ) {
            Column (
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Relatorio:",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.size(8.dp))
                hoursWorked.forEach { entry ->
                    Text(
                        text = entry,
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.fillMaxWidth().padding(bottom = 4.dp),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
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
