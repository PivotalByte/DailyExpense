package com.dailyexpense.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dailyexpense.data.models.CategorySummary
import com.dailyexpense.data.models.Duration
import com.dailyexpense.data.models.TransactionCategorySummary
import com.dailyexpense.data.models.TransactionStats
import com.dailyexpense.data.repository.DatabaseRepository
import com.dailyexpense.utils.DateUtil.getThisWeekRange
import com.dailyexpense.utils.extensions.getDateRange
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject


@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class AnalyticsViewModel @Inject constructor(
    private val repository: DatabaseRepository,
) : ViewModel() {


    private val _startDate = MutableStateFlow(getThisWeekRange().first)
    private val _endDate = MutableStateFlow(getThisWeekRange().second)
    private val _duration = MutableStateFlow<Duration>(Duration.ThisWeek)

    val startDate: StateFlow<Long> = _startDate
    val endDate: StateFlow<Long> = _endDate
    val duration: StateFlow<Duration> = _duration

    val totalExpense: StateFlow<Double> = combine(_startDate, _endDate) { start, end ->
        start to end
    }.flatMapLatest { (start, end) ->
        repository.getTotalExpenseForRange(start, end)
    }.stateIn(viewModelScope, SharingStarted.Lazily, 0.0)

    val totalIncome: StateFlow<Double> = combine(_startDate, _endDate) { start, end ->
        start to end
    }.flatMapLatest { (start, end) ->
        repository.getTotalIncome(start, end)
    }.stateIn(viewModelScope, SharingStarted.Lazily, 0.0)

    val categoriesExpenseSummary: StateFlow<List<CategorySummary>> =
        combine(_startDate, _endDate) { start, end ->
            start to end
        }.flatMapLatest { (start, end) ->
            repository.getCategoryExpenseSummary(start, end)
        }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val categoriesIncomeSummary: StateFlow<List<CategorySummary>> =
        combine(_startDate, _endDate) { start, end ->
            start to end
        }.flatMapLatest { (start, end) ->
            repository.getCategoryIncomeSummary(start, end)
        }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val transactionCategoriesExpenseSummary: StateFlow<List<TransactionCategorySummary>> =
        combine(_startDate, _endDate) { start, end ->
            start to end
        }.flatMapLatest { (start, end) ->
            repository.getTransactionCategoryExpenseSummary(start, end)
        }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val transactionCategoriesIncomeSummary: StateFlow<List<TransactionCategorySummary>> =
        combine(_startDate, _endDate) { start, end ->
            start to end
        }.flatMapLatest { (start, end) ->
            repository.getTransactionCategoryIncomeSummary(start, end)
        }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val stats: StateFlow<TransactionStats> =
        combine(_startDate, _endDate) { start, end ->
            start to end
        }.flatMapLatest { (start, end) ->
            repository.getStats(start, end)
        }.stateIn(viewModelScope, SharingStarted.Lazily, TransactionStats())

    fun updateDuration(duration: Duration) {
        _duration.value = duration
        if (duration != Duration.Custom) {
            val (start, end) = duration.getDateRange()
            _startDate.value = start
            _endDate.value = end
            Timber.tag("AnalyticsVM").d("Duration: $duration → ${formatDate(start)} - ${formatDate(end)}")
        } else {
            Timber.tag("AnalyticsVM").d("Duration: Custom → Using selected range")
        }
    }

    fun updateDateRange(start: Long, end: Long) {
        _startDate.value = start
        _endDate.value = end
        Timber.tag("AnalyticsVM").d("Custom range applied → ${formatDate(start)} - ${formatDate(end)}")
    }

    private fun formatDate(epochMillis: Long): String {
        val dateFormat = SimpleDateFormat("dd MMM yyyy, HH:mm:ss", Locale.getDefault())
        return dateFormat.format(Date(epochMillis))
    }
}