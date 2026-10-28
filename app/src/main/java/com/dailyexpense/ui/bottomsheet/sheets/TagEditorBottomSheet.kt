package com.dailyexpense.ui.bottomsheet.sheets

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import com.dailyexpense.R
import com.dailyexpense.presentation.TagViewModel.EditorError
import com.dailyexpense.presentation.TagViewModel.EditorState
import com.dailyexpense.ui.components.CustomTextField
import com.dailyexpense.ui.theme.ExpenseColor

@Composable
fun TagEditorBottomSheet(
    state: EditorState,
    onNameChange: (String) -> Unit,
    onSave: () -> Unit,
    onDelete: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = stringResource(id = if (state.isNew) R.string.title_new_tag else R.string.title_rename_tag),
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurface
        )

        CustomTextField(
            value = state.name,
            onValueChange = onNameChange,
            label = stringResource(id = R.string.label_name),
            placeholder = stringResource(id = R.string.hint_tag_name),
            capitalization = KeyboardCapitalization.Words
        )

        if (!state.isNew && state.transactionCount > 0) {
            Text(
                text = pluralStringResource(
                    R.plurals.message_tag_usage,
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
                    EditorError.DuplicateName -> stringResource(id = R.string.error_tag_exists)
                },
                style = MaterialTheme.typography.bodySmall,
                color = ExpenseColor
            )
        }

        EditorButtons(isNew = state.isNew, onSave = onSave, onDelete = onDelete)
        Spacer(modifier = Modifier.height(16.dp))
    }
}
