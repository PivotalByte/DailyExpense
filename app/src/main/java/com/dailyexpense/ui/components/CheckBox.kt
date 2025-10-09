package com.dailyexpense.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import com.dailyexpense.R
import com.dailyexpense.ui.theme.LocalCustomColors


@Composable
fun CheckBox(
    modifier: Modifier,
    isSelected: Boolean,
    selectedColor: Color = LocalCustomColors.current.primaryColor,
    unselectedColor: Color = MaterialTheme.colorScheme.onSurfaceVariant,
    tickColor: Color = Color.White,
){
    Box(
        contentAlignment = Alignment.Center
    ) {
        if (isSelected) {
            Icon(
                painter = painterResource(id = R.drawable.ic_square_filled),
                contentDescription = "Selected Background",
                tint = selectedColor,
                modifier = modifier
            )
        }

        Icon(
            painter = if (isSelected) painterResource(id = R.drawable.ic_tick)
            else painterResource(id = R.drawable.ic_tick_empty),
            contentDescription = "Tick Icon",
            tint = if (isSelected) tickColor else unselectedColor,
            modifier = modifier
        )
    }
}