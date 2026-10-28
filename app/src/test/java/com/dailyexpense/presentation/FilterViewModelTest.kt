package com.dailyexpense.presentation

import com.dailyexpense.data.models.Duration
import com.dailyexpense.data.models.FilterState
import com.dailyexpense.fakes.FakeCategoryRepository
import com.dailyexpense.fakes.FakeTagRepository
import com.dailyexpense.utils.DateUtil.getThisMonthRange
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class FilterViewModelTest {

    @Before
    fun setUp() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `showCategory replaces any filter with this month in that category`() {
        val vm = FilterViewModel(FakeCategoryRepository(), FakeTagRepository())
        vm.updateFilter(FilterState(selectedTags = setOf(1), selectedCategories = setOf(5)))

        vm.showCategory(categoryId = 2)

        val (start, end) = getThisMonthRange()
        assertEquals(
            FilterState(
                selectedDuration = Duration.ThisMonth,
                selectedCategories = setOf(2),
                startDate = start,
                endDate = end
            ),
            vm.filterState.value
        )
    }
}
