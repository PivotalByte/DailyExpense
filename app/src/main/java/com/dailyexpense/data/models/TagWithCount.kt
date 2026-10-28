package com.dailyexpense.data.models

import androidx.room.Embedded
import com.dailyexpense.data.room.entity.TagEntity

data class TagWithCount(
    @Embedded val tag: TagEntity,
    val transactionCount: Int
)
