package com.dailyexpense.data.room.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.dailyexpense.data.converters.TransactionCategoryConverter
import com.dailyexpense.data.converters.TransactionDateConverter
import com.dailyexpense.data.converters.TransactionTypeConverter
import com.dailyexpense.data.room.dao.AccountDao
import com.dailyexpense.data.room.dao.CategoryDao
import com.dailyexpense.data.room.dao.TagDao
import com.dailyexpense.data.room.dao.TransactionDao
import com.dailyexpense.data.room.entity.AccountEntity
import com.dailyexpense.data.room.entity.CategoryEntity
import com.dailyexpense.data.room.entity.TagEntity
import com.dailyexpense.data.room.entity.TransactionEntity
import com.dailyexpense.data.room.junctions.TransactionTagCrossRef

@Database(
    entities = [
        TransactionEntity::class,
        CategoryEntity::class,
        AccountEntity::class,
        TagEntity::class,
        TransactionTagCrossRef::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(
    TransactionTypeConverter::class,
    TransactionCategoryConverter::class,
    TransactionDateConverter::class
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun transactionDao(): TransactionDao
    abstract fun categoryDao(): CategoryDao
    abstract fun accountDao(): AccountDao
    abstract fun tagDao(): TagDao
}
