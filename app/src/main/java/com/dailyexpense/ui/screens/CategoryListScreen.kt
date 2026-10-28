package com.dailyexpense.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.dailyexpense.R
import com.dailyexpense.data.enums.TransactionType
import com.dailyexpense.data.models.CategoryWithCount
import com.dailyexpense.data.room.entity.CategoryEntity
import com.dailyexpense.presentation.CategoryViewModel
import com.dailyexpense.ui.bottomsheet.BottomSheetHost
import com.dailyexpense.ui.bottomsheet.factory.rememberCustomBottomSheetController
import com.dailyexpense.ui.bottomsheet.sheets.CategoryEditorBottomSheet
import com.dailyexpense.ui.components.CategoryGrid
import com.dailyexpense.ui.components.CustomToolbar
import com.dailyexpense.ui.components.TextChip
import com.dailyexpense.ui.theme.DailyExpenseTheme
import com.dailyexpense.ui.theme.LocalCustomColors

@Composable
fun CategoryListScreen(
    navController: NavHostController,
    viewModel: CategoryViewModel = hiltViewModel()
) {
    val selectedType by viewModel.selectedType.collectAsState()
    val categories by viewModel.categories.collectAsState()
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
                    CategoryEditorBottomSheet(
                        state = editor,
                        onNameChange = viewModel::updateName,
                        onTypeChange = viewModel::updateType,
                        onColorChange = viewModel::updateColor,
                        onIconChange = viewModel::updateIcon,
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
        CategoryListScreenContent(
            selectedType = selectedType,
            categories = categories,
            onTypeSelected = viewModel::selectType,
            onCategoryClick = viewModel::startEdit,
            onAddClick = viewModel::startNew,
            onBackClick = { navController.popBackStack() }
        )
    }
}

@Composable
fun CategoryListScreenContent(
    selectedType: TransactionType,
    categories: List<CategoryWithCount>,
    onTypeSelected: (TransactionType) -> Unit,
    onCategoryClick: (CategoryWithCount) -> Unit,
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
                    contentDescription = stringResource(id = R.string.title_new_category),
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
                title = stringResource(id = R.string.label_categories),
                showBack = true,
                onBack = onBackClick
            )
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(LocalCustomColors.current.appBg)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf(
                        TransactionType.EXPENSE to R.string.label_expense,
                        TransactionType.INCOME to R.string.label_income
                    ).forEach { (type, label) ->
                        Box(modifier = Modifier.weight(1f)) {
                            TextChip(
                                modifier = Modifier.fillMaxWidth(),
                                text = stringResource(id = label),
                                isSelected = selectedType == type,
                                onSelected = { onTypeSelected(type) }
                            )
                        }
                    }
                }
                Text(
                    text = stringResource(id = R.string.hint_tap_category_to_edit),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                val byId = categories.associateBy { it.category.categoryId }
                CategoryGrid(
                    categories = categories.map { it.category },
                    onCategoryClick = { category -> byId[category.categoryId]?.let(onCategoryClick) }
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewCategoryListScreen() {
    DailyExpenseTheme {
        CategoryListScreenContent(
            selectedType = TransactionType.EXPENSE,
            categories = listOf(
                CategoryWithCount(
                    CategoryEntity(1, "Groceries", TransactionType.EXPENSE, "#4CAF50", "ic_category_groceries"),
                    transactionCount = 12
                ),
                CategoryWithCount(
                    CategoryEntity(2, "Medical", TransactionType.EXPENSE, "#F44336", "ic_category_medical"),
                    transactionCount = 0
                )
            ),
            onTypeSelected = {},
            onCategoryClick = {},
            onAddClick = {},
            onBackClick = {}
        )
    }
}
