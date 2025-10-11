package com.dailyexpense.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.toColorInt
import com.dailyexpense.R
import com.dailyexpense.data.enums.TransactionType
import com.dailyexpense.data.room.entity.CategoryEntity

@Composable
fun CategoryCard(category: CategoryEntity, onCategoryClick: (CategoryEntity) -> Unit) {
    val backgroundColor = try {
        Color(category.colorCode.toColorInt())
    } catch (_: IllegalArgumentException) {
        Color.White
    }

    val iconResId = when (category.iconResName) {
        "ic_category_groceries" -> R.drawable.ic_category_groceries
        "ic_category_medical" -> R.drawable.ic_category_medical
        "ic_category_fuel_transport" -> R.drawable.ic_category_fuel_transport
        "ic_category_restaurant" -> R.drawable.ic_category_restaurant
        "ic_category_salary" -> R.drawable.ic_category_salary
        "ic_category_business" -> R.drawable.ic_category_business
        "ic_category_investment" -> R.drawable.ic_category_investment
        else -> R.drawable.ic_category_more
    }

    Card(
        modifier = Modifier
            .aspectRatio(1f)
            .clickable { onCategoryClick(category) }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(backgroundColor),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = iconResId),
                contentDescription = category.name,
                modifier = Modifier.size(35.dp)
            )
            Text(
                modifier = Modifier.padding(all = 4.dp),
                text = category.name,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = 10.sp,
                    lineHeight = 10.sp
                ),
                color = Color.White,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Preview
@Composable
fun PreviewCategoryCard() {
    val category =
        CategoryEntity("Groceries", TransactionType.EXPENSE, "ic_category_groceries", "#FF0000")

    CategoryCard(category) {}
}
