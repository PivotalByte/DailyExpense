package com.dailyexpense.data.models

import com.dailyexpense.R

sealed class MainTab(val labelRes: Int, val icon: Int) {
    object Dashboard : MainTab(labelRes = R.string.label_dashboard, icon = R.drawable.ic_dashboard)
    object Transactions : MainTab(labelRes = R.string.label_transactions, icon = R.drawable.ic_transaction)
    object Analytics : MainTab(labelRes = R.string.label_analytics, icon = R.drawable.ic_analytics)
    object Account : MainTab(labelRes = R.string.label_account, icon = R.drawable.ic_account)
}
