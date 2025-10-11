package com.dailyexpense.data.room.wrapper

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import com.dailyexpense.data.room.entity.TagEntity
import com.dailyexpense.data.room.entity.TransactionEntity
import com.dailyexpense.data.room.junctions.TransactionTagCrossRef

data class TransactionWithTags(
    @Embedded val transaction: TransactionEntity,
    @Relation(
        parentColumn = "transactionId",
        entityColumn = "tagId",
        associateBy = Junction(TransactionTagCrossRef::class)
    )
    val tags: List<TagEntity>
)
