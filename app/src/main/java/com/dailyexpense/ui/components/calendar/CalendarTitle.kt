package com.dailyexpense.ui.components.calendar

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.dailyexpense.R
import com.dailyexpense.utils.extensions.getDisplayMonthYear
import kotlinx.datetime.LocalDate
import kotlinx.datetime.YearMonth
import kotlinx.datetime.number

@Composable
fun CalendarTitle(
    modifier: Modifier,
    today: LocalDate,
    currentMonth: YearMonth,
    goToPrevious: () -> Unit,
    goToNext: () -> Unit,
) {

    val todayYearMonth = YearMonth(today.year, today.month.number)
    val isNextEnabled = currentMonth < todayYearMonth

    Row(
        modifier = modifier.height(40.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        CalendarNavigationIcon(
            painter = painterResource(R.drawable.ic_left_square),
            isEnabled = true,
            contentDescription = "Previous",
            onClick = goToPrevious,
        )
        Text(
            modifier = Modifier
                .weight(1f)
                .testTag("MonthTitle"),
            text = currentMonth.getDisplayMonthYear(),
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Center,
        )
        CalendarNavigationIcon(
            painter = painterResource(R.drawable.ic_rigth_square),
            isEnabled = isNextEnabled,
            contentDescription = "Next",
            onClick = goToNext
        )
    }
}

@Composable
private fun CalendarNavigationIcon(
    painter: Painter,
    contentDescription: String,
    onClick: () -> Unit,
    isEnabled: Boolean,
) = Box(
    modifier = Modifier
        .fillMaxHeight()
        .aspectRatio(1f)
        .clickable(
            enabled = isEnabled,
            role = Role.Button,
            onClick = onClick
        ),
) {

    Icon(
        painter = painter,
        contentDescription = contentDescription,
        tint = if (!isEnabled) Color.Gray else MaterialTheme.colorScheme.primary,
        modifier = Modifier
            .fillMaxSize()
            .align(Alignment.Center),
    )
}
