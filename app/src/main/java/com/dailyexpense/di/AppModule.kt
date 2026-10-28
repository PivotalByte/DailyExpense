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

    private val MIGRATION_2_3 = object : Migration(2, 3) {
        override fun migrate(db: SupportSQLiteDatabase) {
            // Seed data stored expenses as negative amounts while the app saved them positive.
            // The transaction type now carries the sign, so every amount is stored positive.
            db.execSQL("UPDATE DE_transaction SET amount = ABS(amount)")

            // accountId was not auto-generated, so every account added in-app was saved with id -1
            // and overwrote the previous one. Move any such row (and its transactions) to a real id.
            val invalidIds = db.query("SELECT accountId FROM DE_account WHERE accountId <= 0").use { cursor ->
                buildList { while (cursor.moveToNext()) add(cursor.getInt(0)) }
            }
            invalidIds.forEach { oldId ->
                val newId = db.query("SELECT COALESCE(MAX(accountId), 0) + 1 FROM DE_account").use { cursor ->
                    cursor.moveToFirst()
                    cursor.getInt(0)
                }
                db.execSQL(
                    """
                    INSERT INTO DE_account (accountId, accountHolderName, accountBankName, accountNumber, balance)
                    SELECT ?, accountHolderName, accountBankName, accountNumber, balance
                    FROM DE_account WHERE accountId = ?
                    """.trimIndent(),
                    arrayOf(newId, oldId)
                )
                db.execSQL("UPDATE DE_transaction SET accountId = ? WHERE accountId = ?", arrayOf(newId, oldId))
                db.execSQL("DELETE FROM DE_account WHERE accountId = ?", arrayOf(oldId))
            }

            // The balance column becomes the opening balance; the current balance is now derived
            // from transactions. Rebase it so every account shows the same balance as before.
            db.execSQL(
                """
                UPDATE DE_account SET balance = balance - COALESCE((
                    SELECT SUM(CASE WHEN t.transactionType = 'INCOME' THEN t.amount
                                    WHEN t.transactionType = 'EXPENSE' THEN -t.amount ELSE 0 END)
                    FROM DE_transaction t WHERE t.accountId = DE_account.accountId
                ), 0)
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
            .addMigrations(MIGRATION_1_2, MIGRATION_2_3)
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
