package com.dailyexpense.data.converters

import androidx.room.TypeConverter
import com.dailyexpense.data.enums.TransactionCategory
import com.dailyexpense.data.enums.TransactionType

class TransactionCategoryConverter {

    @TypeConverter
    fun fromTransactionCategory(category: TransactionCategory): String {
        return category.name
    }

    @TypeConverter
    fun toTransactionCategory(value: String): TransactionCategory {
        return TransactionCategory.valueOf(value)
    }
}