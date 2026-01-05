package com.dailyexpense.ui.actions

import com.dailyexpense.data.enums.TransactionCategory
import com.dailyexpense.data.enums.TransactionType
import com.dailyexpense.data.room.entity.TagEntity

data class NewTransactionActions(
    val onTransactionTypeChanged: (TransactionType) -> Unit = {},
    val onAmountChanged: (String) -> Unit = {},
    val onCategoryFieldClick: () -> Unit = {},
    val onAccountFieldClick: () -> Unit = {},
    val onPaymentMethodChanged: (TransactionCategory) -> Unit = {},
    val onNotesChanged: (String) -> Unit = {},
    val onDateFieldClick: () -> Unit = {},
    val onTimeFieldClick: () -> Unit = {},
    val onTagSearchChanged: (String) -> Unit = {},
    val onTagSelected: (TagEntity) -> Unit = {},
    val onTagRemoved: (TagEntity) -> Unit = {},
    val onCreateTag: (String) -> Unit = {},
    val onSaveClick: () -> Unit = {}
)
