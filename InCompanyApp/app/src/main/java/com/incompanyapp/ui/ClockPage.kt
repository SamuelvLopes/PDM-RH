package com.incompanyapp.ui

import android.location.Location
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
import com.incompanyapp.model.MainViewModel
import kotlinx.coroutines.delay

@Composable
fun ClockPage(viewModel: MainViewModel, userLocation: LatLng?, modifier: Modifier = Modifier) {
    var isRunning by remember { mutableStateOf(false) }
    var elapsedTime by remember { mutableStateOf(0) }
    var isPaused by remember { mutableStateOf(false) }
    val timer = remember { mutableStateOf(0L) }
    var distance by remember { mutableStateOf(0f) }
    val coroutineScope = rememberCoroutineScope()
    var isWithinDistance by remember { mutableStateOf(false) }

    LaunchedEffect(userLocation, viewModel.selectedCompany.value?.location) {
        if (userLocation != null && viewModel.selectedCompany.value?.location != null) {
            val companyLocation = viewModel.selectedCompany.value?.location
            distance = calculateDistance(userLocation, companyLocation)
            isWithinDistance = distance <= 100 // Check if within 25 meters
        }
    }

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
                            if (!isRunning && isWithinDistance) {
                                timer.value = System.currentTimeMillis()
                                isRunning = true
                                isPaused = false
                            }
                        },
                        enabled = isWithinDistance
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
                if (!isWithinDistance) {
                    Text(
                        text = "You must be within 25 meters of the company to start the timer ${viewModel.selectedCompany.value?.location} e $distance",
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
