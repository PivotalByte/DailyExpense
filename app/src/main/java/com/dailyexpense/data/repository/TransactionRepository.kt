package com.dailyexpense.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.room.withTransaction
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
import com.dailyexpense.data.room.db.AppDatabase
import com.dailyexpense.data.room.entity.TransactionEntity
import com.dailyexpense.data.room.junctions.TransactionTagCrossRef
import com.dailyexpense.data.room.wrapper.TransactionFull
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

interface TransactionRepository {
    suspend fun insertTransaction(transactionEntity: TransactionEntity): Long

    suspend fun insertTransactionWithTags(transaction: TransactionEntity, tagIds: List<Int>): Long

    /** Updates [transaction] in place and replaces its tags with [tagIds]. */
    suspend fun updateTransactionWithTags(transaction: TransactionEntity, tagIds: List<Int>)

    suspend fun getTransactionById(id: Int): TransactionFull?

    fun observeTransactionById(id: Int): Flow<TransactionFull?>

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
    private val database: AppDatabase,
    private val transactionDao: TransactionDao,
    private val tagDao: TagDao
) : TransactionRepository {

    override suspend fun insertTransaction(transactionEntity: TransactionEntity): Long =
        transactionDao.insertTransaction(transactionEntity)

    override suspend fun insertTransactionWithTags(transaction: TransactionEntity, tagIds: List<Int>): Long =
        database.withTransaction {
            val transactionId = transactionDao.insertTransaction(transaction)
            insertTags(transactionId.toInt(), tagIds)
            transactionId
        }

    override suspend fun updateTransactionWithTags(transaction: TransactionEntity, tagIds: List<Int>) =
        database.withTransaction {
            transactionDao.updateTransaction(transaction)
            tagDao.deleteTransactionTagCrossRefs(transaction.transactionId)
            insertTags(transaction.transactionId, tagIds)
        }

    private suspend fun insertTags(transactionId: Int, tagIds: List<Int>) {
        tagIds.forEach { tagId ->
            tagDao.insertTransactionTagCrossRef(
                TransactionTagCrossRef(
                    transactionId = transactionId,
                    tagId = tagId
                )
            )
        }
    }

    override suspend fun getTransactionById(id: Int): TransactionFull? = transactionDao.getTransactionFullById(id)

    override fun observeTransactionById(id: Int): Flow<TransactionFull?> =
        transactionDao.observeTransactionFullById(id)

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
