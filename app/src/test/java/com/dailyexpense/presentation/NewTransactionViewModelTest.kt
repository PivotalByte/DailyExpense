package com.dailyexpense.presentation

import androidx.lifecycle.SavedStateHandle
import com.dailyexpense.data.enums.TransactionCategory
import com.dailyexpense.data.enums.TransactionType
import com.dailyexpense.fakes.FakeAccountRepository
import com.dailyexpense.fakes.FakeCategoryRepository
import com.dailyexpense.fakes.FakeTagRepository
import com.dailyexpense.fakes.FakeTransactionRepository
import com.dailyexpense.fakes.axisAccountWithBalance
import com.dailyexpense.fakes.familyTag
import com.dailyexpense.fakes.groceries
import com.dailyexpense.fakes.groceryTransaction
import com.dailyexpense.navigation.ARG_TRANSACTION_ID
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class NewTransactionViewModelTest {

    private val transactions = FakeTransactionRepository(listOf(groceryTransaction))

    @Before
    fun setUp() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun viewModel(args: Map<String, Any> = emptyMap()) = NewTransactionViewModel(
        transactionRepository = transactions,
        categoryRepository = FakeCategoryRepository(listOf(groceries)),
        tagRepository = FakeTagRepository(listOf(familyTag)),
        accountRepository = FakeAccountRepository(listOf(axisAccountWithBalance)),
        savedStateHandle = SavedStateHandle(args)
    )

    @Test
    fun `edit mode prefills every field from the stored transaction`() {
        val vm = viewModel(mapOf(ARG_TRANSACTION_ID to 42))

        assertTrue(vm.isEditMode)
        assertEquals(TransactionType.EXPENSE, vm.transactionTypeState.value)
        assertEquals("12.5", vm.amountState.value)
        assertEquals("Weekly groceries", vm.notesState.value)
        assertEquals(groceries, vm.selectedCategoryState.value)
        assertEquals(axisAccountWithBalance, vm.selectedAccountState.value)
        assertEquals(TransactionCategory.UPI, vm.selectedPaymentMethodState.value)
        assertEquals(18, vm.hourState.value)
        assertEquals(45, vm.minuteState.value)
        assertEquals(listOf(familyTag), vm.selectedTagsState.value)
    }

    @Test
    fun `saving in edit mode updates the same transaction and its tags`() {
        val vm = viewModel(mapOf(ARG_TRANSACTION_ID to 42))

        vm.updateAmount("20")
        vm.removeTag(familyTag)
        vm.saveTransaction {}

        val (saved, tagIds) = transactions.updated.single()
        assertEquals(42, saved.transactionId)
        assertEquals(20.0, saved.amount, 0.0)
        assertEquals(groceryTransaction.transaction.date, saved.date)
        assertEquals(emptyList<Int>(), tagIds)
        assertTrue(transactions.inserted.isEmpty())
    }

    @Test
    fun `saving without an id inserts a new transaction`() {
        val vm = viewModel()

        assertFalse(vm.isEditMode)
        vm.updateAmount("99")
        vm.updateNotes("Lunch")
        vm.updateSelectedCategory(groceries)
        vm.updateSelectedAccount(axisAccountWithBalance)
        vm.updatePaymentMethod(TransactionCategory.CASH)
        vm.saveTransaction {}

        val (saved, _) = transactions.inserted.single()
        assertEquals(0, saved.transactionId)
        assertEquals(axisAccountWithBalance.account.accountId, saved.accountId)
        assertTrue(transactions.updated.isEmpty())
    }
}
