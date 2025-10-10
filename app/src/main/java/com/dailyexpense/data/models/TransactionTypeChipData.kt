package com.dailyexpense.data.models

import com.dailyexpense.data.enums.TransactionType

data class TransactionTypeChipData(
    val type: TransactionType,
    val label: String
)
