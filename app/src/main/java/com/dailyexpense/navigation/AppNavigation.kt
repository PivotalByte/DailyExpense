package com.dailyexpense.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.dailyexpense.ui.screens.MainScreen
import com.dailyexpense.ui.screens.NewTransactionScreen

@Composable
fun AppNavigation(navController: NavHostController){

    NavHost(
        navController = navController, startDestination = Route.Main.route
    ) {
        composable(Route.Main.route) {
            MainScreen(navController)
        }
        composable(Route.NewTransaction.route) {
            NewTransactionScreen(navController)
        }
    }

}