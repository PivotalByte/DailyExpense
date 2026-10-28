package com.dailyexpense.presentation

import androidx.lifecycle.SavedStateHandle
import com.dailyexpense.fakes.FakeTransactionRepository
import com.dailyexpense.fakes.groceryTransaction
import com.dailyexpense.navigation.ARG_TRANSACTION_ID
import com.dailyexpense.presentation.TransactionDetailViewModel.UiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class TransactionDetailViewModelTest {

    private val dispatcher = UnconfinedTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun TestScope.viewModel(repository: FakeTransactionRepository, id: Int) =
        TransactionDetailViewModel(repository, SavedStateHandle(mapOf(ARG_TRANSACTION_ID to id))).also {
            // uiState only runs while observed, as it is on screen
            backgroundScope.launch { it.uiState.collect {} }
        }

    @Test
    fun `existing transaction is loaded with its relations`() = runTest(dispatcher) {
        val vm = viewModel(FakeTransactionRepository(listOf(groceryTransaction)), id = 42)

        assertEquals(UiState.Loaded(groceryTransaction), vm.uiState.value)
    }

    @Test
    fun `unknown id reports not found`() = runTest(dispatcher) {
        val vm = viewModel(FakeTransactionRepository(listOf(groceryTransaction)), id = 7)

        assertEquals(UiState.NotFound, vm.uiState.value)
    }

    @Test
    fun `delete removes the transaction and reports deleted rather than not found`() = runTest(dispatcher) {
        val repository = FakeTransactionRepository(listOf(groceryTransaction))
        val vm = viewModel(repository, id = 42)

        vm.deleteTransaction()

        assertEquals(listOf(groceryTransaction.transaction), repository.deleted)
        assertEquals(UiState.Deleted, vm.uiState.value)
    }
}
