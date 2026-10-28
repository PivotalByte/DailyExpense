package com.dailyexpense.data.room.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.dailyexpense.data.models.TagWithCount
import com.dailyexpense.data.room.entity.TagEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TagDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(tag: TagEntity): Long

    @Query("SELECT * FROM DE_tags")
    fun getAllTags(): Flow<List<TagEntity>>

    @Query(
        """SELECT g.*, COUNT(r.transactionId) AS transactionCount
        FROM DE_tags g
        LEFT JOIN DE_transaction_tag_cross_ref r ON r.tagId = g.tagId
        GROUP BY g.tagId
        ORDER BY g.name COLLATE NOCASE"""
    )
    fun getTagsWithCount(): Flow<List<TagWithCount>>

    // Renames use @Update: a REPLACE insert would delete the tag first, cascading away its links
    @Update
    suspend fun updateTag(tag: TagEntity)

    @Delete
    suspend fun deleteTag(tag: TagEntity)

    @Query("DELETE FROM DE_tags")
    suspend fun deleteAllTags()

    @Query("SELECT * FROM DE_tags WHERE LOWER(name) LIKE '%' || LOWER(:query) || '%'")
    fun searchTags(query: String): Flow<List<TagEntity>>

    @Query("SELECT * FROM DE_tags WHERE LOWER(name) = LOWER(:name)")
    suspend fun getTagByName(name: String): TagEntity?

    @Query("DELETE FROM DE_transaction_tag_cross_ref WHERE transactionId = :transactionId")
    suspend fun deleteTransactionTagCrossRefs(transactionId: Int)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertTransactionTagCrossRef(crossRef: com.dailyexpense.data.room.junctions.TransactionTagCrossRef)
}
