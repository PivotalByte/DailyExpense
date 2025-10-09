package com.dailyexpense.data.room.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.dailyexpense.data.enums.TransactionCategory
import com.dailyexpense.data.enums.TransactionType
import java.util.Date

@Entity(
    tableName = "DE_transaction",
    foreignKeys = [
        ForeignKey(
            entity = CategoryEntity::class,
            parentColumns = ["categoryId"],
            childColumns = ["categoryId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = AccountEntity::class,
            parentColumns = ["accountId"],
            childColumns = ["accountId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["categoryId", "accountId"])]
)
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true)
    val transactionId: Int = 0,
    val title: String,
    val categoryId: Int,
    val accountId: Int,
    val date: Date,
    val transactionType: TransactionType,
    val transactionCategory: TransactionCategory,
    val amount: Double
) {
    constructor(
        title: String,
        categoryId: Int,
        accountId: Int,
        date: Date,
        transactionType: TransactionType,
        transactionCategory: TransactionCategory,
        amount: Double
    ) : this(
        transactionId = 0,
        title = title,
        categoryId = categoryId,
        accountId = accountId,
        date = date,
        transactionType = transactionType,
        transactionCategory = transactionCategory,
        amount = amount
    )
}
