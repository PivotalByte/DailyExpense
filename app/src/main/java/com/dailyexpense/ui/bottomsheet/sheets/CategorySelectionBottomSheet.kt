package com.dailyexpense.ui.bottomsheet.sheets

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.dailyexpense.data.room.entity.CategoryEntity
import com.dailyexpense.ui.components.CategoryGrid

@Composable
fun CategorySelectionBottomSheet(
    categories: List<CategoryEntity>,
    selectedCategory: CategoryEntity?,
    onCategorySelected: (CategoryEntity) -> Unit,
    closeSheet: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Select Category",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurface
        )

        if (categories.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No categories available",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            CategoryGrid(
                categories = categories,
                selectedCategoryId = selectedCategory?.categoryId,
                onCategoryClick = { category ->
                    onCategorySelected(category)
                    closeSheet()
                }
            )
        }
    }
}
