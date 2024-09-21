package com.incompanyapp

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.navigation.compose.rememberNavController
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.model.LatLng
import com.incompanyapp.db.fb.FBDatabase
import com.incompanyapp.model.Company
import com.incompanyapp.model.MainViewModel
import com.incompanyapp.ui.nav.BottomNavBar
import com.incompanyapp.ui.nav.MainNavHost
import com.incompanyapp.ui.theme.InCompanyAppTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private lateinit var locationCallback: LocationCallback

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val viewModel : MainViewModel by viewModels()

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        locationRequest = LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY, 10000 // 10 seconds interval
        )
            .setMinUpdateIntervalMillis(5000) // Minimum interval between updates
            .setMaxUpdateDelayMillis(15000) // Maximum delay for batched location updates
            .build()

        setContent {
            if (!viewModel.loggedIn) {
                this.finish()
            }
            val fbDB = remember { FBDatabase (viewModel) }
            val context = LocalContext.current

            var userLocation by remember { mutableStateOf<LatLng?>(null) }
            val launcher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.RequestPermission(),
                onResult = { granted ->
                    if (granted) {
                        // Get user location if permission is granted
                        requestLocationUpdates(userLocationSetter = { latLng -> userLocation = latLng })
                    }
                }
            )

            LaunchedEffect(Unit) {
                launcher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            }

            InCompanyAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainScreen(viewModel, fbDB, context, userLocation)
                }
            }
        }
    }

    private fun requestLocationUpdates(userLocationSetter: (LatLng) -> Unit) {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            // Define the location callback
            locationCallback = object : LocationCallback() {
                override fun onLocationResult(locationResult: LocationResult) {
                    locationResult.let {
                        for (location in it.locations) {
                            val userLatLng = LatLng(location.latitude, location.longitude)
                            // Set user location via the setter function passed in
                            userLocationSetter(userLatLng)
                        }
                    }
                }
            }

            // Request location updates
            fusedLocationClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                null
            )
        }
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(viewModel: MainViewModel, fbDB: FBDatabase, context: Context, userLocation: LatLng?) {
    var selectedScreen by remember { mutableStateOf("home") }
    var selectedCompany by remember { mutableStateOf<Company?>(null) }
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val navController = rememberNavController()
    var shouldLogout by remember { mutableStateOf(false) }
    val launcher = rememberLauncherForActivityResult(contract =
        ActivityResultContracts.RequestPermission(), onResult = {} )

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
                    viewModel = viewModel,
                    fbDB = fbDB,
                    onCompanySelected = { company ->
                        viewModel.selectedCompany.value = company
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
                        BottomNavBar(navController = navController)
                    }
                ) { innerPadding ->
                    Box(modifier = Modifier.padding(innerPadding)) {
                        launcher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                        MainNavHost(
                            navController = navController,
                            viewModel = viewModel,
                            context = context,
                            fbDB = fbDB,
                            userLocation = userLocation,
                            modifier = Modifier.padding(innerPadding)
                        )
                    }
                }
            }
        )
    }
}

@Composable
fun DrawerContent(
    viewModel: MainViewModel,
    fbDB: FBDatabase,
    onCompanySelected: (Company) -> Unit,
    onLogout: () -> Unit
) {
    var showAddCompanyDialog by remember { mutableStateOf(false) }

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

        for (company in viewModel.companies) {
            Text(
                text = company.name,
                fontSize = 16.sp,
                modifier = Modifier
                    .padding(vertical = 8.dp)
                    .clickable { onCompanySelected(company) }
                    .background(if (viewModel.selectedCompany.value?.name == company.name) MaterialTheme.colorScheme.primary else Color.Transparent)
                    .padding(8.dp)
            )
        }

        // Button to open dialog for adding a new company
        Button(
            onClick = { showAddCompanyDialog = true },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Add Company")
        }

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = { onLogout() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Logout")
        }

        if (showAddCompanyDialog) {
            var companyCode by rememberSaveable { mutableStateOf("") }
            AlertDialog(
                onDismissRequest = { showAddCompanyDialog = false },
                title = { Text(text = "Add New Company!") },
                text = {
                    Column(
                        modifier = Modifier.padding(16.dp),
                    ) {
                        Text("Por favor entre com o código informado pelo administrador:")
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
                                    viewModel.selectedCompany.value = avaCompany
                                    showAddCompanyDialog = false
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

@Composable
fun Logo() {
    Image(
        painter = painterResource(id = R.drawable.logo),
        contentDescription = "App Logo",
        modifier = Modifier.size(32.dp),
        contentScale = ContentScale.Crop
    )
}
