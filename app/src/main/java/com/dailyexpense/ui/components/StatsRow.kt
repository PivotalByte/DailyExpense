package com.dailyexpense.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle

@Composable
fun StatsRow(
    statTitle: String,
    statAmount: String,
    titleStyle: TextStyle = MaterialTheme.typography.bodyMedium,
    amountStyle: TextStyle = MaterialTheme.typography.bodyMedium,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = statTitle,
            style = titleStyle,
            modifier = Modifier.weight(weight = 1f),
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Text(
            text = statAmount,
            style = amountStyle,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}