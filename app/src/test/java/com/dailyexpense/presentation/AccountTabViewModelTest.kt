package com.dailyexpense.presentation

import com.dailyexpense.fakes.FakeAccountRepository
import com.dailyexpense.fakes.FakeCategoryRepository
import com.dailyexpense.fakes.FakeDataRepository
import com.dailyexpense.fakes.FakeTagRepository
import com.dailyexpense.presentation.AccountTabViewModel.Event
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.io.File
import java.io.IOException

@OptIn(ExperimentalCoroutinesApi::class)
class AccountTabViewModelTest {

    @Before
    fun setUp() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun viewModel(data: FakeDataRepository) =
        AccountTabViewModel(FakeAccountRepository(), FakeCategoryRepository(), FakeTagRepository(), data)

    @Test
    fun `export with transactions hands the file to the screen`() {
        val vm = viewModel(FakeDataRepository(exportCount = 3))

        vm.exportTransactions()

        assertEquals(Event.ExportReady(File("export.csv")), vm.event.value)
        assertFalse(vm.isWorking.value)
    }

    @Test
    fun `export with no transactions says so instead of sharing an empty file`() {
        val vm = viewModel(FakeDataRepository(exportCount = 0))

        vm.exportTransactions()

        assertEquals(Event.NothingToExport, vm.event.value)
    }

    @Test
    fun `export failure is reported`() {
        val vm = viewModel(FakeDataRepository(exportError = IOException("disk full")))

        vm.exportTransactions()

        assertEquals(Event.ExportFailed, vm.event.value)
        assertFalse(vm.isWorking.value)
    }

    @Test
    fun `clear all data runs once and reports completion, then the event is consumed`() {
        val data = FakeDataRepository()
        val vm = viewModel(data)

        vm.clearAllData()

        assertEquals(1, data.clearCalls)
        assertTrue(vm.event.value is Event.DataCleared)
        vm.consumeEvent()
        assertNull(vm.event.value)
    }
}
