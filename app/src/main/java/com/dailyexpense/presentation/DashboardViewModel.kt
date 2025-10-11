package com.dailyexpense.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dailyexpense.data.enums.TransactionType
import com.dailyexpense.data.repository.DatabaseRepository
import com.dailyexpense.data.room.entity.TransactionEntity
import com.dailyexpense.utils.Constants.RECENT_TRANSACTION_LIMIT
import com.dailyexpense.utils.DateUtil.getLast30DaysRange
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val repository: DatabaseRepository,
) : ViewModel() {

    private val dateRange = getLast30DaysRange()
    private val startDate = dateRange.first
    private val endDate = dateRange.second

    val recentTransactions = repository.getRecentTransaction(limit = RECENT_TRANSACTION_LIMIT)
        .stateIn(viewModelScope, started = SharingStarted.Lazily, initialValue = emptyList())

    val categories = repository.getAllCategories()
        .stateIn(viewModelScope, started = SharingStarted.Lazily, initialValue = emptyList())

    val totalBalance = repository.getTotalBalance()
        .stateIn(viewModelScope, started = SharingStarted.Lazily, initialValue = 0.0)

    val totalExpense =
        repository.getTotalExpenseForRange(startDate, endDate)
            .stateIn(viewModelScope, started = SharingStarted.Lazily, initialValue = 0.0)

    val totalIncome =
        repository.getTotalIncome(startDate, endDate)
            .stateIn(viewModelScope, started = SharingStarted.Lazily, initialValue = 0.0)

    fun addTransactions(transactionEntity: TransactionEntity) {
        viewModelScope.launch {
            repository.insertTransaction(transactionEntity)
        }
    }

    fun deleteTransactions(transactionEntity: TransactionEntity) {
        viewModelScope.launch {
            repository.deleteTransaction(transactionEntity)
        }
    }

    fun getCategoriesByType(type: TransactionType) = repository.getCategoriesByType(type)
}
