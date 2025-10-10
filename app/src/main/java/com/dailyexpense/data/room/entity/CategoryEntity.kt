package com.dailyexpense.data.room.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.dailyexpense.data.enums.TransactionType

@Entity(tableName = "DE_category")
data class CategoryEntity(
    @PrimaryKey(autoGenerate = true)
    val categoryId: Int = 0,
    val name: String,
    val transactionType: TransactionType,
    val colorCode: String,
    val iconResName: String
) {
    constructor(name: String, transactionType: TransactionType, colorCode: String, iconResName: String) : this(
        categoryId = 0,
        name = name,
        transactionType = transactionType,
        colorCode = colorCode,
        iconResName = iconResName
    )
}
