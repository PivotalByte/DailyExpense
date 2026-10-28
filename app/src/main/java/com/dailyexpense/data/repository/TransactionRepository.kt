package com.dailyexpense.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.dailyexpense.data.enums.SortOption
import com.dailyexpense.data.enums.SortOrder
import com.dailyexpense.data.enums.TransactionType
import com.dailyexpense.data.models.DailyTransactionSummary
import com.dailyexpense.data.models.FilterState
import com.dailyexpense.data.models.SortState
import com.dailyexpense.data.models.TransactionCategorySummary
import com.dailyexpense.data.models.TransactionStats
import com.dailyexpense.data.room.dao.TagDao
import com.dailyexpense.data.room.dao.TransactionDao
import com.dailyexpense.data.room.entity.TransactionEntity
import com.dailyexpense.data.room.junctions.TransactionTagCrossRef
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

interface TransactionRepository {
    suspend fun insertTransaction(transactionEntity: TransactionEntity): Long

    suspend fun insertTransactionWithTags(transaction: TransactionEntity, tagIds: List<Int>): Long

    suspend fun deleteTransaction(transactionEntity: TransactionEntity)

    fun getRecentTransaction(limit: Int): Flow<List<TransactionEntity>>

    fun getPagedTransactions(
        filterState: FilterState,
        sortState: SortState,
        query: String
    ): Pager<Int, TransactionEntity>

    fun getTotalExpenseForRange(startDate: Long, endDate: Long): Flow<Double>

    fun getTotalIncome(startDate: Long, endDate: Long): Flow<Double>

    fun getTransactionCategoryExpenseSummary(startDate: Long, endDate: Long): Flow<List<TransactionCategorySummary>>

    fun getTransactionCategoryIncomeSummary(startDate: Long, endDate: Long): Flow<List<TransactionCategorySummary>>

    fun getStats(startDate: Long, endDate: Long): Flow<TransactionStats>

    fun getDailyTransactionSummary(startDate: Long, endDate: Long): Flow<List<DailyTransactionSummary>>
}

@Singleton
class RoomTransactionRepository @Inject constructor(
    private val transactionDao: TransactionDao,
    private val tagDao: TagDao
) : TransactionRepository {

    override suspend fun insertTransaction(transactionEntity: TransactionEntity): Long =
        transactionDao.insertTransaction(transactionEntity)

    override suspend fun insertTransactionWithTags(transaction: TransactionEntity, tagIds: List<Int>): Long {
        val transactionId = transactionDao.insertTransaction(transaction)
        tagIds.forEach { tagId ->
            tagDao.insertTransactionTagCrossRef(
                TransactionTagCrossRef(
                    transactionId = transactionId.toInt(),
                    tagId = tagId
                )
            )
        }
        return transactionId
    }

    override suspend fun deleteTransaction(transactionEntity: TransactionEntity) =
        transactionDao.deleteTransaction(transactionEntity)

    override fun getRecentTransaction(limit: Int): Flow<List<TransactionEntity>> =
        transactionDao.getRecentTransaction(limit)

    override fun getPagedTransactions(
        filterState: FilterState,
        sortState: SortState,
        query: String
    ): Pager<Int, TransactionEntity> {
        // Determine sort field
        val sortField = when (sortState.sortOption) {
            SortOption.DATE -> "DATE"
            SortOption.AMOUNT -> "AMOUNT"
        }

        // Determine sort order
        val sortOrder = when (sortState.sortOrder) {
            SortOrder.ASCENDING -> "ASC"
            SortOrder.DESCENDING -> "DESC"
        }

        return Pager(
            config = PagingConfig(pageSize = 20, enablePlaceholders = false),
            pagingSourceFactory = {
                transactionDao.getPagedTransactions(
                    search = "%$query%",
                    startDate = filterState.startDate,
                    endDate = filterState.endDate,
                    categoryIds = filterState.selectedCategories.toList(),
                    categoryIdsSize = filterState.selectedCategories.size,
                    transactionTypes = filterState.selectedTransactionTypes.toList(),
                    transactionTypesSize = filterState.selectedTransactionTypes.size,
                    transactionCategories = filterState.selectedTransactionCategories.toList(),
                    transactionCategoriesSize = filterState.selectedTransactionCategories.size,
                    tagIds = filterState.selectedTags.toList(),
                    tagIdsSize = filterState.selectedTags.size,
                    sortField = sortField,
                    sortOrder = sortOrder
                )
            }
        )
    }

    override fun getTotalExpenseForRange(startDate: Long, endDate: Long): Flow<Double> =
        transactionDao.getTotalForRangeByTransactionType(
            startDate = startDate,
            endDate = endDate,
            transactionType = TransactionType.EXPENSE
        )

    override fun getTotalIncome(startDate: Long, endDate: Long): Flow<Double> =
        transactionDao.getTotalForRangeByTransactionType(
            startDate = startDate,
            endDate = endDate,
            transactionType = TransactionType.INCOME
        )

    override fun getTransactionCategoryExpenseSummary(
        startDate: Long,
        endDate: Long
    ): Flow<List<TransactionCategorySummary>> =
        transactionDao.getTransactionCategorySummaryBetweenDatesByType(
            transactionType = TransactionType.EXPENSE,
            startDate = startDate,
            endDate = endDate
        )

    override fun getTransactionCategoryIncomeSummary(
        startDate: Long,
        endDate: Long
    ): Flow<List<TransactionCategorySummary>> =
        transactionDao.getTransactionCategorySummaryBetweenDatesByType(
            transactionType = TransactionType.INCOME,
            startDate = startDate,
            endDate = endDate
        )

    override fun getStats(startDate: Long, endDate: Long): Flow<TransactionStats> =
        transactionDao.getTransactionStats(startDate = startDate, endDate = endDate)

    override fun getDailyTransactionSummary(startDate: Long, endDate: Long): Flow<List<DailyTransactionSummary>> =
        transactionDao.getDailyTransactionSummary(startDate = startDate, endDate = endDate)
}
