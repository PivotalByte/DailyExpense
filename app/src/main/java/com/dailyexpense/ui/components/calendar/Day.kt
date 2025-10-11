package com.dailyexpense.ui.components.calendar

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.dailyexpense.ui.theme.LocalCustomColors
import com.dailyexpense.utils.extensions.DateSelection
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.core.DayPosition
import kotlinx.datetime.LocalDate

@Composable
fun Day(
    day: CalendarDay,
    today: LocalDate,
    selection: DateSelection,
    onClick: (CalendarDay) -> Unit
) {
    var textColor = Color.Transparent
    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .clickable(
                enabled = day.position == DayPosition.MonthDate && day.date <= today,
                indication = null,
                onClick = { onClick(day) },
                interactionSource = remember { MutableInteractionSource() }
            ).backgroundHighlight(
                day = day,
                today = today,
                selection = selection,
                selectionColor = LocalCustomColors.current.primaryColor,
                continuousSelectionColor = LocalCustomColors.current.primaryColor.copy(alpha = 0.2f),
            ) {
                textColor = it
            },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = day.date.day.toString(),
            color = textColor,
            style = MaterialTheme.typography.labelLarge
        )
    }
}
