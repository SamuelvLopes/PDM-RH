package com.incompanyapp.ui.nav

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.google.android.gms.maps.model.LatLng
import com.incompanyapp.db.fb.FBDatabase
import com.incompanyapp.model.MainViewModel
import com.incompanyapp.ui.ActivityListPage
import com.incompanyapp.ui.ClockPage
import com.incompanyapp.ui.HomePage


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MainNavHost(
    navController: NavHostController,
    viewModel: MainViewModel,
    context: Context,
    fbDB: FBDatabase,
    userLocation: LatLng?,
    modifier: Modifier = Modifier
) {
    NavHost(navController, startDestination = BottomNavItem.HomePage.route) {
        // composable (route = NOME DESTA DESTINAÇÃO) { UI DA DESTINAÇÃO }
        composable(route = BottomNavItem.HomePage.route) {
            HomePage(
                modifier = modifier,
                viewModel = viewModel,
                context = context,
                fbDB = fbDB
            )
        }
        composable(route = BottomNavItem.ClockPage.route) {
            ClockPage(
                viewModel = viewModel,
                userLocation = userLocation,
                modifier = modifier,
                fbDB = fbDB
            )
        }
        composable(route = BottomNavItem.ActivityListPage.route) {
            ActivityListPage(
                modifier = modifier
            )
        }
    }
}
