package com.dailyexpense.ui.theme

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

data class CustomColors(
    val primaryColor: Color,
    val bottomBarBg: Color,
    val appBg: Color,
    val cardBg: Color,
    val dashboardCardTitleBg: Color,
    val searchBoxBorder: Color
)

val LocalCustomColors = staticCompositionLocalOf {
    CustomColors(
        primaryColor = LightPrimary,
        bottomBarBg = LightBottomBarBg,
        appBg = LightSurFace,
        cardBg = LightSurfaceVariant,
        dashboardCardTitleBg = LightDashboardCardTitleBg,
        searchBoxBorder = LightSearchBoxBorder
    )
}
