package com.dailyexpense.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.dailyexpense.data.enums.SortOption
import com.dailyexpense.data.enums.SortOrder
import com.dailyexpense.data.enums.TransactionType
import com.dailyexpense.data.models.CategorySummary
import com.dailyexpense.data.models.DailyTransactionSummary
import com.dailyexpense.data.models.FilterState
import com.dailyexpense.data.models.SortState
import com.dailyexpense.data.models.TransactionCategorySummary
import com.dailyexpense.data.models.TransactionStats
import com.dailyexpense.data.room.dao.AccountDao
import com.dailyexpense.data.room.dao.CategoryDao
import com.dailyexpense.data.room.dao.TagDao
import com.dailyexpense.data.room.dao.TransactionDao
import com.dailyexpense.data.room.entity.CategoryEntity
import com.dailyexpense.data.room.entity.TagEntity
import com.dailyexpense.data.room.entity.TransactionEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DatabaseRepository @Inject constructor(
    private val transactionDao: TransactionDao,
    private val categoryDao: CategoryDao,
    private val accountDao: AccountDao,
    private val tagDao: TagDao
) {
    suspend fun insertTransaction(transactionEntity: TransactionEntity) =
        transactionDao.insertTransaction(transactionEntity)

    suspend fun deleteTransaction(transactionEntity: TransactionEntity) =
        transactionDao.deleteTransaction(transactionEntity)

    fun getRecentTransaction(limit: Int): Flow<List<TransactionEntity>> =
        transactionDao.getRecentTransaction(limit)

    fun getPagedTransactions(
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

    fun getCategoriesByType(type: TransactionType): Flow<List<CategoryEntity>> =
        categoryDao.getCategoriesByType(type)

    fun getAllCategories(): Flow<List<CategoryEntity>> =
        categoryDao.getAllCategory()

    fun getTotalBalance(): Flow<Double> = accountDao.getTotalBalance()

    fun getTotalExpenseForRange(
        startDate: Long,
        endDate: Long
    ): Flow<Double> = transactionDao.getTotalForRangeByTransactionType(
        startDate = startDate,
        endDate = endDate,
        transactionType = TransactionType.EXPENSE
    )

    fun getTotalIncome(
        startDate: Long,
        endDate: Long
    ): Flow<Double> = transactionDao.getTotalForRangeByTransactionType(
        startDate = startDate,
        endDate = endDate,
        transactionType = TransactionType.INCOME
    )

    fun getAllTags(): Flow<List<TagEntity>> =
        tagDao.getAllTags()

    fun getCategoryExpenseSummary(startDate: Long, endDate: Long): Flow<List<CategorySummary>> =
        categoryDao.getCategorySummaryBetweenDatesByType(
            transactionType = TransactionType.EXPENSE,
            startDate = startDate,
            endDate = endDate
        )

    fun getCategoryIncomeSummary(startDate: Long, endDate: Long): Flow<List<CategorySummary>> =
        categoryDao.getCategorySummaryBetweenDatesByType(
            transactionType = TransactionType.INCOME,
            startDate = startDate,
            endDate = endDate
        )

    fun getTransactionCategoryExpenseSummary(
        startDate: Long,
        endDate: Long
    ): Flow<List<TransactionCategorySummary>> =
        transactionDao.getTransactionCategorySummaryBetweenDatesByType(
            transactionType = TransactionType.EXPENSE,
            startDate = startDate,
            endDate = endDate
        )

    fun getTransactionCategoryIncomeSummary(
        startDate: Long,
        endDate: Long
    ): Flow<List<TransactionCategorySummary>> =
        transactionDao.getTransactionCategorySummaryBetweenDatesByType(
            transactionType = TransactionType.INCOME,
            startDate = startDate,
            endDate = endDate
        )

    fun getStats(startDate: Long, endDate: Long): Flow<TransactionStats> =
        transactionDao.getTransactionStats(startDate = startDate, endDate = endDate)

    fun getDailyTransactionSummary(startDate: Long, endDate: Long): Flow<List<DailyTransactionSummary>> =
        transactionDao.getDailyTransactionSummary(startDate = startDate, endDate = endDate)
}
