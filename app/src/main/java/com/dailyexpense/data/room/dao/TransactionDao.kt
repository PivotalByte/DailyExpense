package com.dailyexpense.data.room.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.dailyexpense.data.enums.TransactionCategory
import com.dailyexpense.data.enums.TransactionType
import com.dailyexpense.data.models.DailyTransactionSummary
import com.dailyexpense.data.models.TransactionCategorySummary
import com.dailyexpense.data.models.TransactionStats
import com.dailyexpense.data.room.entity.TransactionEntity
import com.dailyexpense.data.room.wrapper.TransactionFull
import com.dailyexpense.data.room.wrapper.TransactionWithTags
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transactionEntity: TransactionEntity): Long

    @Delete
    suspend fun deleteTransaction(transactionEntity: TransactionEntity)

    @Query("SELECT * FROM DE_transaction ORDER BY date DESC LIMIT :limit")
    fun getRecentTransaction(limit: Int): Flow<List<TransactionEntity>>

    @Query(
        """
    SELECT * FROM DE_transaction
    WHERE date BETWEEN :startDate AND :endDate
    ORDER BY date DESC
"""
    )
    fun getPagedTransactionsByDateRange(
        startDate: Long,
        endDate: Long
    ): PagingSource<Int, TransactionEntity>

    @Query(
        """
    SELECT * FROM DE_transaction ORDER BY date DESC
"""
    )
    fun getAllPagedTransactions(): PagingSource<Int, TransactionEntity>

    @Query(
        """
    SELECT DISTINCT t.* FROM DE_transaction t
    LEFT JOIN DE_transaction_tag_cross_ref ttcr ON t.transactionId = ttcr.transactionId
    WHERE (:search IS NULL OR t.title LIKE '%' || :search || '%')
      AND (:startDate IS NULL OR t.date >= :startDate)
      AND (:endDate IS NULL OR t.date <= :endDate)
      AND (:categoryIdsSize = 0 OR t.categoryId IN (:categoryIds))
      AND (:transactionTypesSize = 0 OR t.transactionType IN (:transactionTypes))
      AND (:transactionCategoriesSize = 0 OR t.transactionCategory IN (:transactionCategories))
      AND (:tagIdsSize = 0 OR ttcr.tagId IN (:tagIds))
    ORDER BY 
      CASE 
        WHEN :sortField = 'DATE' AND :sortOrder = 'ASC' THEN t.date
        WHEN :sortField = 'DATE' AND :sortOrder = 'DESC' THEN -t.date
      END,
      CASE 
        WHEN :sortField = 'AMOUNT' AND :sortOrder = 'ASC' THEN t.amount
        WHEN :sortField = 'AMOUNT' AND :sortOrder = 'DESC' THEN -t.amount
      END
"""
    )
    fun getPagedTransactions(
        search: String?,
        startDate: Long?,
        endDate: Long?,
        categoryIds: List<Int>,
        categoryIdsSize: Int,
        transactionTypes: List<TransactionType>,
        transactionTypesSize: Int,
        transactionCategories: List<TransactionCategory>,
        transactionCategoriesSize: Int,
        tagIds: List<Int>,
        tagIdsSize: Int,
        sortField: String,
        sortOrder: String
    ): PagingSource<Int, TransactionEntity>

    @Transaction
    @Query("SELECT * FROM DE_transaction WHERE transactionId = :id")
    suspend fun getTransactionWithTags(id: Int): TransactionWithTags

    @Transaction
    @Query("SELECT * FROM DE_transaction WHERE transactionId = :id")
    suspend fun getTransactionFullById(id: Int): TransactionFull

    @Transaction
    @Query("SELECT * FROM DE_transaction ORDER BY date DESC")
    fun getAllTransactionsFull(): Flow<List<TransactionFull>>

    @Query(
        """
        SELECT ABS(SUM(amount)) FROM DE_transaction
        WHERE transactionType = :transactionType
        AND date BETWEEN :startDate AND :endDate
    """
    )
    fun getTotalForRangeByTransactionType(
        startDate: Long,
        endDate: Long,
        transactionType: TransactionType
    ): Flow<Double>

    @Query(
        """
    SELECT t.transactionCategory AS transactionCategory, ABS(SUM(t.amount)) AS totalAmount
    FROM DE_transaction t
    WHERE t.date BETWEEN :startDate AND :endDate AND t.transactionType = :transactionType
    GROUP BY t.transactionCategory
    HAVING totalAmount > 0
"""
    )
    fun getTransactionCategorySummaryBetweenDatesByType(
        transactionType: TransactionType,
        startDate: Long,
        endDate: Long
    ): Flow<List<TransactionCategorySummary>>

    @Query(
        """
    SELECT
        COUNT(*) AS totalTransactions,
        ABS(SUM(CASE WHEN transactionType = :expenseType THEN amount ELSE 0 END)) AS totalExpense,
        SUM(CASE WHEN transactionType = :incomeType THEN amount ELSE 0 END) AS totalIncome,

        -- average expense per day
        (ABS(SUM(CASE WHEN transactionType = :expenseType THEN amount ELSE 0 END)) /
         CAST(((:endDate - :startDate) / (1000*60*60*24) + 1) AS REAL)) AS avgExpensePerDay,

        -- average expense per transaction
        (ABS(SUM(CASE WHEN transactionType = :expenseType THEN amount ELSE 0 END)) /
         NULLIF(COUNT(CASE WHEN transactionType = :expenseType THEN 1 END), 0)) AS avgExpensePerTransaction,

        -- average income per day
        (SUM(CASE WHEN transactionType = :incomeType THEN amount ELSE 0 END) /
         CAST(((:endDate - :startDate) / (1000*60*60*24) + 1) AS REAL)) AS avgIncomePerDay,

        -- average income per transaction
        (SUM(CASE WHEN transactionType = :incomeType THEN amount ELSE 0 END) /
         NULLIF(COUNT(CASE WHEN transactionType = :incomeType THEN 1 END), 0)) AS avgIncomePerTransaction,

        ABS(MIN(CASE WHEN transactionType = :expenseType THEN amount END)) AS maxExpense,
        MAX(CASE WHEN transactionType = :incomeType THEN amount END) AS maxIncome

    FROM DE_transaction
    WHERE date BETWEEN :startDate AND :endDate
"""
    )
    fun getTransactionStats(
        expenseType: TransactionType = TransactionType.EXPENSE,
        incomeType: TransactionType = TransactionType.INCOME,
        startDate: Long,
        endDate: Long
    ): Flow<TransactionStats>

    @Query(
        """
    SELECT
        date,
        SUM(CASE WHEN transactionType = :incomeType THEN amount ELSE 0 END) AS totalIncome,
        ABS(SUM(CASE WHEN transactionType = :expenseType THEN amount ELSE 0 END)) AS totalExpense
    FROM DE_transaction
    WHERE date BETWEEN :startDate AND :endDate
    GROUP BY strftime('%Y-%m-%d', date / 1000, 'unixepoch')
    ORDER BY date ASC
"""
    )
    fun getDailyTransactionSummary(
        expenseType: TransactionType = TransactionType.EXPENSE,
        incomeType: TransactionType = TransactionType.INCOME,
        startDate: Long,
        endDate: Long
    ): Flow<List<DailyTransactionSummary>>
}
