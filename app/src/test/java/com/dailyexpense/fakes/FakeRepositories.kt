package com.dailyexpense.fakes

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.dailyexpense.data.enums.TransactionType
import com.dailyexpense.data.models.AccountWithBalance
import com.dailyexpense.data.models.CategorySummary
import com.dailyexpense.data.models.CategoryWithCount
import com.dailyexpense.data.models.DailyTransactionSummary
import com.dailyexpense.data.models.FilterState
import com.dailyexpense.data.models.SortState
import com.dailyexpense.data.models.TagWithCount
import com.dailyexpense.data.models.TransactionCategorySummary
import com.dailyexpense.data.models.TransactionStats
import com.dailyexpense.data.repository.AccountRepository
import com.dailyexpense.data.repository.CategoryRepository
import com.dailyexpense.data.repository.DataRepository
import com.dailyexpense.data.repository.ExportResult
import com.dailyexpense.data.repository.TagRepository
import com.dailyexpense.data.repository.TransactionRepository
import com.dailyexpense.data.room.entity.AccountEntity
import com.dailyexpense.data.room.entity.CategoryEntity
import com.dailyexpense.data.room.entity.TagEntity
import com.dailyexpense.data.room.entity.TransactionEntity
import com.dailyexpense.data.room.wrapper.TransactionFull
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import java.io.File

/** In-memory store keyed by transaction id; records which write calls were made. */
class FakeTransactionRepository(initial: List<TransactionFull> = emptyList()) : TransactionRepository {
    private val store = MutableStateFlow(initial.associateBy { it.transaction.transactionId })

    val inserted = mutableListOf<Pair<TransactionEntity, List<Int>>>()
    val updated = mutableListOf<Pair<TransactionEntity, List<Int>>>()
    val deleted = mutableListOf<TransactionEntity>()

    override suspend fun insertTransaction(transactionEntity: TransactionEntity): Long {
        inserted += transactionEntity to emptyList()
        return 1L
    }

    override suspend fun insertTransactionWithTags(transaction: TransactionEntity, tagIds: List<Int>): Long {
        inserted += transaction to tagIds
        return 1L
    }

    override suspend fun updateTransactionWithTags(transaction: TransactionEntity, tagIds: List<Int>) {
        updated += transaction to tagIds
    }

    override suspend fun getTransactionById(id: Int): TransactionFull? = store.value[id]

    override fun observeTransactionById(id: Int): Flow<TransactionFull?> = store.map { it[id] }

    override suspend fun deleteTransaction(transactionEntity: TransactionEntity) {
        deleted += transactionEntity
        store.value = store.value - transactionEntity.transactionId
    }

    override fun getRecentTransaction(limit: Int): Flow<List<TransactionEntity>> = flowOf(emptyList())

    override fun getPagedTransactions(
        filterState: FilterState,
        sortState: SortState,
        query: String
    ): Pager<Int, TransactionEntity> = Pager(PagingConfig(pageSize = 20)) { EmptyPagingSource() }

    override fun getTotalExpenseForRange(startDate: Long, endDate: Long): Flow<Double> = flowOf(0.0)
    override fun getTotalIncome(startDate: Long, endDate: Long): Flow<Double> = flowOf(0.0)
    override fun getTransactionCategoryExpenseSummary(
        startDate: Long,
        endDate: Long
    ): Flow<List<TransactionCategorySummary>> = flowOf(emptyList())

    override fun getTransactionCategoryIncomeSummary(
        startDate: Long,
        endDate: Long
    ): Flow<List<TransactionCategorySummary>> = flowOf(emptyList())

    override fun getStats(startDate: Long, endDate: Long): Flow<TransactionStats> = flowOf(TransactionStats())
    override fun getDailyTransactionSummary(
        startDate: Long,
        endDate: Long
    ): Flow<List<DailyTransactionSummary>> = flowOf(emptyList())

    private class EmptyPagingSource : PagingSource<Int, TransactionEntity>() {
        override fun getRefreshKey(state: PagingState<Int, TransactionEntity>): Int? = null
        override suspend fun load(params: LoadParams<Int>): LoadResult<Int, TransactionEntity> =
            LoadResult.Page(data = emptyList(), prevKey = null, nextKey = null)
    }
}

