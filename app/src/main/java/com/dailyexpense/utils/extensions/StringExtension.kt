package com.dailyexpense.utils.extensions

fun String.toReadableFormat(): String {
    return this.lowercase()
        .replace(oldValue = "_", newValue = " ")
        .split(" ")
        .joinToString(separator = " ") { word ->
            word.replaceFirstChar { it.uppercase() }
        }
}
