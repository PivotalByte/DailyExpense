package com.dailyexpense.ui.bottomsheet

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.runtime.Composable
import com.dailyexpense.ui.bottomsheet.controller.BottomSheetController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomSheetHost(
    controller: BottomSheetController,
    content: @Composable () -> Unit
) {
    Box {
        content()

        if (controller.isVisible() && controller.sheetContent != null) {
            ModalBottomSheet(
                onDismissRequest = { controller.hide() },
                sheetState = controller.sheetState
            ) {
                controller.sheetContent?.invoke()
            }
        }
    }
}