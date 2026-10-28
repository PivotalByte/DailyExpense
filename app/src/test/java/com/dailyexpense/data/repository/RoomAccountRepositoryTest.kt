package com.dailyexpense.data.repository

import com.dailyexpense.data.models.AccountWithBalance
import com.dailyexpense.data.room.dao.AccountDao
import com.dailyexpense.data.room.entity.AccountEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class RoomAccountRepositoryTest {

    private class FakeAccountDao(private val netByAccount: Map<Int, Double>) : AccountDao {
        val inserted = mutableListOf<AccountEntity>()

        override suspend fun insertAccount(accountEntity: AccountEntity) {
            inserted += accountEntity
        }

        override suspend fun deleteAccount(accountEntity: AccountEntity) = Unit
        override suspend fun deleteAllAccounts() = Unit
        override fun getTotalBalance(): Flow<Double> = emptyFlow()
        override fun getAllAccountsWithBalance(): Flow<List<AccountWithBalance>> = emptyFlow()
        override suspend fun getAccountWithBalanceById(accountId: Int): AccountWithBalance? = null
        override suspend fun getNetTransactionAmount(accountId: Int): Double = netByAccount[accountId] ?: 0.0
    }

    private fun account(id: Int) = AccountEntity(
        accountId = id,
        accountHolderName = "Holder",
        accountBankName = "Bank",
        accountNumber = "123",
        openingBalance = 0.0
    )

    @Test
    fun `new account stores the entered balance as its opening balance`() = runTest {
        val dao = FakeAccountDao(netByAccount = emptyMap())

        RoomAccountRepository(dao).saveAccount(account(id = 0), currentBalance = 1000.0)

        assertEquals(1000.0, dao.inserted.single().openingBalance, 0.0)
    }

    @Test
    fun `existing account rebases opening balance so derived balance matches the entered one`() = runTest {
        // Account 7 has spent 300 more than it earned
        val dao = FakeAccountDao(netByAccount = mapOf(7 to -300.0))

        RoomAccountRepository(dao).saveAccount(account(id = 7), currentBalance = 1000.0)

        val saved = dao.inserted.single()
        assertEquals(1300.0, saved.openingBalance, 0.0)
        assertEquals(1000.0, saved.openingBalance + -300.0, 0.0)
    }
}
