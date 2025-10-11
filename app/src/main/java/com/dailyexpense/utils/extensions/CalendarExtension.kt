package com.dailyexpense.utils.extensions

import kotlinx.datetime.YearMonth

fun YearMonth.getDisplayMonthYear(): String {
    val month = this.month.name.lowercase().replaceFirstChar { it.uppercase() }
    return "$month ${this.year}"
}
