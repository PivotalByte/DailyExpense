package com.dailyexpense.di

import android.content.Context
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.dailyexpense.data.room.dao.AccountDao
import com.dailyexpense.data.room.dao.CategoryDao
import com.dailyexpense.data.room.dao.TagDao
import com.dailyexpense.data.room.dao.TransactionDao
import com.dailyexpense.data.room.db.AppDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    private val MIGRATION_1_2 = object : Migration(1, 2) {
        override fun migrate(db: SupportSQLiteDatabase) {
            // Update any transactions with legacy categories (SALARY, BUSINESS, INVESTMENTS)
            // to use CASH as the default category
            db.execSQL(
                """
                UPDATE DE_transaction
                SET transactionCategory = 'CASH'
                WHERE transactionCategory IN ('SALARY', 'BUSINESS', 'INVESTMENTS')
                """.trimIndent()
            )
        }
    }

    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context,
    ): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "daily_expense_db"
        )
            .createFromAsset("database/daily_expense_v1.db")
            .addMigrations(MIGRATION_1_2)
            .build()
    }

    @Provides
    @Singleton
    fun provideTransactionDao(db: AppDatabase): TransactionDao = db.transactionDao()

    @Provides
    @Singleton
    fun provideCategoryDao(db: AppDatabase): CategoryDao = db.categoryDao()

    @Provides
    @Singleton
    fun provideAccountDao(db: AppDatabase): AccountDao = db.accountDao()

    @Provides
    @Singleton
    fun provideTagDao(db: AppDatabase): TagDao = db.tagDao()

    @Provides
    @Singleton
    fun provideContext(@ApplicationContext context: Context): Context = context
}
