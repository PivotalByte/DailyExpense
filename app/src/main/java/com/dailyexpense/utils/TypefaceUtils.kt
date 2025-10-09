package com.dailyexpense.utils

import android.graphics.Typeface
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalFontFamilyResolver
import androidx.compose.ui.text.font.FontFamily

@Composable
fun rememberTypeface(fontFamily: FontFamily? = null): Typeface? {
    val resolver = LocalFontFamilyResolver.current
    return resolver.resolve(fontFamily).value as? Typeface
}