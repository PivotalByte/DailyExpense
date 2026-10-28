package com.dailyexpense.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.dailyexpense.ui.screens.AccountListScreen
import com.dailyexpense.ui.screens.AddNewAccountScreen
import com.dailyexpense.ui.screens.CategoryListScreen
import com.dailyexpense.ui.screens.MainScreen
import com.dailyexpense.ui.screens.NewTransactionScreen
import com.dailyexpense.ui.screens.TagListScreen
import com.dailyexpense.ui.screens.TransactionDetailScreen

@Composable
fun AppNavigation(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Route.Main.route
    ) {
        composable(Route.Main.route) {
            MainScreen(navController)
        }
        composable(
            route = Route.NewTransaction.route,
            arguments = listOf(
                navArgument(ARG_TRANSACTION_ID) {
                    type = NavType.IntType
                    defaultValue = NO_ID
                }
            )
        ) {
            NewTransactionScreen(navController)
        }
        composable(
            route = Route.TransactionDetail.route,
            arguments = listOf(navArgument(ARG_TRANSACTION_ID) { type = NavType.IntType })
        ) {
            TransactionDetailScreen(navController)
        }
        composable(Route.AccountList.route) {
            AccountListScreen(navController)
        }
        composable(Route.CategoryList.route) {
            CategoryListScreen(navController)
        }
        composable(Route.TagList.route) {
            TagListScreen(navController)
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
