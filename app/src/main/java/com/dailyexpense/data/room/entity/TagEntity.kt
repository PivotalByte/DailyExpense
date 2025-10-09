package com.dailyexpense.data.room.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "DE_tags")
data class TagEntity(
    @PrimaryKey(autoGenerate = true)
    val tagId: Int = 0,
    val name: String
)