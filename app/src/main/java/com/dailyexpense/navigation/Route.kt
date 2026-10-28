package com.dailyexpense.navigation

const val ARG_TRANSACTION_ID = "transactionId"
const val NO_ID = -1

sealed class Route(val route: String) {

    object Main : Route("main")

    /** Adds a new transaction, or edits an existing one when a transaction id is passed. */
    object NewTransaction : Route("new_transaction?$ARG_TRANSACTION_ID={$ARG_TRANSACTION_ID}") {
        fun createRoute(transactionId: Int = NO_ID) = "new_transaction?$ARG_TRANSACTION_ID=$transactionId"
    }

    object TransactionDetail : Route("transaction_detail/{$ARG_TRANSACTION_ID}") {
        fun createRoute(transactionId: Int) = "transaction_detail/$transactionId"
    }

    object AccountList : Route("account_list")

    object CategoryList : Route("category_list")

    object TagList : Route("tag_list")

    object AddNewAccount : Route("add_new_account/{accountId}") {
        fun createRoute(accountId: Int = -1) = "add_new_account/$accountId"
    }
}
