package com.dailyexpense.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Label
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.dailyexpense.R
import com.dailyexpense.data.models.TagWithCount
import com.dailyexpense.data.room.entity.TagEntity
import com.dailyexpense.presentation.TagViewModel
import com.dailyexpense.ui.bottomsheet.BottomSheetHost
import com.dailyexpense.ui.bottomsheet.factory.rememberCustomBottomSheetController
import com.dailyexpense.ui.bottomsheet.sheets.TagEditorBottomSheet
import com.dailyexpense.ui.components.CustomToolbar
import com.dailyexpense.ui.theme.DailyExpenseTheme
import com.dailyexpense.ui.theme.LocalCustomColors
import com.dailyexpense.utils.clickable

@Composable
fun TagListScreen(
    navController: NavHostController,
    viewModel: TagViewModel = hiltViewModel()
) {
    val tags by viewModel.tags.collectAsState()
    val isEditorOpen = viewModel.editor.collectAsState().value != null
    val bottomSheetController = rememberCustomBottomSheetController()

    // The ViewModel decides when the editor is open; the sheet follows it
    LaunchedEffect(isEditorOpen) {
        if (isEditorOpen) {
            bottomSheetController.show {
                val editor = viewModel.editor.collectAsState().value
                // Swiping the sheet away closes the editor too
                DisposableEffect(Unit) { onDispose { viewModel.dismissEditor() } }
                if (editor != null) {
                    TagEditorBottomSheet(
                        state = editor,
                        onNameChange = viewModel::updateName,
                        onSave = viewModel::save,
                        onDelete = viewModel::delete
                    )
                }
            }
        } else if (bottomSheetController.sheetContent != null) {
            bottomSheetController.hide()
        }
    }

    BottomSheetHost(controller = bottomSheetController) {
        TagListScreenContent(
            tags = tags,
            onTagClick = viewModel::startEdit,
            onAddClick = viewModel::startNew,
            onBackClick = { navController.popBackStack() }
        )
    }
}

@Composable
fun TagListScreenContent(
    tags: List<TagWithCount>,
    onTagClick: (TagWithCount) -> Unit,
    onAddClick: () -> Unit,
    onBackClick: () -> Unit
) {
    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddClick,
                containerColor = LocalCustomColors.current.primaryColor
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = stringResource(id = R.string.title_new_tag),
                    tint = Color.White
                )
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            CustomToolbar(
                title = stringResource(id = R.string.label_tags),
                showBack = true,
                onBack = onBackClick
            )
            if (tags.isEmpty()) {
                EmptyTagsState()
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(LocalCustomColors.current.appBg),
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 80.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(count = tags.size, key = { index -> tags[index].tag.tagId }) { index ->
                        TagListItem(item = tags[index], onClick = { onTagClick(tags[index]) })
                    }
                }
            }
        }
    }
}

@Composable
private fun TagListItem(item: TagWithCount, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = LocalCustomColors.current.cardBg),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            TagChip(tag = item.tag)
            Text(
                text = pluralStringResource(R.plurals.count_transactions, item.transactionCount, item.transactionCount),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun EmptyTagsState() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(LocalCustomColors.current.appBg),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.Label,
                contentDescription = null,
                modifier = Modifier.size(80.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f)
            )
            Text(
                text = stringResource(id = R.string.message_no_tags),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewTagListScreen() {
    DailyExpenseTheme {
        TagListScreenContent(
            tags = listOf(
                TagWithCount(TagEntity(tagId = 1, name = "Family"), transactionCount = 12),
                TagWithCount(TagEntity(tagId = 2, name = "Trip"), transactionCount = 1)
            ),
            onTagClick = {},
            onAddClick = {},
            onBackClick = {}
        )
    }
}
