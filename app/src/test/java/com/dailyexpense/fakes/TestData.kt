package com.dailyexpense.fakes

import com.dailyexpense.data.enums.TransactionCategory
import com.dailyexpense.data.enums.TransactionType
import com.dailyexpense.data.models.AccountWithBalance
import com.dailyexpense.data.room.entity.AccountEntity
import com.dailyexpense.data.room.entity.CategoryEntity
import com.dailyexpense.data.room.entity.TagEntity
import com.dailyexpense.data.room.entity.TransactionEntity
import com.dailyexpense.data.room.wrapper.TransactionFull
import java.util.Calendar

val groceries = CategoryEntity(
    categoryId = 1,
    name = "Groceries",
    transactionType = TransactionType.EXPENSE,
    colorCode = "#4CAF50",
    iconResName = "ic_category_groceries"
)

val axisAccount = AccountEntity(
    accountId = 3,
    accountHolderName = "Holder",
    accountBankName = "Axis Bank",
    accountNumber = "1234",
    openingBalance = 1000.0
)

val axisAccountWithBalance = AccountWithBalance(account = axisAccount, currentBalance = 750.0, transactionCount = 1)

val familyTag = TagEntity(tagId = 9, name = "Family")

/** 12.50 spent on 5 Mar 2026 at 18:45. */
val groceryTransaction = TransactionFull(
    transaction = TransactionEntity(
        transactionId = 42,
        title = "Weekly groceries",
        categoryId = groceries.categoryId,
        accountId = axisAccount.accountId,
        date = Calendar.getInstance().apply {
            clear()
            set(2026, Calendar.MARCH, 5, 18, 45)
        }.time,
        transactionType = TransactionType.EXPENSE,
        transactionCategory = TransactionCategory.UPI,
        amount = 12.5
    ),
    category = groceries,
    account = axisAccount,
    tags = listOf(familyTag)
)
