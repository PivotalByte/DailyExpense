package com.dailyexpense.utils.extensions

import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

fun Double.toIndianCurrencyFormat(): String {
    val symbols = DecimalFormatSymbols(Locale.forLanguageTag("en-IN"))
    val formatter = DecimalFormat("##,##,##0.00", symbols)
    return formatter.format(this)
}