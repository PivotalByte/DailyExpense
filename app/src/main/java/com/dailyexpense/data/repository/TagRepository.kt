package com.dailyexpense.data.repository

import com.dailyexpense.data.models.TagWithCount
import com.dailyexpense.data.room.dao.TagDao
import com.dailyexpense.data.room.entity.TagEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

interface TagRepository {
    fun getAllTags(): Flow<List<TagEntity>>

    fun searchTags(query: String): Flow<List<TagEntity>>

    suspend fun getOrCreateTag(tagName: String): TagEntity

    fun getTagsWithCount(): Flow<List<TagWithCount>>

    suspend fun findTagByName(name: String): TagEntity?

    /** Inserts when [TagEntity.tagId] is 0, otherwise renames in place. */
    suspend fun saveTag(tag: TagEntity)

    suspend fun deleteTag(tag: TagEntity)
}

@Singleton
class RoomTagRepository @Inject constructor(
    private val tagDao: TagDao
) : TagRepository {

    override fun getAllTags(): Flow<List<TagEntity>> = tagDao.getAllTags()

    override fun searchTags(query: String): Flow<List<TagEntity>> = tagDao.searchTags(query)

    override suspend fun getOrCreateTag(tagName: String): TagEntity {
        val existingTag = tagDao.getTagByName(tagName)
        return if (existingTag != null) {
            existingTag
        } else {
            val newTag = TagEntity(name = tagName)
            val tagId = tagDao.insert(newTag)
            newTag.copy(tagId = tagId.toInt())
        }
    }

    override fun getTagsWithCount(): Flow<List<TagWithCount>> = tagDao.getTagsWithCount()

    override suspend fun findTagByName(name: String): TagEntity? = tagDao.getTagByName(name)

    override suspend fun saveTag(tag: TagEntity) {
        if (tag.tagId == 0) {
            tagDao.insert(tag)
        } else {
            tagDao.updateTag(tag)
        }
    }

    override suspend fun deleteTag(tag: TagEntity) = tagDao.deleteTag(tag)
}
