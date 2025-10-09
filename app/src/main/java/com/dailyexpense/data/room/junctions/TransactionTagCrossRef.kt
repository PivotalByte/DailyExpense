package com.dailyexpense.data.room.junctions

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import com.dailyexpense.data.room.entity.TagEntity
import com.dailyexpense.data.room.entity.TransactionEntity

@Entity(
    tableName = "DE_transaction_tag_cross_ref",
    primaryKeys = ["transactionId", "tagId"],
    foreignKeys = [
        ForeignKey(
            entity = TransactionEntity::class,
            parentColumns = ["transactionId"],
            childColumns = ["transactionId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = TagEntity::class,
            parentColumns = ["tagId"],
            childColumns = ["tagId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("transactionId"), Index("tagId")]
)
data class TransactionTagCrossRef(
    val transactionId: Int,
    val tagId: Int
)