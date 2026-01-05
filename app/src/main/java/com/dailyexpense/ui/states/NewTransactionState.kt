package com.dailyexpense.ui.states

import com.dailyexpense.data.enums.TransactionCategory
import com.dailyexpense.data.enums.TransactionType
import com.dailyexpense.data.models.TransactionTypeChipData
import com.dailyexpense.data.room.entity.AccountEntity
import com.dailyexpense.data.room.entity.CategoryEntity
import com.dailyexpense.data.room.entity.TagEntity

data class NewTransactionState(
    val transactionType: TransactionType,
    val amount: String,
    val selectedCategory: CategoryEntity?,
    val selectedAccount: AccountEntity?,
    val selectedPaymentMethod: TransactionCategory?,
    val notes: String,
    val selectedDate: Long,
    val selectedHour: Int,
    val selectedMinute: Int,
    val transactionTypeChipData: List<TransactionTypeChipData>,
    val selectedTags: List<TagEntity>,
    val tagSearchQuery: String,
    val suggestedTags: List<TagEntity>
)
