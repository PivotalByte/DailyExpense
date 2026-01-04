package com.dailyexpense.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dailyexpense.ui.theme.DailyExpenseTheme
import com.dailyexpense.ui.theme.LocalCustomColors

@Composable
fun CustomTextField(
    modifier: Modifier = Modifier,
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String = "",
    prefix: String = "",
    keyboardType: KeyboardType = KeyboardType.Text,
    validation: ((String) -> Boolean)? = null,
    trailingIcon: (@Composable () -> Unit)? = null
) {
    var isFocused by remember { mutableStateOf(false) }

    val borderColor by animateColorAsState(
        targetValue = if (isFocused) {
            LocalCustomColors.current.primaryColor
        } else {
            LocalCustomColors.current.searchBoxBorder
        },
        animationSpec = tween(durationMillis = 200),
        label = "borderColor"
    )

    val borderWidth by animateDpAsState(
        targetValue = if (isFocused) 2.dp else 1.dp,
        animationSpec = tween(durationMillis = 200),
        label = "borderWidth"
    )

    val shadowElevation by animateDpAsState(
        targetValue = if (isFocused) 4.dp else 0.dp,
        animationSpec = tween(durationMillis = 200),
        label = "shadowElevation"
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = shadowElevation,
                shape = RoundedCornerShape(size = 12.dp),
                spotColor = LocalCustomColors.current.primaryColor.copy(alpha = 0.25f)
            )
            .border(
                width = borderWidth,
                color = borderColor,
                shape = RoundedCornerShape(size = 12.dp)
            )
            .background(
                color = LocalCustomColors.current.cardBg,
                shape = RoundedCornerShape(size = 12.dp)
            )
            .padding(horizontal = 14.dp, vertical = 10.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall.copy(
                fontWeight = FontWeight.Medium,
                letterSpacing = 0.5.sp
            ),
            color = if (isFocused) {
                LocalCustomColors.current.primaryColor
            } else {
                MaterialTheme.colorScheme.onSurfaceVariant
            },
            modifier = Modifier.padding(bottom = 4.dp)
        )

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (prefix.isNotEmpty()) {
                Text(
                    text = prefix,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Normal
                    ),
                    color = if (value.isEmpty()) {
                        MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
                    } else {
                        LocalCustomColors.current.primaryColor
                    },
                    modifier = Modifier.padding(end = 6.dp)
                )
            }

            Box(
                modifier = Modifier.weight(1f)
            ) {
                if (value.isEmpty() && placeholder.isNotEmpty()) {
                    Text(
                        text = placeholder,
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Normal
                        ),
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f)
                    )
                }

                BasicTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .onFocusChanged { focusState ->
                            isFocused = focusState.isFocused
                        },
                    value = value,
                    onValueChange = { newValue ->
                        if (validation == null || validation(newValue)) {
                            onValueChange(newValue)
                        }
                    },
                    textStyle = MaterialTheme.typography.titleLarge.copy(
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Normal,
                        color = MaterialTheme.colorScheme.onSurface
                    ),
                    cursorBrush = SolidColor(LocalCustomColors.current.primaryColor),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = keyboardType
                    )
                )
            }

            trailingIcon?.invoke()
        }
    }
}

@Preview
@Composable
fun PreviewCustomTextField() {
    var amount by remember { mutableStateOf(value = "") }

    DailyExpenseTheme {
        CustomTextField(
            value = amount,
            onValueChange = { amount = it },
            label = "Amount",
            prefix = "₹",
            placeholder = "0.00",
            keyboardType = KeyboardType.Decimal,
            validation = { newValue ->
                newValue.isEmpty() || newValue.matches(Regex("^\\d*\\.?\\d{0,2}$"))
            }
        )
    }
}

@Preview
@Composable
fun PreviewCustomTextFieldWithValue() {
    var amount by remember { mutableStateOf(value = "1500.50") }

    DailyExpenseTheme {
        CustomTextField(
            value = amount,
            onValueChange = { amount = it },
            label = "Amount",
            prefix = "₹",
            placeholder = "0.00",
            keyboardType = KeyboardType.Decimal,
            validation = { newValue ->
                newValue.isEmpty() || newValue.matches(Regex("^\\d*\\.?\\d{0,2}$"))
            }
        )
    }
}

@Preview
@Composable
fun PreviewCustomTextFieldText() {
    var text by remember { mutableStateOf(value = "") }

    DailyExpenseTheme {
        CustomTextField(
            value = text,
            onValueChange = { text = it },
            label = "Description",
            placeholder = "Enter description"
        )
    }
}
