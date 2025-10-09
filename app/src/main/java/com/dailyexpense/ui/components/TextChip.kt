package com.dailyexpense.ui.components

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dailyexpense.ui.theme.DailyExpenseTheme
import com.dailyexpense.ui.theme.LocalCustomColors

@Composable
fun TextChip(
    modifier: Modifier = Modifier,
    text: String,
    selectedColor: Color = LocalCustomColors.current.primaryColor,
    unselectedColor: Color = MaterialTheme.colorScheme.onSurfaceVariant,
    isSelected: Boolean,
    onSelected: () -> Unit
) {
    Box(
        modifier = modifier
            .clip(shape = RoundedCornerShape(size = 8.dp))
            .border(
                width = 1.dp,
                color = if (isSelected) selectedColor else unselectedColor,
                shape = RoundedCornerShape(8.dp)
            )
            .clickable { onSelected() }
            .background(
                color = if (isSelected) selectedColor else Color.Transparent
            )
            .padding(horizontal = 10.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall,
            color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
        )
    }

}

@Preview(name = "Unselected Chip - Light", showBackground = true)
@Preview(
    name = "Unselected Chip - Dark",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
fun TextChipItemUnselectedPreview() {
    DailyExpenseTheme(modifier = Modifier.wrapContentSize()) {
        TextChip(
            text = "Chip",
            isSelected = false,
            onSelected = {}
        )
    }
}

@Preview(name = "Selected Chip - Light", showBackground = true)
@Preview(
    name = "Selected Chip - Dark",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
fun TextChipItemSelectedPreview() {
    DailyExpenseTheme(modifier = Modifier.wrapContentSize()) {
        TextChip(
            text = "Chip",
            isSelected = true,
            onSelected = {}
        )
    }
}
