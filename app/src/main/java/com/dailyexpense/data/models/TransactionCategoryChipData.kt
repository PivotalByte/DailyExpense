package com.dailyexpense.data.models

import com.dailyexpense.data.enums.TransactionCategory


data class TransactionCategoryChipData(
    val type: TransactionCategory,
    val label: String
)