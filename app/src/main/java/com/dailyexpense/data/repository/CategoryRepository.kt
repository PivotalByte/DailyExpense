package com.dailyexpense.data.repository

import com.dailyexpense.data.enums.TransactionType
import com.dailyexpense.data.models.CategorySummary
import com.dailyexpense.data.models.CategoryWithCount
import com.dailyexpense.data.room.dao.CategoryDao
import com.dailyexpense.data.room.entity.CategoryEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

interface CategoryRepository {
    fun getCategoriesByType(type: TransactionType): Flow<List<CategoryEntity>>

    fun getAllCategories(): Flow<List<CategoryEntity>>

    fun getCategoryExpenseSummary(startDate: Long, endDate: Long): Flow<List<CategorySummary>>

    fun getCategoryIncomeSummary(startDate: Long, endDate: Long): Flow<List<CategorySummary>>

    fun getCategoriesWithCount(type: TransactionType): Flow<List<CategoryWithCount>>

    fun getCategoryCount(): Flow<Int>

    suspend fun findCategoryByName(name: String, type: TransactionType): CategoryEntity?

    /** Inserts when [CategoryEntity.categoryId] is 0, otherwise updates in place. */
    suspend fun saveCategory(category: CategoryEntity)

    suspend fun deleteCategory(category: CategoryEntity)
}

@Singleton
class RoomCategoryRepository @Inject constructor(
    private val categoryDao: CategoryDao
) : CategoryRepository {

    override fun getCategoriesByType(type: TransactionType): Flow<List<CategoryEntity>> =
        categoryDao.getCategoriesByType(type)

    override fun getAllCategories(): Flow<List<CategoryEntity>> = categoryDao.getAllCategory()

    override fun getCategoryExpenseSummary(startDate: Long, endDate: Long): Flow<List<CategorySummary>> =
        categoryDao.getCategorySummaryBetweenDatesByType(
            transactionType = TransactionType.EXPENSE,
            startDate = startDate,
            endDate = endDate
        )

    override fun getCategoryIncomeSummary(startDate: Long, endDate: Long): Flow<List<CategorySummary>> =
        categoryDao.getCategorySummaryBetweenDatesByType(
            transactionType = TransactionType.INCOME,
            startDate = startDate,
            endDate = endDate
        )

    override fun getCategoriesWithCount(type: TransactionType): Flow<List<CategoryWithCount>> =
        categoryDao.getCategoriesWithCountByType(type)

    override fun getCategoryCount(): Flow<Int> = categoryDao.getCategoryCount()

    override suspend fun findCategoryByName(name: String, type: TransactionType): CategoryEntity? =
        categoryDao.getCategoryByName(name, type)

    override suspend fun saveCategory(category: CategoryEntity) {
        if (category.categoryId == 0) {
            categoryDao.insertCategory(category)
        } else {
            categoryDao.updateCategory(category)
        }
    }

    override suspend fun deleteCategory(category: CategoryEntity) = categoryDao.deleteCategory(category)
}
