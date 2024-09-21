package com.incompanyapp.ui.nav

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomNavItem(var title: String, var icon: ImageVector, var route: String)
{
    data object HomePage : BottomNavItem("Início", Icons.Default.Home, "home")
    data object ClockPage : BottomNavItem("Clock", Icons.Filled.PlayArrow, "clock")
    data object ActivityListPage : BottomNavItem("Activity List", Icons.Default.LocationOn, "list")
}
