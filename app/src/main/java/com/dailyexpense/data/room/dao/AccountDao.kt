package com.dailyexpense.data.room.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.dailyexpense.data.models.AccountWithBalance
import com.dailyexpense.data.room.entity.AccountEntity
import kotlinx.coroutines.flow.Flow

// Net effect of a transaction on its account. Amounts are stored positive; NEUTRAL moves no money.
private const val NET_AMOUNT =
    "CASE WHEN t.transactionType = 'INCOME' THEN t.amount " +
        "WHEN t.transactionType = 'EXPENSE' THEN -t.amount ELSE 0 END"

@Dao
interface AccountDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAccount(accountEntity: AccountEntity)

    @Delete
    suspend fun deleteAccount(accountEntity: AccountEntity)

    @Query("DELETE FROM DE_account")
    suspend fun deleteAllAccounts()

    @Query(
        """SELECT COALESCE((SELECT SUM(balance) FROM DE_account), 0) +
        COALESCE((SELECT SUM($NET_AMOUNT) FROM DE_transaction t), 0)"""
    )
    fun getTotalBalance(): Flow<Double>

    @Query(
        """SELECT a.*, a.balance + COALESCE(SUM($NET_AMOUNT), 0) AS currentBalance,
        COUNT(t.transactionId) AS transactionCount
        FROM DE_account a
        LEFT JOIN DE_transaction t ON t.accountId = a.accountId
        GROUP BY a.accountId"""
    )
    fun getAllAccountsWithBalance(): Flow<List<AccountWithBalance>>

    @Query(
        """SELECT a.*, a.balance + COALESCE(SUM($NET_AMOUNT), 0) AS currentBalance,
        COUNT(t.transactionId) AS transactionCount
        FROM DE_account a
        LEFT JOIN DE_transaction t ON t.accountId = a.accountId
        WHERE a.accountId = :accountId
        GROUP BY a.accountId"""
    )
    suspend fun getAccountWithBalanceById(accountId: Int): AccountWithBalance?

    @Query("SELECT COALESCE(SUM($NET_AMOUNT), 0) FROM DE_transaction t WHERE t.accountId = :accountId")
    suspend fun getNetTransactionAmount(accountId: Int): Double
}
