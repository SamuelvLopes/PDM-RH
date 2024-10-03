package com.incompanyapp.ui

import android.location.Location
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import com.incompanyapp.db.fb.FBDatabase
import com.incompanyapp.model.Clock
import com.incompanyapp.model.MainViewModel
import kotlinx.coroutines.delay
import java.time.LocalDate
import java.time.LocalTime

//@RequiresApi(Build.VERSION_CODES.O)
//@Composable
//fun ClockPage(viewModel: MainViewModel, userLocation: LatLng?, modifier: Modifier = Modifier) {
//    var isRunning by remember { mutableStateOf(false) }
//    var clock by remember { mutableStateOf<Clock?>(null) }
//    var distance by remember { mutableStateOf(0f) }
//    var isWithinDistance by remember { mutableStateOf(false) }
//
//    LaunchedEffect(userLocation, viewModel.selectedCompany?.location) {
//        if (userLocation != null && viewModel.selectedCompany?.location != null) {
//            val companyLocation = viewModel.selectedCompany?.location
//            distance = calculateDistance(userLocation, companyLocation)
//            isWithinDistance = distance <= 100 // Check if within 100 meters
//        }
//    }
//
//    Column(
//        modifier = modifier
//            .fillMaxSize()
//            .padding(16.dp),
//        verticalArrangement = Arrangement.Center,
//        horizontalAlignment = Alignment.CenterHorizontally
//    ) {
//        Box(
//            modifier = Modifier
//                .padding(16.dp)
//                .background(Color.LightGray)
//                .padding(16.dp),
//            contentAlignment = Alignment.Center
//        ) {
//            Column(
//                horizontalAlignment = Alignment.CenterHorizontally
//            ) {
//                Text(
//                    text = clock?.let { "Clock In: ${it.clockIn}, Clock Out: ${it.clockOut}" } ?: "Not Timed In",
//                    fontSize = 24.sp,
//                    textAlign = TextAlign.Center,
//                    modifier = Modifier.padding(bottom = 16.dp)
//                )
//                Row(
//                    modifier = Modifier.fillMaxWidth(),
//                    horizontalArrangement = Arrangement.SpaceEvenly
//                ) {
//                    Button(
//                        onClick = {
//                            if (!isRunning && isWithinDistance) {
//                                clock = Clock(LocalDate.now(), LocalTime.now(), null) // Set clockIn
//                                isRunning = true
//                            }
//                        },
//                        enabled = isWithinDistance
//                    ) {
//                        Icon(Icons.Filled.PlayArrow, contentDescription = "Start")
//                    }
//                    Button(
//                        onClick = {
//                            if (isRunning) {
//                                // Set clockOut when stopping
//                                clock = clock?.copy(clockOut = LocalTime.now())
//                            }
//                            isRunning = false
//                        }
//                    ) {
//                        Icon(Icons.Filled.Refresh, contentDescription = "Stop")
//                    }
//                }
//                if (!isWithinDistance) {
//                    Text(
//                        text = "You must be within 100 meters of the company to start the timer",
//                        fontSize = 14.sp,
//                        color = Color.Red,
//                        textAlign = TextAlign.Center,
//                        modifier = Modifier.padding(top = 16.dp)
//                    )
//                }
//            }
//        }
//    }
//}
//
//fun calculateDistance(userLocation: LatLng, companyLocation: LatLng?): Float {
//    val result = FloatArray(1)
//    if (companyLocation != null) {
//        Location.distanceBetween(
//            userLocation.latitude, userLocation.longitude,
//            companyLocation.latitude, companyLocation.longitude,
//            result
//        )
//    }
//    return result[0] // Distance in meters
//}
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ClockPage(viewModel: MainViewModel, userLocation: LatLng?, modifier: Modifier = Modifier, fbDB: FBDatabase) {
    var isRunning by remember { mutableStateOf(false) }
    var clock by remember { mutableStateOf<Clock?>(null) }
    var distance by remember { mutableStateOf(0f) }
    var isWithinDistance by remember { mutableStateOf(false) }

    LaunchedEffect(userLocation, viewModel.selectedCompany?.location) {
        if (userLocation != null && viewModel.selectedCompany?.location != null) {
            val companyLocation = viewModel.selectedCompany?.location
            distance = calculateDistance(userLocation, companyLocation)
            isWithinDistance = distance <= 100 // Check if within 100 meters
        }
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
                    text = clock?.let { "Clock In: ${it.clockIn}, Clock Out: ${it.clockOut}" } ?: "Not Timed In",
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
                            if (!isRunning && isWithinDistance && viewModel.selectedCompany?.name?.isNotEmpty() == true) {

                                if (viewModel.selectedCompany != null && viewModel.currentClock == null) {
                                    clock = Clock(LocalDate.now(), LocalTime.now(), null) // Set clockIn
                                    viewModel.selectedCompany?.name?.let { fbDB.addClock(it, clock!!) }
                                    viewModel.onClockUpdated(clock = clock!!)
                                    isRunning = true
                                }
                            }
                        },
                        enabled = isWithinDistance && !isRunning
                    ) {
                        Icon(Icons.Filled.PlayArrow, contentDescription = "Start")
                    }
                    Button(
                        onClick = {
                            if (isRunning && isWithinDistance && viewModel.selectedCompany?.name?.isNotEmpty() == true) {
                                // Set clockOut when stopping
                                val updatedClock = clock?.copy(clockOut = LocalTime.now())
                                clock = updatedClock

                                // Save clock to Firebase
                                if (updatedClock != null && viewModel.currentClock != null) {
                                    viewModel.selectedCompany?.name?.let { fbDB.addClock(it, updatedClock) }
                                }
                            }
                            isRunning = false
                        }
                    ) {
                        Icon(Icons.Filled.Refresh, contentDescription = "Stop")
                    }
                }
                if (!isWithinDistance) {
                    Text(
                        text = "You must be within 100 meters of the company to start the timer",
                        fontSize = 14.sp,
                        color = Color.Red,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(top = 16.dp)
                    )
                }
            }
        }
    }
}

fun calculateDistance(userLocation: LatLng, companyLocation: LatLng?): Float {
    val result = FloatArray(1)
    if (companyLocation != null) {
        Location.distanceBetween(
            userLocation.latitude, userLocation.longitude,
            companyLocation.latitude, companyLocation.longitude,
            result
        )
    }
    return result[0] // Distance in meters
}
