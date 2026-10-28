package com.dailyexpense.data.room.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * [openingBalance] is the balance before any recorded transaction. The current balance is
 * derived from it plus the account's income minus its expenses (see [com.dailyexpense.data.models.AccountWithBalance]).
 * The column keeps its original name `balance` so existing databases need no table rebuild.
 */
@Entity(tableName = "DE_account")
data class AccountEntity(
    @PrimaryKey(autoGenerate = true)
    val accountId: Int = 0,
    val accountHolderName: String,
    val accountBankName: String,
    val accountNumber: String,
    @ColumnInfo(name = "balance")
    val openingBalance: Double
)
