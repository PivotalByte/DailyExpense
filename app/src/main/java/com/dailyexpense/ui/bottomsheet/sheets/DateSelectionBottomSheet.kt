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
import androidx.compose.ui.unit.dp
import com.dailyexpense.ui.components.calendar.CalendarTitle
import com.dailyexpense.ui.components.calendar.Day
import com.dailyexpense.ui.components.calendar.MonthHeader
import com.dailyexpense.ui.theme.LocalCustomColors
import com.dailyexpense.utils.DateUtil.toLocalDate
import com.dailyexpense.utils.DateUtil.toMillis
import com.dailyexpense.utils.extensions.DateSelection
import com.dailyexpense.utils.rememberFirstMostVisibleMonth
import com.kizitonwose.calendar.compose.HorizontalCalendar
import com.kizitonwose.calendar.compose.rememberCalendarState
import com.kizitonwose.calendar.core.daysOfWeek
import com.kizitonwose.calendar.core.minusMonths
import com.kizitonwose.calendar.core.now
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate
import kotlinx.datetime.YearMonth
import kotlinx.datetime.minusMonth
import kotlinx.datetime.plusMonth

@Composable
fun DateSelectionBottomSheet(
    selectedDate: Long,
    onDateSelected: (Long) -> Unit,
    closeSheet: () -> Unit
) {
    val today = remember { LocalDate.now() }
    val currentMonth = remember { YearMonth.now() }
    val startMonth = remember { currentMonth.minusMonths(value = 500) }
    val endMonth = remember { currentMonth }
    val daysOfWeek = remember { daysOfWeek() }

    var selection by remember {
        mutableStateOf(
            DateSelection(
                startDate = selectedDate.toLocalDate(),
                endDate = null
            )
        )
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(all = 16.dp),
        verticalArrangement = Arrangement.spacedBy(space = 16.dp)
    ) {
        Text(
            text = "Select Date",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurface
        )

        val state = rememberCalendarState(
            startMonth = startMonth,
            endMonth = endMonth,
            firstVisibleMonth = currentMonth,
            firstDayOfWeek = daysOfWeek.first(),
        )
        val coroutineScope = rememberCoroutineScope()
        val visibleMonth = rememberFirstMostVisibleMonth(state, viewportPercent = 90f)

        CalendarTitle(
            modifier = Modifier.padding(vertical = 10.dp),
            today = today,
            currentMonth = visibleMonth.yearMonth,
            goToPrevious = {
                coroutineScope.launch {
                    state.animateScrollToMonth(
                        month = state.firstVisibleMonth.yearMonth.minusMonth()
                    )
                }
            },
            goToNext = {
                coroutineScope.launch {
                    val nextMonth = state.firstVisibleMonth.yearMonth.plusMonth()
                    if (nextMonth <= currentMonth) {
                        state.animateScrollToMonth(month = nextMonth)
                    }
                }
            },
        )

        HorizontalCalendar(
            state = state,
            dayContent = { day ->
                Day(day = day, today = today, selection = selection) { clickedDay ->
                    if (clickedDay.date <= today) {
                        selection = DateSelection(
                            startDate = clickedDay.date,
                            endDate = null
                        )
                    }
                }
            },
            monthHeader = {
                MonthHeader(daysOfWeek = daysOfWeek)
            },
        )

        Button(
            onClick = {
                onDateSelected(selection.startDate?.toMillis() ?: today.toMillis())
                closeSheet()
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = LocalCustomColors.current.primaryColor
            ),
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(size = 12.dp)
        ) {
            Text(
                text = "Confirm",
                style = MaterialTheme.typography.labelLarge,
                color = Color.White
            )
        }
    }
}
