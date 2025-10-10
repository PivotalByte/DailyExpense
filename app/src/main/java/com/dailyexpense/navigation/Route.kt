package com.dailyexpense.navigation

sealed class Route(val route: String) {

    object Main : Route("main")

    object NewTransaction : Route("new_transaction")
}
