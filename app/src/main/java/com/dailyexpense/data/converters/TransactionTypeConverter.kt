package com.dailyexpense.data.converters

import androidx.room.TypeConverter
import com.dailyexpense.data.enums.TransactionType

class TransactionTypeConverter {

    @TypeConverter
    fun fromTransactionType(type: TransactionType): String {
        return type.name
    }

    @TypeConverter
    fun toTransactionType(value: String): TransactionType {
        return TransactionType.valueOf(value)
    }
}