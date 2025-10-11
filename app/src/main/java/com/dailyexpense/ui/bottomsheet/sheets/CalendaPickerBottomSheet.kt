package com.dailyexpense.ui.bottomsheet.sheets

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.dailyexpense.R
import com.dailyexpense.ui.components.calendar.CalendarTitle
import com.dailyexpense.ui.components.calendar.Day
import com.dailyexpense.ui.components.calendar.MonthHeader
import com.dailyexpense.ui.theme.LocalCustomColors
import com.dailyexpense.utils.extensions.ContinuousSelectionHelper.getSelection
import com.dailyexpense.utils.extensions.DateSelection
import com.dailyexpense.utils.rememberFirstMostVisibleMonth
import com.kizitonwose.calendar.compose.CalendarState
import com.kizitonwose.calendar.compose.HorizontalCalendar
import com.kizitonwose.calendar.compose.rememberCalendarState
import com.kizitonwose.calendar.core.CalendarMonth
import com.kizitonwose.calendar.core.daysOfWeek
import com.kizitonwose.calendar.core.minusMonths
import com.kizitonwose.calendar.core.now
import com.kizitonwose.calendar.core.plusMonths
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.YearMonth
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.minusMonth
import kotlinx.datetime.plusMonth
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
@Composable
fun CalendarPickerBottomSheet(
    onRangeSelected: (Long, Long) -> Unit,
    closeSheet: () -> Unit,
    startDate: LocalDate,
    endDate: LocalDate
) {
    val daysOfWeek = remember { daysOfWeek() }
    val currentMonth = remember(startDate) {
        YearMonth(startDate.year, startDate.month)
    }
    val startMonth = remember { currentMonth.minusMonths(value = 500) }
    val endMonth = remember { currentMonth.plusMonths(value = 500) }
    val today = remember { LocalDate.now() }
    val state = rememberCalendarState(
        startMonth = startMonth,
        endMonth = endMonth,
        firstVisibleMonth = currentMonth,
        firstDayOfWeek = daysOfWeek.first(),
    )
    val coroutineScope = rememberCoroutineScope()
    val visibleMonth =
        rememberFirstMostVisibleMonth(state, viewportPercent = 90f)

    var selection by remember(startDate, endDate) {
        mutableStateOf(
            DateSelection(
                startDate = startDate,
                endDate = endDate
            )
        )
    }
    CalendarPickerBottomSheetContent(
        today = today,
        visibleMonth = visibleMonth,
        state = state,
        coroutineScope = coroutineScope,
        daysOfWeek = daysOfWeek,
        selection = selection,
        onSelectionChanged = { selection = it },
        onApplyClicked = {
            val startMillis = selection.startDate?.atStartOfDayIn(TimeZone.currentSystemDefault())
                ?.toEpochMilliseconds()
            val endMillis = selection.endDate?.atStartOfDayIn(TimeZone.currentSystemDefault())
                ?.toEpochMilliseconds()
            if (startMillis != null && endMillis != null) {
                onRangeSelected(startMillis, endMillis)
            }

            closeSheet.invoke()
        }
    )
}

@Composable
fun CalendarPickerBottomSheetContent(
    today: LocalDate,
    visibleMonth: CalendarMonth,
    state: CalendarState,
    coroutineScope: CoroutineScope,
    daysOfWeek: List<DayOfWeek>,
    onApplyClicked: () -> Unit,
    onSelectionChanged: (DateSelection) -> Unit,
    selection: DateSelection
) {
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        Column(verticalArrangement = Arrangement.spacedBy(space = 4.dp)) {
            CalendarTitle(
                modifier = Modifier.padding(vertical = 10.dp),
                today = today,
                currentMonth = visibleMonth.yearMonth,
                goToPrevious = {
                    coroutineScope.launch {
                        state.animateScrollToMonth(month = state.firstVisibleMonth.yearMonth.minusMonth())
                    }
                },
                goToNext = {
                    coroutineScope.launch {
                        state.animateScrollToMonth(month = state.firstVisibleMonth.yearMonth.plusMonth())
                    }
                },
            )
            HorizontalCalendar(
                state = state,
                dayContent = { day ->
                    Day(day = day, today = today, selection = selection) { clicked ->
                        onSelectionChanged(
                            getSelection(clickedDate = clicked.date, dateSelection = selection)
                        )
                    }
                },
                monthHeader = {
                    MonthHeader(daysOfWeek = daysOfWeek)
                },
            )
        }

        Button(
            onClick = {
                onApplyClicked.invoke()
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = LocalCustomColors.current.primaryColor
            ),
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(size = 12.dp)
        ) {
            Text(
                text = stringResource(id = R.string.label_apply_dates),
                style = MaterialTheme.typography.labelLarge.copy(
                    color = Color.White
                )
            )
        }
    }
}
