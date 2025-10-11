package com.dailyexpense.data.models

import com.dailyexpense.data.enums.TransactionCategory
import com.dailyexpense.data.enums.TransactionType
import com.dailyexpense.utils.DateUtil.getTodayRange
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

data class FilterState(
    var selectedDuration: Duration = Duration.Today,
    var selectedCategories: Set<Int> = emptySet(),
    var selectedTransactionTypes: Set<TransactionType> = emptySet(),
    var selectedTransactionCategories: Set<TransactionCategory> = emptySet(),
    var selectedTags: Set<Int> = emptySet(),
    var startDate: Long? = getTodayRange().first,
    var endDate: Long? = getTodayRange().second
)

@OptIn(ExperimentalTime::class)
fun FilterState.isSameDay(): Boolean {
    val start = startDate?.let {
        Instant.fromEpochMilliseconds(it)
            .toLocalDateTime(TimeZone.currentSystemDefault())
            .date
    }
    val end = endDate?.let {
        Instant.fromEpochMilliseconds(it)
            .toLocalDateTime(TimeZone.currentSystemDefault())
            .date
    }

    return start != null && end != null && start == end
}
