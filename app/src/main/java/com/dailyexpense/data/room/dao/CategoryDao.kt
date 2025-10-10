package com.dailyexpense.data.room.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.dailyexpense.data.enums.TransactionType
import com.dailyexpense.data.models.CategorySummary
import com.dailyexpense.data.room.entity.CategoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategory(categoryEntity: CategoryEntity)

    @Delete
    suspend fun deleteCategory(categoryEntity: CategoryEntity)

    @Query("SELECT * FROM DE_category WHERE transactionType = :type")
    fun getCategoriesByType(type: TransactionType): Flow<List<CategoryEntity>>

    @Query("SELECT * FROM DE_category")
    fun getAllCategory(): Flow<List<CategoryEntity>>

    @Query(
        """SELECT c.name AS categoryName, c.colorCode AS colorCode, SUM(t.amount) AS totalAmount
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
