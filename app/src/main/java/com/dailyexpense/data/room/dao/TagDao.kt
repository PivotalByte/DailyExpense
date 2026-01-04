package com.dailyexpense.data.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.dailyexpense.data.room.entity.TagEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TagDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(tag: TagEntity): Long

    @Query("SELECT * FROM DE_tags")
    fun getAllTags(): Flow<List<TagEntity>>

    @Query("SELECT * FROM DE_tags WHERE LOWER(name) LIKE '%' || LOWER(:query) || '%'")
    fun searchTags(query: String): Flow<List<TagEntity>>

    @Query("SELECT * FROM DE_tags WHERE LOWER(name) = LOWER(:name)")
    suspend fun getTagByName(name: String): TagEntity?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertTransactionTagCrossRef(crossRef: com.dailyexpense.data.room.junctions.TransactionTagCrossRef)
}
