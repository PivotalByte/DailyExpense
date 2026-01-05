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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dailyexpense.ui.theme.LocalCustomColors
import kotlinx.datetime.LocalTime
import network.chaintech.kmp_date_time_picker.ui.timepicker.WheelTimePickerComponent.WheelTimePicker
import network.chaintech.kmp_date_time_picker.utils.TimeFormat
import network.chaintech.kmp_date_time_picker.utils.WheelPickerDefaults

@Composable
fun TimeSelectionBottomSheet(
    selectedHour: Int,
    selectedMinute: Int,
    onTimeSelected: (hour: Int, minute: Int) -> Unit,
    closeSheet: () -> Unit
) {
    var currentTime by remember {
        mutableStateOf(
            LocalTime(
                hour = selectedHour,
                minute = selectedMinute
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
            text = "Select Time",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurface
        )

        WheelTimePicker(
            modifier = Modifier.fillMaxWidth(),
            title = "",
            doneLabel = "",
            titleStyle = MaterialTheme.typography.titleLarge,
            doneLabelStyle = MaterialTheme.typography.labelLarge,
            startTime = LocalTime(
                hour = selectedHour,
                minute = selectedMinute
            ),
            minTime = LocalTime(
                hour = 0,
                minute = 0
            ),
            maxTime = LocalTime(
                hour = 23,
                minute = 59
            ),
            timeFormat = TimeFormat.AM_PM,
            rowCount = 5,
            height = 180.dp,
            hideHeader = true,
            selectorProperties = WheelPickerDefaults.selectorProperties(
                enabled = false
            ),
            selectedTextStyle = MaterialTheme.typography.titleMedium.copy(
                color = MaterialTheme.colorScheme.primary,
                fontFamily = MaterialTheme.typography.bodyLarge.fontFamily,
                fontSize = 18.sp
            ),
            defaultTextStyle = MaterialTheme.typography.titleSmall.copy(
                color = MaterialTheme.colorScheme.onSurface,
                fontFamily = MaterialTheme.typography.bodyLarge.fontFamily,
                fontSize = 16.sp
            ),
            onTimeChangeListener = { snappedTime ->
                currentTime = snappedTime
            }
        )

        Button(
            onClick = {
                onTimeSelected(
                    currentTime.hour,
                    currentTime.minute
                )
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
