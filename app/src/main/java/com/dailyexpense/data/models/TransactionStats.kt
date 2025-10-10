package com.dailyexpense.data.models

data class TransactionStats(
    val totalTransactions: Int = 0,
    val totalExpense: Double = 0.0,
    val totalIncome: Double = 0.0,
    val avgExpensePerDay: Double = 0.0,
    val avgExpensePerTransaction: Double = 0.0,
    val avgIncomePerDay: Double = 0.0,
    val avgIncomePerTransaction: Double = 0.0,
    val maxExpense: Double = 0.0,
    val maxIncome: Double = 0.0
)
