package com.dailyexpense.ui.components

import androidx.annotation.DrawableRes
import androidx.compose.ui.graphics.Color
import androidx.core.graphics.toColorInt
import com.dailyexpense.R

@DrawableRes
fun categoryIconRes(iconResName: String): Int = when (iconResName) {
    "ic_category_groceries" -> R.drawable.ic_category_groceries
    "ic_category_medical" -> R.drawable.ic_category_medical
    "ic_category_fuel_transport" -> R.drawable.ic_category_fuel_transport
    "ic_category_restaurant" -> R.drawable.ic_category_restaurant
    "ic_category_salary" -> R.drawable.ic_category_salary
    "ic_category_business" -> R.drawable.ic_category_business
    "ic_category_investment" -> R.drawable.ic_category_investment
    else -> R.drawable.ic_category_more
}

fun categoryColor(colorCode: String, fallback: Color): Color = try {
    Color(colorCode.toColorInt())
} catch (_: IllegalArgumentException) {
    fallback
}
