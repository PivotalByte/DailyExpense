package com.dailyexpense.utils

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.BaselineShift
import androidx.compose.ui.unit.sp
import com.dailyexpense.data.models.Duration
import com.kizitonwose.calendar.core.now
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.Month
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.format
import kotlinx.datetime.format.MonthNames
import kotlinx.datetime.format.Padding
import kotlinx.datetime.format.char
import kotlinx.datetime.isoDayNumber
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime
import kotlinx.datetime.yearMonth
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@OptIn(ExperimentalTime::class)
object DateUtil {

    private val tz: TimeZone = TimeZone.currentSystemDefault()

    // Cached formatters to avoid recreation
    private val dayMonthYearFormatter by lazy {
        LocalDate.Format {
            day(Padding.NONE)
            char(value = ' ')
            monthName(names = MonthNames.ENGLISH_ABBREVIATED)
            char(value = ' ')
            year()
        }
    }

    private val dayMonthFormatter by lazy {
        LocalDate.Format {
            day(Padding.NONE)
            char(value = ' ')
            monthName(names = MonthNames.ENGLISH_ABBREVIATED)
        }
    }

    private val monthFormatter by lazy {
        LocalDate.Format { monthName(names = MonthNames.ENGLISH_FULL) }
    }

    private val yearFormatter by lazy {
        LocalDate.Format { year() }
    }

    fun Date.toOrdinalAnnotatedDate(): AnnotatedString {
        val day = SimpleDateFormat("d", Locale.getDefault()).format(this).toInt()
        val suffix = when {
            day in 11..13 -> "th"
            day % 10 == 1 -> "st"
            day % 10 == 2 -> "nd"
            day % 10 == 3 -> "rd"
            else -> "th"
        }
        val dayText = day.toString()
        val monthYear = SimpleDateFormat("MMM yyyy", Locale.getDefault()).format(this)
        return buildAnnotatedString {
            append(dayText)
            pushStyle(
                SpanStyle(
                    fontSize = 8.sp,
                    baselineShift = BaselineShift.Superscript
                )
            )
            append(suffix)
            pop()
            append(" $monthYear")
        }
    }

    fun getLast30DaysRange(): Pair<Long, Long> {
        val today = Clock.System.now().toLocalDateTime(timeZone = tz).date
        val start = today.minus(period = DatePeriod(days = 30)).startOfDayOrTodayMillis()
        val end = today.endOfDayOrTodayMillis()
        return start to end
    }

    fun getTodayRange(): Pair<Long, Long> {
        val today = Clock.System.now().toLocalDateTime(timeZone = tz).date
        val start = today.startOfDayOrTodayMillis()
        val end = today.endOfDayOrTodayMillis()
        return start to end
    }

    fun getThisWeekRange(): Pair<Long, Long> {
        val today = Clock.System.now().toLocalDateTime(timeZone = tz).date
        val start = today.startOfWeek()
        val end = today.endOfWeek()
        return start to end
    }

    fun getThisMonthRange(): Pair<Long, Long> {
        val today = Clock.System.now().toLocalDateTime(timeZone = tz).date
        val start = today.startOfMonth()
        val end = today.endOfMonth()
        return start to end
    }

    fun getThisYearRange(): Pair<Long, Long> {
        val today = Clock.System.now().toLocalDateTime(timeZone = tz).date
        val start = today.startOfYear()
        val end = today.endOfYear()
        return start to end
    }

    fun LocalDate.toMillis(timeZone: TimeZone = TimeZone.currentSystemDefault()): Long {
        return this.atStartOfDayIn(timeZone).toEpochMilliseconds()
    }

    fun LocalDate?.startOfDayOrTodayMillis(timeZone: TimeZone = TimeZone.currentSystemDefault()): Long {
        val now = Clock.System.now().toLocalDateTime(timeZone).date
        return (this ?: now).atStartOfDayIn(timeZone).toEpochMilliseconds()
    }

    fun Long.toLocalDate(): LocalDate {
        return Instant.fromEpochMilliseconds(this)
            .toLocalDateTime(TimeZone.currentSystemDefault())
            .date
    }

    fun LocalDate?.endOfDayOrTodayMillis(timeZone: TimeZone = TimeZone.currentSystemDefault()): Long {
        val now = Clock.System.now().toLocalDateTime(timeZone).date
        val dateToUse = this ?: now

        return dateToUse.plus(period = DatePeriod(days = 1))
            .atStartOfDayIn(timeZone)
            .toEpochMilliseconds() - 1
    }

    fun calculateDateRange(duration: Duration): Pair<Long?, Long?> {
        return when (duration) {
            Duration.Today -> getTodayRange()
            Duration.ThisWeek -> getThisWeekRange()
            Duration.ThisMonth -> getThisMonthRange()
            Duration.ThisYear -> getThisYearRange()
            Duration.Custom -> null to null
        }
    }

