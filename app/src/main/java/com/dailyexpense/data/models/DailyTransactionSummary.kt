package com.dailyexpense.data.models

data class DailyTransactionSummary(
    val date: Long,
    val totalIncome: Double,
    val totalExpense: Double
)