package com.dailyexpense.ui.bottomsheet.factory

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import com.dailyexpense.ui.bottomsheet.controller.BottomSheetController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun rememberCustomBottomSheetController(): BottomSheetController {
    val scope = rememberCoroutineScope()
    val state = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    return remember { BottomSheetController(scope, state) }
}
