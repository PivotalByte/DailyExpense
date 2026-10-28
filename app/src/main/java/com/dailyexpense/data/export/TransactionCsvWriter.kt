package com.dailyexpense.data.export

import com.dailyexpense.data.enums.TransactionCategory
import com.dailyexpense.data.room.wrapper.TransactionFull
import com.dailyexpense.utils.extensions.toReadableFormat
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

/** Writes transactions as RFC 4180 CSV, one row per transaction, newest first as given. */
class TransactionCsvWriter(timeZone: TimeZone = TimeZone.getDefault()) {

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US).apply { this.timeZone = timeZone }
    private val timeFormat = SimpleDateFormat("HH:mm", Locale.US).apply { this.timeZone = timeZone }

    fun write(transactions: List<TransactionFull>, out: Appendable) {
        out.appendRow(HEADER)
        transactions.forEach { full ->
            val transaction = full.transaction
            out.appendRow(
                listOf(
                    dateFormat.format(transaction.date),
                    timeFormat.format(transaction.date),
                    text(transaction.title),
                    transaction.transactionType.name.toReadableFormat(),
                    String.format(Locale.US, "%.2f", transaction.amount),
                    text(full.category?.name.orEmpty()),
                    text(full.account?.accountBankName.orEmpty()),
                    paymentModeLabel(transaction.transactionCategory),
                    text(full.tags.joinToString(separator = "; ") { it.name })
                )
            )
        }
    }

    private fun paymentModeLabel(mode: TransactionCategory): String = when (mode) {
        TransactionCategory.UPI -> "UPI"
        else -> mode.name.toReadableFormat()
    }

    private fun Appendable.appendRow(values: List<String>) {
        append(values.joinToString(separator = ",") { escape(it) })
        append("\r\n")
    }

    private fun escape(value: String): String =
        if (value.any { it in NEEDS_QUOTING }) {
            "\"" + value.replace("\"", "\"\"") + "\""
        } else {
            value
        }

    /**
     * User-entered text that starts like a formula would run as one when the file is
     * opened in a spreadsheet, so it is prefixed with an apostrophe.
     */
    private fun text(value: String): String =
        if (value.firstOrNull() in FORMULA_PREFIXES) "'$value" else value

    companion object {
        val HEADER = listOf("Date", "Time", "Title", "Type", "Amount", "Category", "Account", "Payment mode", "Tags")
        private val NEEDS_QUOTING = setOf(',', '"', '\n', '\r')
        private val FORMULA_PREFIXES = setOf('=', '+', '-', '@', '\t', '\r')
    }
}
