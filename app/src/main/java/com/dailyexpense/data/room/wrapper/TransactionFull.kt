package com.dailyexpense.data.room.wrapper

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import com.dailyexpense.data.room.entity.AccountEntity
import com.dailyexpense.data.room.entity.CategoryEntity
import com.dailyexpense.data.room.entity.TagEntity
import com.dailyexpense.data.room.entity.TransactionEntity
import com.dailyexpense.data.room.junctions.TransactionTagCrossRef

data class TransactionFull(
    @Embedded val transaction: TransactionEntity,

    @Relation(
        parentColumn = "categoryId",
        entityColumn = "categoryId"
    )
    val category: CategoryEntity?,

    @Relation(
        parentColumn = "accountId",
        entityColumn = "accountId"
    )
    val account: AccountEntity?,

    @Relation(
        parentColumn = "transactionId",
        entityColumn = "tagId",
        associateBy = Junction(
            value = TransactionTagCrossRef::class,
            parentColumn = "transactionId",
            entityColumn = "tagId"
        )
    )
    val tags: List<TagEntity>
)
