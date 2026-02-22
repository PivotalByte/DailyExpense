package com.dailyexpense.navigation

sealed class Route(val route: String) {

    object Main : Route("main")

    object NewTransaction : Route("new_transaction")

    object AccountList : Route("account_list")

    object AddNewAccount : Route("add_new_account/{accountId}") {
        fun createRoute(accountId: Int = -1) = "add_new_account/$accountId"
    }
}
