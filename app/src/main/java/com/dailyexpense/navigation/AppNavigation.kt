package com.dailyexpense.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.dailyexpense.ui.screens.AccountListScreen
import com.dailyexpense.ui.screens.AddNewAccountScreen
import com.dailyexpense.ui.screens.MainScreen
import com.dailyexpense.ui.screens.NewTransactionScreen

@Composable
fun AppNavigation(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Route.Main.route
    ) {
        composable(Route.Main.route) {
            MainScreen(navController)
        }
        composable(Route.NewTransaction.route) {
            NewTransactionScreen(navController)
        }
        composable(Route.AccountList.route) {
            AccountListScreen(navController)
        }
        composable(
            route = Route.AddNewAccount.route,
            arguments = listOf(
                navArgument("accountId") {
                    type = NavType.IntType
                    defaultValue = -1
                }
            )
        ) { backStackEntry ->
            val accountId = backStackEntry.arguments?.getInt("accountId") ?: -1
            AddNewAccountScreen(
                navController = navController,
                accountId = accountId
            )
        }
    }
}
