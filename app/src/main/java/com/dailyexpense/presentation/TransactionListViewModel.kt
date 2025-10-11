package com.dailyexpense.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.dailyexpense.data.models.FilterState
import com.dailyexpense.data.models.SortState
import com.dailyexpense.data.repository.DatabaseRepository
import com.dailyexpense.data.room.entity.TransactionEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
@HiltViewModel
class TransactionListViewModel @Inject constructor(
    private val repository: DatabaseRepository
) : ViewModel() {

    private val searchQuery = MutableStateFlow(value = "")

    fun getTransactionList(
        filterStateFlow: StateFlow<FilterState>,
        sortStateFlow: StateFlow<SortState>
    ): Flow<PagingData<TransactionEntity>> {
        return combine(
            flow = searchQuery.debounce(timeoutMillis = 300),
            flow2 = filterStateFlow,
            flow3 = sortStateFlow
        ) { query, filter, sort ->
            Triple(first = query, second = filter, third = sort)
        }.flatMapLatest { (query, filter, sort) ->
            repository.getPagedTransactions(
                filterState = filter,
                sortState = sort,
                query = query
            ).flow
        }.cachedIn(viewModelScope)
    }

    fun updateSearchQuery(newQuery: String) {
        searchQuery.value = newQuery
    }
}
