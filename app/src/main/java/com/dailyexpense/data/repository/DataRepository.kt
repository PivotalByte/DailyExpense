package com.dailyexpense.data.repository

import android.content.Context
import androidx.room.withTransaction
import com.dailyexpense.data.export.TransactionCsvWriter
import com.dailyexpense.data.room.dao.AccountDao
import com.dailyexpense.data.room.dao.TagDao
import com.dailyexpense.data.room.dao.TransactionDao
import com.dailyexpense.data.room.db.AppDatabase
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

data class ExportResult(val file: File, val transactionCount: Int)

interface DataRepository {
    /** Writes every transaction to a CSV file in the app cache, ready to share. */
    suspend fun exportTransactionsCsv(): ExportResult

    /** Deletes all accounts, transactions and tags. Categories are kept. */
    suspend fun clearAllData()
}

@Singleton
class RoomDataRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    private val database: AppDatabase,
    private val transactionDao: TransactionDao,
    private val tagDao: TagDao,
    private val accountDao: AccountDao
) : DataRepository {

    override suspend fun exportTransactionsCsv(): ExportResult {
        val transactions = transactionDao.getAllTransactionsFullOnce()
        return withContext(Dispatchers.IO) {
            val dir = File(context.cacheDir, EXPORT_DIR).apply { mkdirs() }
            // Only the latest export is needed; older ones were already shared
            dir.listFiles()?.forEach { it.delete() }
            val stamp = SimpleDateFormat("yyyyMMdd-HHmm", Locale.US).format(Date())
            val file = File(dir, "daily-expense-$stamp.csv")
            file.bufferedWriter().use { TransactionCsvWriter().write(transactions, it) }
            ExportResult(file = file, transactionCount = transactions.size)
        }
    }

    override suspend fun clearAllData() = database.withTransaction {
        // Tag links cascade from their transactions
        transactionDao.deleteAllTransactions()
        tagDao.deleteAllTags()
        accountDao.deleteAllAccounts()
    }

    companion object {
        /** Must match the cache-path in res/xml/file_paths.xml. */
        const val EXPORT_DIR = "exports"
    }
}
