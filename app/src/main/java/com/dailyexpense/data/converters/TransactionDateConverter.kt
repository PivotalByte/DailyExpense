package com.dailyexpense.data.converters

import androidx.room.TypeConverter
import java.util.Date

class TransactionDateConverter {
    @TypeConverter
    fun fromDate(date: Date): Long = date.time

    @TypeConverter
    fun toDate(timestamp: Long): Date = Date(timestamp)
}
