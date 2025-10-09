package com.dailyexpense.presentation

import androidx.lifecycle.ViewModel
import com.dailyexpense.data.models.SortState
import com.dailyexpense.data.repository.DatabaseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class SortViewModel @Inject constructor(
    repository: DatabaseRepository
) : ViewModel() {
    private val _sortState = MutableStateFlow(SortState())
    val sortState: StateFlow<SortState> = _sortState.asStateFlow()

    fun updateSort(newState: SortState) {
        _sortState.value = newState
    }

}