package com.dailyexpense.ui.bottomsheet.sheets

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.dailyexpense.R
import com.dailyexpense.data.enums.SortOption
import com.dailyexpense.data.enums.SortOrder
import com.dailyexpense.data.models.SortState
import com.dailyexpense.presentation.SortViewModel
import com.dailyexpense.ui.components.SortOptionCard
import com.dailyexpense.ui.theme.DailyExpenseTheme
import com.dailyexpense.ui.theme.LocalCustomColors

@Composable
fun SortBottomSheet(
    closeSheet: () -> Unit,
    viewModel: SortViewModel = hiltViewModel()
) {
    val selectedSortState by viewModel.sortState.collectAsState()

    var localSelectedState by remember { mutableStateOf(selectedSortState) }

    SortBottomSheetContent(
        localSelectedState = localSelectedState,
        onSortSelected = { updatedState ->
            localSelectedState = updatedState
        },
        onApplySort = { selectedOption ->
            viewModel.updateSort(newState = selectedOption)
            closeSheet()
        }
    )
}

@Composable
fun SortBottomSheetContent(
    localSelectedState: SortState,
    onSortSelected: (SortState) -> Unit,
    onApplySort: (SortState) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            /** Sort By Section */
            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = stringResource(R.string.label_sort_by),
                        style = MaterialTheme.typography.bodyLarge.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )

                    SortOptionCard(
                        title = stringResource(R.string.label_sort_by_date),
                        isSelected = localSelectedState.sortOption == SortOption.DATE,
                        onClick = {
                            onSortSelected(localSelectedState.copy(sortOption = SortOption.DATE))
                        }
                    )

                    SortOptionCard(
                        title = stringResource(R.string.label_sort_by_amount),
                        isSelected = localSelectedState.sortOption == SortOption.AMOUNT,
                        onClick = {
                            onSortSelected(localSelectedState.copy(sortOption = SortOption.AMOUNT))
                        }
                    )
                }
            }

            /** Order By Section */
            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = stringResource(R.string.label_order_by),
                        style = MaterialTheme.typography.bodyLarge.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )

                    SortOptionCard(
                        title = stringResource(R.string.label_ascending),
                        isSelected = localSelectedState.sortOrder == SortOrder.ASCENDING,
                        onClick = {
                            onSortSelected(localSelectedState.copy(sortOrder = SortOrder.ASCENDING))
                        }
                    )

                    SortOptionCard(
                        title = stringResource(R.string.label_descending),
                        isSelected = localSelectedState.sortOrder == SortOrder.DESCENDING,
                        onClick = {
                            onSortSelected(localSelectedState.copy(sortOrder = SortOrder.DESCENDING))
                        }
                    )
                }
            }
        }

        /** Apply Button */
        Button(
            onClick = { onApplySort(localSelectedState) },
            colors = ButtonDefaults.buttonColors(
                containerColor = LocalCustomColors.current.primaryColor
            ),
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(
                text = stringResource(R.string.label_apply_sort),
                style = MaterialTheme.typography.labelLarge.copy(color = Color.White)
            )
        }
    }
}

@Preview
@Composable
fun PreviewSortBottomSheet() {
    DailyExpenseTheme(modifier = Modifier.wrapContentSize()) {
        SortBottomSheetContent(
            localSelectedState = SortState(),
            onSortSelected = {},
            onApplySort = {}
        )
    }
}
