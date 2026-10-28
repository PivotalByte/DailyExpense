package com.dailyexpense.ui.bottomsheet.sheets

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import com.dailyexpense.R
import com.dailyexpense.data.enums.TransactionType
import com.dailyexpense.data.models.CATEGORY_COLORS
import com.dailyexpense.data.models.CATEGORY_ICONS
import com.dailyexpense.presentation.CategoryViewModel.EditorError
import com.dailyexpense.presentation.CategoryViewModel.EditorState
import com.dailyexpense.ui.components.CustomTextField
import com.dailyexpense.ui.components.TextChip
import com.dailyexpense.ui.components.categoryColor
import com.dailyexpense.ui.components.categoryIconRes
import com.dailyexpense.ui.theme.ExpenseColor
import com.dailyexpense.ui.theme.LocalCustomColors
import com.dailyexpense.utils.clickable

@Composable
fun CategoryEditorBottomSheet(
    state: EditorState,
    onNameChange: (String) -> Unit,
    onTypeChange: (TransactionType) -> Unit,
    onColorChange: (String) -> Unit,
    onIconChange: (String) -> Unit,
    onSave: () -> Unit,
    onDelete: () -> Unit
) {
    val selectedColor = categoryColor(state.colorCode, fallback = MaterialTheme.colorScheme.primary)
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = stringResource(id = if (state.isNew) R.string.title_new_category else R.string.title_edit_category),
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurface
        )

        CustomTextField(
            value = state.name,
            onValueChange = onNameChange,
            label = stringResource(id = R.string.label_name),
            placeholder = stringResource(id = R.string.hint_category_name),
            capitalization = KeyboardCapitalization.Words
        )

        if (state.isNew) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf(
                    TransactionType.EXPENSE to R.string.label_expense,
                    TransactionType.INCOME to R.string.label_income
                ).forEach { (type, label) ->
                    Box(modifier = Modifier.weight(1f)) {
                        TextChip(
                            modifier = Modifier.fillMaxWidth(),
                            text = stringResource(id = label),
                            isSelected = state.transactionType == type,
                            onSelected = { onTypeChange(type) }
                        )
                    }
                }
            }
        }

        PickerLabel(text = stringResource(id = R.string.label_colour))
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            CATEGORY_COLORS.forEach { colorCode ->
                val isSelected = colorCode == state.colorCode
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(categoryColor(colorCode, fallback = Color.Gray))
                        .clickable { onColorChange(colorCode) },
                    contentAlignment = Alignment.Center
                ) {
                    if (isSelected) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }

        PickerLabel(text = stringResource(id = R.string.label_icon))
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            CATEGORY_ICONS.forEach { iconResName ->
                val isSelected = iconResName == state.iconResName
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isSelected) selectedColor else selectedColor.copy(alpha = 0.35f))
                        .then(
                            if (isSelected) {
                                Modifier.border(2.dp, MaterialTheme.colorScheme.onSurface, RoundedCornerShape(8.dp))
                            } else {
                                Modifier
                            }
                        )
                        .clickable { onIconChange(iconResName) },
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = categoryIconRes(iconResName)),
                        contentDescription = null,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }
        }

        if (!state.isNew && state.transactionCount > 0) {
            Text(
                text = pluralStringResource(
                    R.plurals.message_category_usage,
                    state.transactionCount,
                    state.transactionCount
                ),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        state.error?.let { error ->
            Text(
                text = when (error) {
                    EditorError.EmptyName -> stringResource(id = R.string.error_name_required)
                    EditorError.DuplicateName -> stringResource(id = R.string.error_category_exists)
                    EditorError.InUse -> stringResource(id = R.string.error_category_in_use)
                },
                style = MaterialTheme.typography.bodySmall,
                color = ExpenseColor
            )
        }

        EditorButtons(isNew = state.isNew, onSave = onSave, onDelete = onDelete)
        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun PickerLabel(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )
}

/** Save, plus Delete for existing items; shared by the category and tag editors. */
@Composable
fun EditorButtons(isNew: Boolean, onSave: () -> Unit, onDelete: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        if (!isNew) {
            TextButton(
                onClick = onDelete,
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = stringResource(id = R.string.label_delete),
                    style = MaterialTheme.typography.labelLarge,
                    color = ExpenseColor
                )
            }
        }
        Button(
            onClick = onSave,
            modifier = Modifier.weight(1f),
            colors = ButtonDefaults.buttonColors(containerColor = LocalCustomColors.current.primaryColor),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(
                text = stringResource(id = R.string.label_save),
                style = MaterialTheme.typography.labelLarge,
                color = Color.White
            )
        }
    }
}
