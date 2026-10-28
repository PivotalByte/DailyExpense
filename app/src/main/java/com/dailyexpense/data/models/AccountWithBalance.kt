package com.dailyexpense.data.models

import androidx.room.Embedded
import com.dailyexpense.data.room.entity.AccountEntity

data class AccountWithBalance(
    @Embedded val account: AccountEntity,
    val currentBalance: Double
)