    fun getSelectedDateRangeAsString(
        duration: Duration,
        startDate: Long,
        endDate: Long
    ): String {
        val startLocalDate = startDate.toLocalDate()
        val endLocalDate = endDate.toLocalDate()

        return when (duration) {
            is Duration.Today -> startLocalDate.format(dayMonthYearFormatter)
            is Duration.ThisWeek -> {
                val start = startLocalDate.format(dayMonthFormatter)
                val end = endLocalDate.format(dayMonthFormatter)
                "$start - $end"
            }
            is Duration.ThisMonth -> {
                val month = startLocalDate.format(monthFormatter)
                val year = startLocalDate.format(yearFormatter)
                "$month $year"
            }
            is Duration.ThisYear -> startLocalDate.format(yearFormatter)
            is Duration.Custom -> {
                val start = startLocalDate.format(dayMonthYearFormatter)
                val end = endLocalDate.format(dayMonthYearFormatter)
                "$start - $end"
            }
        }
    }

    fun LocalDate.startOfWeek(): Long {
        val dayOfWeekValue = dayOfWeek.isoDayNumber
        return this.minus(value = dayOfWeekValue - 1, unit = DateTimeUnit.DAY)
            .startOfDayOrTodayMillis()
    }

    fun LocalDate.endOfWeek(): Long {
        val dayOfWeekValue = dayOfWeek.isoDayNumber
        return this.plus(7 - dayOfWeekValue, unit = DateTimeUnit.DAY).endOfDayOrTodayMillis()
    }

    fun LocalDate.startOfMonth(): Long {
        return LocalDate(year, month, day = 1).startOfDayOrTodayMillis()
    }

    fun LocalDate.endOfMonth(): Long {
        // Simplified using yearMonth.lastDay property
        val daysInMonth = when (month) {
            Month.JANUARY, Month.MARCH, Month.MAY, Month.JULY,
            Month.AUGUST, Month.OCTOBER, Month.DECEMBER -> 31
            Month.APRIL, Month.JUNE, Month.SEPTEMBER, Month.NOVEMBER -> 30
            Month.FEBRUARY -> if ((year % 4 == 0 && year % 100 != 0) || (year % 400 == 0)) 29 else 28
        }
        return LocalDate(year, month, daysInMonth).endOfDayOrTodayMillis()
    }

    fun LocalDate.startOfYear(): Long {
        return LocalDate(year, Month.JANUARY, day = 1).startOfDayOrTodayMillis()
    }

    fun LocalDate.endOfYear(): Long {
        return LocalDate(year, Month.DECEMBER, day = 31).endOfDayOrTodayMillis()
    }

    private fun getPreviousWeekRange(startDate: Long): Pair<Long, Long> {
        val start = startDate.toLocalDate().minus(period = DatePeriod(days = 7))
        return start.startOfWeek() to start.endOfWeek()
    }

    private fun getNextWeekRange(startDate: Long): Pair<Long, Long> {
        val start = startDate.toLocalDate().plus(period = DatePeriod(days = 7))
        return start.startOfWeek() to start.endOfWeek()
    }

    private fun getPreviousMonthRange(startDate: Long): Pair<Long, Long> {
        val date = startDate.toLocalDate().minus(period = DatePeriod(months = 1))
        return date.startOfMonth() to date.endOfMonth()
    }

    private fun getNextMonthRange(startDate: Long): Pair<Long, Long> {
        val date = startDate.toLocalDate().plus(period = DatePeriod(months = 1))
        return date.startOfMonth() to date.endOfMonth()
    }

    private fun getPreviousYearRange(startDate: Long): Pair<Long, Long> {
        val date = startDate.toLocalDate().minus(period = DatePeriod(years = 1))
        return date.startOfYear() to date.endOfYear()
    }

    private fun getNextYearRange(startDate: Long): Pair<Long, Long> {
        val date = startDate.toLocalDate().plus(period = DatePeriod(years = 1))
        return date.startOfYear() to date.endOfYear()
    }

    fun isNextEnabledForDuration(
        duration: Duration,
        endDate: Long
    ): Boolean {
        val today = LocalDate.now()
        return when (duration) {
            is Duration.Today -> false // can't go "next" for today

            is Duration.ThisWeek -> {
                val sunday = today.plus(
                    (7 - today.dayOfWeek.isoDayNumber).toLong(),
                    DateTimeUnit.DAY
                )
                endDate.toLocalDate() < sunday
            }

            is Duration.ThisMonth -> {
                val lastDayOfMonth = today.yearMonth.lastDay
                endDate.toLocalDate() < lastDayOfMonth
            }

            is Duration.ThisYear -> {
                val lastDayOfYear = LocalDate(year = today.year, month = 12, day = 31)
                endDate.toLocalDate() < lastDayOfYear
            }

            is Duration.Custom -> false // navigation for custom ranges is usually disabled
        }
    }

    fun getPreviousRange(
        duration: Duration,
        startDate: Long,
        endDate: Long
    ): Pair<Long, Long> {
        return when (duration) {
            is Duration.Today -> getTodayRange()
            is Duration.ThisWeek -> getPreviousWeekRange(startDate)
            is Duration.ThisMonth -> getPreviousMonthRange(startDate)
            is Duration.ThisYear -> getPreviousYearRange(startDate)
            is Duration.Custom -> startDate to endDate
        }
    }

    fun getNextRange(
        duration: Duration,
        startDate: Long,
        endDate: Long
    ): Pair<Long, Long> {
        return when (duration) {
            is Duration.Today -> getTodayRange()
            is Duration.ThisWeek -> getNextWeekRange(startDate)
            is Duration.ThisMonth -> getNextMonthRange(startDate)
            is Duration.ThisYear -> getNextYearRange(startDate)
            is Duration.Custom -> startDate to endDate
        }
    }
}
