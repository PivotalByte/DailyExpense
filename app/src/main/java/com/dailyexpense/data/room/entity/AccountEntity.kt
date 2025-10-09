package com.dailyexpense.data.room.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "DE_account")
data class AccountEntity(
    @PrimaryKey
    val accountId: Int = 0,
    val accountHolderName: String,
    val accountBankName: String,
    val accountNumber: String,
    val balance: Double
) {
    constructor(
        accountHolderName: String,
        accountBankName: String,
        accountNumber: String,
        balance: Double
    ) : this(
        accountId = 0,
        accountHolderName = accountHolderName,
        accountBankName = accountBankName,
        accountNumber = accountNumber,
        balance = balance
    )
}
