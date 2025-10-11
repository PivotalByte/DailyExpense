package com.dailyexpense.ui.components.calendar

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dailyexpense.R
import com.dailyexpense.data.models.Duration
import com.dailyexpense.ui.theme.DailyExpenseTheme
import com.dailyexpense.ui.theme.LocalCustomColors

@Composable
fun CalendarRangeSelector(
    modifier: Modifier = Modifier,
    selectedRange: String,
    isNextEnabled: Boolean,
    goToPrevious: () -> Unit,
    goToNext: () -> Unit,
    duration: Duration,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(height = 40.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (duration != Duration.Custom) {
            CalendarNavigationIcon(
                painter = painterResource(id = R.drawable.ic_left_square),
                isEnabled = true,
                contentDescription = "Previous",
                onClick = goToPrevious,
            )
        }

        Box(
            modifier = Modifier
                .weight(weight = 1f),
            contentAlignment = Alignment.Center
        ) {
            Card(
                modifier = Modifier.wrapContentSize(),
                shape = RoundedCornerShape(size = 6.dp),
                colors = CardDefaults.cardColors(
                    containerColor = LocalCustomColors.current.cardBg,
                    contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Text(
                    modifier = Modifier
                        .padding(horizontal = 12.dp, vertical = 4.dp)
                        .testTag(tag = "MonthTitle"),
                    text = selectedRange,
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Normal
                    ),
                    textAlign = TextAlign.Center,
                )
            }
        }

        if (duration != Duration.Custom) {
            CalendarNavigationIcon(
                painter = painterResource(id = R.drawable.ic_rigth_square),
                isEnabled = isNextEnabled,
                contentDescription = "Next",
                onClick = goToNext
            )
        }
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
        .aspectRatio(ratio = 1f)
        .clickable(
            enabled = isEnabled,
            role = Role.Button,
            onClick = onClick
        ),
    contentAlignment = Alignment.Center
) {
    Icon(
        painter = painter,
        contentDescription = contentDescription,
        tint = if (!isEnabled) Color.Gray else MaterialTheme.colorScheme.primary,
    )
}

@Preview
@Composable
fun PreviewCalendarRangeSelector() {
    DailyExpenseTheme(modifier = Modifier.wrapContentSize()) {
        CalendarRangeSelector(
            selectedRange = "22 Sep - 28 Sep",
            isNextEnabled = true,
            goToPrevious = { },
            goToNext = { },
            duration = Duration.ThisWeek
        )
    }
}