class FakeAccountRepository(private val accounts: List<AccountWithBalance> = emptyList()) : AccountRepository {
    override fun getAllAccounts(): Flow<List<AccountWithBalance>> = flowOf(accounts)
    override fun getTotalBalance(): Flow<Double> = flowOf(accounts.sumOf { it.currentBalance })
    override suspend fun getAccountById(accountId: Int): AccountWithBalance? =
        accounts.firstOrNull { it.account.accountId == accountId }

    override suspend fun saveAccount(account: AccountEntity, currentBalance: Double) = Unit
    override suspend fun deleteAccount(account: AccountEntity) = Unit
}

class FakeCategoryRepository(
    private val categories: List<CategoryEntity> = emptyList(),
    private val counts: Map<Int, Int> = emptyMap()
) : CategoryRepository {
    val saved = mutableListOf<CategoryEntity>()
    val deleted = mutableListOf<CategoryEntity>()

    override fun getCategoriesByType(type: TransactionType): Flow<List<CategoryEntity>> =
        flowOf(categories.filter { it.transactionType == type })

    override fun getAllCategories(): Flow<List<CategoryEntity>> = flowOf(categories)
    override fun getCategoryExpenseSummary(startDate: Long, endDate: Long): Flow<List<CategorySummary>> =
        flowOf(emptyList())

    override fun getCategoryIncomeSummary(startDate: Long, endDate: Long): Flow<List<CategorySummary>> =
        flowOf(emptyList())

    override fun getCategoriesWithCount(type: TransactionType): Flow<List<CategoryWithCount>> =
        flowOf(
            categories.filter { it.transactionType == type }
                .map { CategoryWithCount(it, counts[it.categoryId] ?: 0) }
        )

    override fun getCategoryCount(): Flow<Int> = flowOf(categories.size)

    override suspend fun findCategoryByName(name: String, type: TransactionType): CategoryEntity? =
        categories.firstOrNull { it.name.equals(name, ignoreCase = true) && it.transactionType == type }

    override suspend fun saveCategory(category: CategoryEntity) {
        saved += category
    }

    override suspend fun deleteCategory(category: CategoryEntity) {
        deleted += category
    }
}

class FakeTagRepository(
    private val tags: List<TagEntity> = emptyList(),
    private val counts: Map<Int, Int> = emptyMap()
) : TagRepository {
    val saved = mutableListOf<TagEntity>()
    val deleted = mutableListOf<TagEntity>()

    override fun getAllTags(): Flow<List<TagEntity>> = flowOf(tags)
    override fun searchTags(query: String): Flow<List<TagEntity>> =
        flowOf(tags.filter { it.name.contains(query, ignoreCase = true) })

    override suspend fun getOrCreateTag(tagName: String): TagEntity =
        tags.firstOrNull { it.name.equals(tagName, ignoreCase = true) }
            ?: TagEntity(tagId = tags.size + 1, name = tagName)

    override fun getTagsWithCount(): Flow<List<TagWithCount>> =
        flowOf(tags.map { TagWithCount(it, counts[it.tagId] ?: 0) })

    override suspend fun findTagByName(name: String): TagEntity? =
        tags.firstOrNull { it.name.equals(name, ignoreCase = true) }

    override suspend fun saveTag(tag: TagEntity) {
        saved += tag
    }

    override suspend fun deleteTag(tag: TagEntity) {
        deleted += tag
    }
}

/** Export returns [exportCount] transactions, or throws [exportError] when set. */
class FakeDataRepository(
    private val exportCount: Int = 0,
    private val exportError: Exception? = null
) : DataRepository {
    var clearCalls = 0

    override suspend fun exportTransactionsCsv(): ExportResult {
        exportError?.let { throw it }
        return ExportResult(file = File("export.csv"), transactionCount = exportCount)
    }

    override suspend fun clearAllData() {
        clearCalls++
    }
}
