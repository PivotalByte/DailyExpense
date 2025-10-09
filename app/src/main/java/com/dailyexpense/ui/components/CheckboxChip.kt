package com.dailyexpense.ui.components

import android.content.res.Configuration
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dailyexpense.ui.theme.DailyExpenseTheme
import com.dailyexpense.ui.theme.LocalCustomColors

@Composable
fun CheckboxChip(
    text: String,
    selectedColor: Color = LocalCustomColors.current.primaryColor,
    unselectedColor: Color = MaterialTheme.colorScheme.onSurfaceVariant,
    tickColor: Color = Color.White,
    isSelected: Boolean,
    onSelected: () -> Unit
) {
    Row(
        modifier = Modifier
            .border(
                width = 1.dp,
                color = if (isSelected) selectedColor else unselectedColor,
                shape = RoundedCornerShape(8.dp)
            )
            .clickable { onSelected() }
            .padding(horizontal = 10.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        CheckBox(
            modifier = Modifier.size(20.dp),
            isSelected = isSelected,
            selectedColor = LocalCustomColors.current.primaryColor,
            unselectedColor = MaterialTheme.colorScheme.onSurfaceVariant,
            tickColor = Color.White
        )

        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall,
            color = if (isSelected) selectedColor else unselectedColor
        )
    }
}


@Preview(name = "Unselected Chip - Light", showBackground = true)
@Composable
fun MultipleSelectChipItemUnselectedLightPreview() {
    DailyExpenseTheme(modifier = Modifier.wrapContentSize()) {
        CheckboxChip(
            text = "Chip",
            isSelected = false,
            onSelected = {}
        )
    }
}

@Preview(name = "Selected Chip - Light", showBackground = true)
@Composable
fun MultipleSelectChipItemSelectedLightPreview() {
    DailyExpenseTheme(modifier = Modifier.wrapContentSize()) {
        CheckboxChip(
            text = "Chip",
            isSelected = true,
            onSelected = {}
        )
    }
}

@Preview(
    name = "Unselected Chip - Dark",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
fun MultipleSelectChipItemUnselectedDarkPreview() {
    DailyExpenseTheme(modifier = Modifier.wrapContentSize()) {
        CheckboxChip(
            text = "Chip",
            isSelected = false,
            onSelected = {}
        )
    }
}

@Preview(
    name = "Selected Chip - Dark",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
fun MultipleSelectChipItemSelectedDarkPreview() {
    DailyExpenseTheme(modifier = Modifier.wrapContentSize()) {
        CheckboxChip(
            text = "Chip",
            isSelected = true,
            onSelected = {}
        )
    }
}