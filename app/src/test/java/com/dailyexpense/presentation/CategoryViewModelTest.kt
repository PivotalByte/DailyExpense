package com.dailyexpense.presentation

import com.dailyexpense.data.enums.TransactionType
import com.dailyexpense.data.models.CategoryWithCount
import com.dailyexpense.fakes.FakeCategoryRepository
import com.dailyexpense.fakes.groceries
import com.dailyexpense.presentation.CategoryViewModel.EditorError
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class CategoryViewModelTest {

    // groceries (id 1) is used by 4 transactions
    private val repository = FakeCategoryRepository(listOf(groceries), counts = mapOf(1 to 4))
    private lateinit var vm: CategoryViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
        vm = CategoryViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `new category is saved with trimmed name and chosen style, then the editor closes`() {
        vm.startNew()
        vm.updateType(TransactionType.INCOME)
        vm.updateName("  Freelance ")
        vm.updateColor("#9C27B0")
        vm.updateIcon("ic_category_business")
        vm.save()

        val saved = repository.saved.single()
        assertEquals(0, saved.categoryId)
        assertEquals("Freelance", saved.name)
        assertEquals(TransactionType.INCOME, saved.transactionType)
        assertEquals("#9C27B0", saved.colorCode)
        assertEquals("ic_category_business", saved.iconResName)
        assertNull(vm.editor.value)
        assertEquals(TransactionType.INCOME, vm.selectedType.value)
    }

    @Test
    fun `blank and duplicate names are rejected`() {
        vm.startNew()
        vm.save()
        assertEquals(EditorError.EmptyName, vm.editor.value?.error)

        vm.updateName("groceries")
        vm.save()
        assertEquals(EditorError.DuplicateName, vm.editor.value?.error)
        assertTrue(repository.saved.isEmpty())
    }

    @Test
    fun `editing keeps the id and type and may keep its own name`() {
        vm.startEdit(CategoryWithCount(groceries, transactionCount = 4))
        vm.updateType(TransactionType.INCOME)
        vm.updateColor("#795548")
        vm.save()

        val saved = repository.saved.single()
        assertEquals(groceries.categoryId, saved.categoryId)
        assertEquals(TransactionType.EXPENSE, saved.transactionType)
        assertEquals("#795548", saved.colorCode)
    }

    @Test
    fun `category in use cannot be deleted`() {
        vm.startEdit(CategoryWithCount(groceries, transactionCount = 4))
        vm.delete()

        assertEquals(EditorError.InUse, vm.editor.value?.error)
        assertTrue(repository.deleted.isEmpty())
    }

    @Test
    fun `unused category is deleted`() {
        vm.startEdit(CategoryWithCount(groceries, transactionCount = 0))
        vm.delete()

        assertEquals(groceries.categoryId, repository.deleted.single().categoryId)
        assertNull(vm.editor.value)
    }
}
