package com.dailyexpense.ui.theme

import android.os.Build
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = DarkPrimary,
    secondary = PurpleGrey80,
    tertiary = Pink80,
)

private val LightColorScheme = lightColorScheme(
    primary = LightPrimary,
    secondary = PurpleGrey40,
    tertiary = Pink40,
)

@Composable
fun DailyExpenseTheme(
    modifier: Modifier = Modifier,
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    val customColors = if (darkTheme) {
        CustomColors(
            primaryColor = DarkPrimary,
            bottomBarBg = DarkBottomBarBg,
            appBg = DarkSurface,
            cardBg = DarkSurfaceVariant,
            dashboardCardTitleBg = DarkDashboardCardTitleBg,
            searchBoxBorder = DarkSearchBoxBorder
        )
    } else {
        CustomColors(
            primaryColor = LightPrimary,
            bottomBarBg = LightBottomBarBg,
            appBg = LightSurFace,
            cardBg = LightSurfaceVariant,
            dashboardCardTitleBg = LightDashboardCardTitleBg,
            searchBoxBorder = LightSearchBoxBorder
        )
    }

    CompositionLocalProvider(
        LocalCustomColors provides customColors
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography
        ) {
            Surface(
                modifier = modifier
                    .fillMaxSize()
                    .background(color = LocalCustomColors.current.appBg),
                ) {
                content()
            }
        }
    }
}
