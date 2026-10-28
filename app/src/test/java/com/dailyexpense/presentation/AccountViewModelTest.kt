package com.dailyexpense.presentation

import com.dailyexpense.data.models.AccountWithBalance
import com.dailyexpense.data.repository.AccountRepository
import com.dailyexpense.data.room.entity.AccountEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AccountViewModelTest {

    private class FakeAccountRepository(
        private val stored: AccountWithBalance? = null
    ) : AccountRepository {
        val saved = mutableListOf<Pair<AccountEntity, Double>>()

        override fun getAllAccounts(): Flow<List<AccountWithBalance>> = flowOf(emptyList())
        override fun getTotalBalance(): Flow<Double> = flowOf(0.0)
        override suspend fun getAccountById(accountId: Int): AccountWithBalance? =
            stored?.takeIf { it.account.accountId == accountId }

        override suspend fun saveAccount(account: AccountEntity, currentBalance: Double) {
            saved += account to currentBalance
        }

        override suspend fun deleteAccount(account: AccountEntity) = Unit
    }

    @Before
    fun setUp() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun AccountViewModel.fillForm(balance: String) {
        updateAccountHolderName("Holder")
        updateAccountBankName("Bank")
        updateAccountNumber("123")
        updateBalance(balance)
    }

    @Test
    fun `new account is saved with id 0 so Room generates a unique id`() {
        val repository = FakeAccountRepository()
        val viewModel = AccountViewModel(repository)

        viewModel.fillForm(balance = "500")
        viewModel.saveAccount {}

        val (account, currentBalance) = repository.saved.single()
        assertEquals(0, account.accountId)
        assertEquals(500.0, currentBalance, 0.0)
        assertEquals(AccountViewModel.SaveResult.Success, viewModel.saveResult.value)
    }

    @Test
    fun `new account with negative balance is rejected`() {
        val repository = FakeAccountRepository()
        val viewModel = AccountViewModel(repository)

        viewModel.fillForm(balance = "-1")
        viewModel.saveAccount {}

        assertTrue(repository.saved.isEmpty())
        assertTrue(viewModel.saveResult.value is AccountViewModel.SaveResult.Error)
    }

    @Test
    fun `loading an overdrawn account shows its derived balance and can be saved`() {
        val stored = AccountWithBalance(
            account = AccountEntity(
                accountId = 4,
                accountHolderName = "Holder",
                accountBankName = "Bank",
                accountNumber = "123",
                openingBalance = 100.0
            ),
            currentBalance = -250.5
        )
        val repository = FakeAccountRepository(stored)
        val viewModel = AccountViewModel(repository)

        viewModel.loadAccount(4)
        assertEquals("-250.50", viewModel.balance.value)

        viewModel.saveAccount {}

        val (account, currentBalance) = repository.saved.single()
        assertEquals(4, account.accountId)
        assertEquals(-250.5, currentBalance, 0.0)
    }
}
