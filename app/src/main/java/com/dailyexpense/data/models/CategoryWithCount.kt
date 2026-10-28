package com.dailyexpense.data.models

import androidx.room.Embedded
import com.dailyexpense.data.room.entity.CategoryEntity

data class CategoryWithCount(
    @Embedded val category: CategoryEntity,
    val transactionCount: Int
)
