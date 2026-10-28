package com.dailyexpense.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dailyexpense.data.models.Duration
import com.dailyexpense.data.models.FilterState
import com.dailyexpense.data.repository.CategoryRepository
import com.dailyexpense.data.repository.TagRepository
import com.dailyexpense.utils.DateUtil.getThisMonthRange
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class FilterViewModel @Inject constructor(
    categoryRepository: CategoryRepository,
    tagRepository: TagRepository
) : ViewModel() {

    private val _filterState = MutableStateFlow(value = FilterState())
    val filterState: StateFlow<FilterState> = _filterState.asStateFlow()

    fun updateFilter(newState: FilterState) {
        _filterState.value = newState
    }

    /** Replaces the filter with this month's transactions in a single category. */
    fun showCategory(categoryId: Int) {
        val (start, end) = getThisMonthRange()
        _filterState.value = FilterState(
            selectedDuration = Duration.ThisMonth,
            selectedCategories = setOf(categoryId),
            startDate = start,
            endDate = end
        )
    }

    val categories = categoryRepository.getAllCategories()
        .stateIn(
            viewModelScope,
            started = SharingStarted.Lazily,
            initialValue = emptyList()
        )

    val tags = tagRepository.getAllTags()
        .stateIn(
            viewModelScope,
            started = SharingStarted.Lazily,
            initialValue = emptyList()
        )
}
