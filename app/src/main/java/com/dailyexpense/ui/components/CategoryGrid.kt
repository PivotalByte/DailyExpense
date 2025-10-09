package com.dailyexpense.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dailyexpense.data.enums.TransactionType
import com.dailyexpense.data.room.entity.CategoryEntity

@Composable
fun CategoryGrid(
    categories: List<CategoryEntity>,
    onCategoryClick: (CategoryEntity) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        categories.chunked(4).forEach { rowItems ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                rowItems.forEach { category ->
                    Box(
                        modifier = Modifier
                            .weight(1f) // Equal width for all items
                            .padding(8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CategoryCard(category, onCategoryClick)
                    }
                }

                // Fill empty spaces if row has < 4 items
                repeat(4 - rowItems.size) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}


@Preview
@Composable
fun PreviewCategoryGrid() {
    val category1 =
        CategoryEntity("Groceries", TransactionType.EXPENSE, "ic_category_groceries", "#FF0000")
    val category2 =
        CategoryEntity("Groceries", TransactionType.EXPENSE, "ic_category_groceries", "#FF0000")
    val category3 =
        CategoryEntity("Groceries", TransactionType.EXPENSE, "ic_category_groceries", "#FF0000")
    val category4 =
        CategoryEntity("Groceries", TransactionType.EXPENSE, "ic_category_groceries", "#FF0000")
    CategoryGrid(listOf(category1, category2, category3, category4), {})
}