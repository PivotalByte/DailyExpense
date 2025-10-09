package com.dailyexpense.ui.bottomsheet.controller

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class BottomSheetController @OptIn(ExperimentalMaterial3Api::class) constructor(
    private val scope: CoroutineScope,
    val sheetState: SheetState
) {
    var sheetContent by mutableStateOf<(@Composable () -> Unit)?>(null)
        private set

    @OptIn(ExperimentalMaterial3Api::class)
    fun show(content: @Composable () -> Unit) {
        sheetContent = content
        scope.launch { sheetState.show() }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    fun hide() {
        scope.launch {
            sheetState.hide()
            sheetContent = null
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    fun isVisible() = sheetState.isVisible
}