package com.dailyexpense.data.repository

import com.dailyexpense.data.models.AccountWithBalance
import com.dailyexpense.data.room.dao.AccountDao
import com.dailyexpense.data.room.entity.AccountEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

interface AccountRepository {
    fun getAllAccounts(): Flow<List<AccountWithBalance>>

    fun getTotalBalance(): Flow<Double>

    suspend fun getAccountById(accountId: Int): AccountWithBalance?

    /**
     * Saves [account] so that its derived current balance equals [currentBalance].
     * [AccountEntity.openingBalance] on [account] is ignored and recomputed from the
     * account's existing transactions.
     */
    suspend fun saveAccount(account: AccountEntity, currentBalance: Double)

    suspend fun deleteAccount(account: AccountEntity)
}

@Singleton
class RoomAccountRepository @Inject constructor(
    private val accountDao: AccountDao
) : AccountRepository {

    override fun getAllAccounts(): Flow<List<AccountWithBalance>> = accountDao.getAllAccountsWithBalance()

    override fun getTotalBalance(): Flow<Double> = accountDao.getTotalBalance()

    override suspend fun getAccountById(accountId: Int): AccountWithBalance? =
        accountDao.getAccountWithBalanceById(accountId)

    override suspend fun saveAccount(account: AccountEntity, currentBalance: Double) {
        val net = if (account.accountId > 0) accountDao.getNetTransactionAmount(account.accountId) else 0.0
        accountDao.insertAccount(account.copy(openingBalance = currentBalance - net))
    }

    override suspend fun deleteAccount(account: AccountEntity) = accountDao.deleteAccount(account)
}
