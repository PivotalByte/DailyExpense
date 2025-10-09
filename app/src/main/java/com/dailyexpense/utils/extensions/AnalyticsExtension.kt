package com.dailyexpense.utils.extensions

import com.dailyexpense.data.models.Duration
import com.dailyexpense.utils.DateUtil

fun Duration.getDateRange(): Pair<Long, Long> {
    return when (this) {
        Duration.Today -> DateUtil.getTodayRange()
        Duration.ThisWeek -> DateUtil.getThisWeekRange()
        Duration.ThisMonth -> DateUtil.getThisMonthRange()
        Duration.ThisYear -> DateUtil.getThisYearRange()
        Duration.Custom -> throw IllegalStateException("Custom range must be set manually")
    }
}