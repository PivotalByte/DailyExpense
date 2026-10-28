package com.dailyexpense.data.room.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.dailyexpense.data.enums.TransactionType
import com.dailyexpense.data.models.CategorySummary
import com.dailyexpense.data.models.CategoryWithCount
import com.dailyexpense.data.room.entity.CategoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategory(categoryEntity: CategoryEntity)

    // Edits must not use the REPLACE insert above: it deletes the row first, cascading to its transactions
    @Update
    suspend fun updateCategory(categoryEntity: CategoryEntity)

    @Delete
    suspend fun deleteCategory(categoryEntity: CategoryEntity)

    @Query(
        """SELECT c.*, COUNT(t.transactionId) AS transactionCount
        FROM DE_category c
        LEFT JOIN DE_transaction t ON t.categoryId = c.categoryId
        WHERE c.transactionType = :type
        GROUP BY c.categoryId
        ORDER BY c.name COLLATE NOCASE"""
    )
    fun getCategoriesWithCountByType(type: TransactionType): Flow<List<CategoryWithCount>>

    @Query("SELECT * FROM DE_category WHERE LOWER(name) = LOWER(:name) AND transactionType = :type LIMIT 1")
    suspend fun getCategoryByName(name: String, type: TransactionType): CategoryEntity?

    @Query("SELECT COUNT(*) FROM DE_category")
    fun getCategoryCount(): Flow<Int>

    @Query("SELECT * FROM DE_category WHERE transactionType = :type")
    fun getCategoriesByType(type: TransactionType): Flow<List<CategoryEntity>>

    @Query("SELECT * FROM DE_category")
    fun getAllCategory(): Flow<List<CategoryEntity>>

    @Query(
        """SELECT c.name AS categoryName, c.colorCode AS colorCode, ABS(SUM(t.amount)) AS totalAmount
        FROM DE_transaction t
        INNER JOIN DE_category c ON t.categoryId = c.categoryId
        WHERE t.date BETWEEN :startDate AND :endDate AND c.transactionType = :transactionType
        GROUP BY c.categoryId
        HAVING totalAmount > 0"""
    )
    fun getCategorySummaryBetweenDatesByType(
        transactionType: TransactionType,
        startDate: Long,
        endDate: Long,
    ): Flow<List<CategorySummary>>
}
