package com.dailyexpense.data

import com.dailyexpense.data.enums.TransactionCategory
import com.dailyexpense.data.enums.TransactionType
import com.dailyexpense.data.models.ChipData
import com.dailyexpense.data.models.Duration
import com.dailyexpense.data.models.TransactionCategoryChipData
import com.dailyexpense.data.models.TransactionTypeChipData
import com.dailyexpense.data.room.entity.CategoryEntity
import com.dailyexpense.data.room.entity.TransactionEntity
import com.dailyexpense.utils.extensions.toReadableFormat
import java.util.Date

val fakeCategories = listOf(
    CategoryEntity(
        name = "Food",
        transactionType = TransactionType.EXPENSE,
        colorCode = "#FF9800",
        iconResName = "ic_food"
    ),
    CategoryEntity(
        name = "Transport",
        transactionType = TransactionType.EXPENSE,
        colorCode = "#03A9F4",
        iconResName = "ic_transport"
    ),
    CategoryEntity(
        name = "Shopping",
        transactionType = TransactionType.EXPENSE,
        colorCode = "#9C27B0",
        iconResName = "ic_shopping"
    ),
    CategoryEntity(
        name = "Salary",
        transactionType = TransactionType.INCOME,
        colorCode = "#4CAF50",
        iconResName = "ic_salary"
    )
)

val fakeTransactions = listOf(
    TransactionEntity(
        title = "Grocery",
        categoryId = 1,
        accountId = 1,
        date = Date(),
        transactionType = TransactionType.EXPENSE,
        transactionCategory = TransactionCategory.CASH,
        amount = -500.0
    ),
    TransactionEntity(
        title = "Salary",
        categoryId = 4,
        accountId = 1,
        date = Date(),
        transactionType = TransactionType.INCOME,
        transactionCategory = TransactionCategory.BANK_TRANSFER,
        amount = 15000.0
    ),
    TransactionEntity(
        title = "Fuel",
        categoryId = 2,
        accountId = 2,
        date = Date(),
        transactionType = TransactionType.EXPENSE,
        transactionCategory = TransactionCategory.UPI,
        amount = -1200.0
    )
)

val fakeDurationChipData = listOf(
    Duration.Today to "Today",
    Duration.ThisWeek to "Week",
    Duration.ThisMonth to "Month",
    Duration.ThisYear to "Year",
    Duration.Custom to "Custom",
)

val fakeCategoryChipData = listOf(
    ChipData(id = 0, label = "Groceries"),
    ChipData(id = 1, label = "Medical"),
    ChipData(id = 2, label = "Fuel and Transport"),
    ChipData(id = 3, label = "Restaurant"),
    ChipData(id = 4, label = "Salary"),
    ChipData(id = 5, label = "Business"),
    ChipData(id = 6, label = "Investments")
)

val fakeTransactionTypeChipData = listOf(
    TransactionTypeChipData(type = TransactionType.EXPENSE, label = "Expense"),
    TransactionTypeChipData(type = TransactionType.INCOME, label = "")
)

val fakeTransactionTypeChipDataNewTransaction = listOf(
    TransactionTypeChipData(
        type = TransactionType.EXPENSE,
        label = "Expense"
    ),
    TransactionTypeChipData(
        type = TransactionType.INCOME,
        label = "Income"
    ),
    TransactionTypeChipData(
        type = TransactionType.NEUTRAL,
        label = "Neutral"
    )
)
val fakeTransactionCategoryChipData = listOf(
    TransactionCategoryChipData(
        type = TransactionCategory.CASH,
        label = TransactionCategory.CASH.name.replaceFirstChar { it.uppercase() }
    ),
    TransactionCategoryChipData(
        type = TransactionCategory.CASH_WITHDRAWAL,
        label = TransactionCategory.CASH_WITHDRAWAL.name.toReadableFormat()
    ),
    TransactionCategoryChipData(
        type = TransactionCategory.DEBIT_CARD,
        label = TransactionCategory.DEBIT_CARD.name.toReadableFormat()
    ),
    TransactionCategoryChipData(
        type = TransactionCategory.CREDIT_CARD,
        label = TransactionCategory.CREDIT_CARD.name.toReadableFormat()
    ),
    TransactionCategoryChipData(
        type = TransactionCategory.UPI,
        label = TransactionCategory.UPI.name.toReadableFormat().uppercase()
    ),

    TransactionCategoryChipData(
        type = TransactionCategory.BANK_TRANSFER,
        label = TransactionCategory.BANK_TRANSFER.name.toReadableFormat()
    )
)

val fakeTagChipDataList = listOf(
    ChipData(id = 1, label = "Groceries"),
    ChipData(id = 2, label = "Fuel"),
    ChipData(id = 3, label = "Shopping"),
    ChipData(id = 4, label = "Electricity"),
    ChipData(id = 5, label = "Water"),
    ChipData(id = 6, label = "Internet"),
    ChipData(id = 7, label = "Mobile Recharge"),
    ChipData(id = 8, label = "EMI"),
    ChipData(id = 9, label = "Netflix"),
    ChipData(id = 10, label = "Spotify"),
    ChipData(id = 11, label = "Insurance"),
    ChipData(id = 12, label = "Education"),
    ChipData(id = 13, label = "Gym"),
    ChipData(id = 14, label = "Dining Out"),
    ChipData(id = 15, label = "Travel"),
    ChipData(id = 16, label = "Gifts"),
    ChipData(id = 17, label = "Entertainment"),
    ChipData(id = 18, label = "Pharmacy"),
    ChipData(id = 19, label = "Hospital"),
    ChipData(id = 20, label = "Investment"),
    ChipData(id = 21, label = "Savings"),
    ChipData(id = 22, label = "Charity"),
    ChipData(id = 23, label = "Home Rent"),
    ChipData(id = 24, label = "Maintenance")
)

val fakeDurationAnalyticsChipData = listOf(
    Duration.ThisWeek to "Week",
    Duration.ThisMonth to "Month",
    Duration.ThisYear to "Year",
    Duration.Custom to "Custom",
)
