package com.dailyexpense.data.export

import com.dailyexpense.data.room.entity.TagEntity
import com.dailyexpense.fakes.groceryTransaction
import org.junit.Assert.assertEquals
import org.junit.Test
import java.util.TimeZone

class TransactionCsvWriterTest {

    // Test data is built in the default zone, so format in the same zone
    private val writer = TransactionCsvWriter(TimeZone.getDefault())

    private fun csvLines(vararg transactions: com.dailyexpense.data.room.wrapper.TransactionFull): List<String> =
        StringBuilder().also { writer.write(transactions.toList(), it) }.toString().split("\r\n").dropLast(1)

    @Test
    fun `writes a header and one row per transaction`() {
        val lines = csvLines(groceryTransaction)

        assertEquals("Date,Time,Title,Type,Amount,Category,Account,Payment mode,Tags", lines[0])
        assertEquals("2026-03-05,18:45,Weekly groceries,Expense,12.50,Groceries,Axis Bank,UPI,Family", lines[1])
        assertEquals(2, lines.size)
    }

    @Test
    fun `quotes fields containing commas or quotes and joins tags`() {
        val tricky = groceryTransaction.copy(
            transaction = groceryTransaction.transaction.copy(title = "Milk, \"organic\""),
            tags = listOf(TagEntity(1, "Family"), TagEntity(2, "Weekly"))
        )

        val row = csvLines(tricky)[1]

        assertEquals(
            "2026-03-05,18:45,\"Milk, \"\"organic\"\"\",Expense,12.50,Groceries,Axis Bank,UPI,Family; Weekly",
            row
        )
    }

    @Test
    fun `prefixes text that a spreadsheet would run as a formula`() {
        val formula = groceryTransaction.copy(
            transaction = groceryTransaction.transaction.copy(title = "=HYPERLINK(\"x\")")
        )

        val title = csvLines(formula)[1].split(",")[2]

        assertEquals("\"'=HYPERLINK(\"\"x\"\")\"", title)
    }
}
