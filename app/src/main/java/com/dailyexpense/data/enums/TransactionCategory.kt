package com.dailyexpense.data.enums

import android.content.Context
import com.dailyexpense.R

enum class TransactionCategory {
    CASH,
    CASH_WITHDRAWAL,
    DEBIT_CARD,
    CREDIT_CARD,
    UPI,
    SALARY,
    BUSINESS,
    INVESTMENTS,
    BANK_TRANSFER
}

fun TransactionCategory.getTransactionCategoryAsString(context: Context): String {
    return when (this) {
        TransactionCategory.CASH -> context.getString(R.string.label_by_cash)
        TransactionCategory.CASH_WITHDRAWAL -> context.getString(R.string.label_cash_withdrawn)
        TransactionCategory.DEBIT_CARD -> context.getString(R.string.label_by_debit_card)
        TransactionCategory.CREDIT_CARD -> context.getString(R.string.label_by_credit_card)
        TransactionCategory.UPI -> context.getString(R.string.label_by_upi)
        TransactionCategory.SALARY -> context.getString(R.string.label_salary)
        TransactionCategory.BUSINESS -> context.getString(R.string.label_from_business)
        TransactionCategory.BANK_TRANSFER -> context.getString(R.string.label_bank_transfer)
        TransactionCategory.INVESTMENTS -> context.getString(R.string.label_investments)
    }
}
