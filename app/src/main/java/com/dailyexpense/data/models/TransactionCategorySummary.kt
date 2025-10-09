package com.dailyexpense.data.models

import com.dailyexpense.data.enums.TransactionCategory

data class TransactionCategorySummary(
    val transactionCategory: TransactionCategory,
    val totalAmount: Double
)