package com.dailyexpense.ui.screens

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.dailyexpense.R
import com.dailyexpense.data.enums.TransactionType
import com.dailyexpense.data.fakeTransactionTypeChipDataNewTransaction
import com.dailyexpense.data.models.TransactionTypeChipData
import com.dailyexpense.presentation.NewTransactionViewModel
import com.dailyexpense.ui.components.BannerAdView
import com.dailyexpense.ui.components.CustomToolbar
import com.dailyexpense.ui.components.TextChip
import com.dailyexpense.ui.theme.DailyExpenseTheme

@Composable
fun NewTransactionScreen(
    navController: NavHostController,
    newTransactionViewModel: NewTransactionViewModel = hiltViewModel()
) {

    val transactionTypeState by newTransactionViewModel.transactionTypeState.collectAsState()

    val transactionTypeChipData = listOf(
        TransactionTypeChipData(
            type = TransactionType.EXPENSE,
            label = stringResource(id = R.string.label_expense)
        ),
        TransactionTypeChipData(
            type = TransactionType.INCOME,
            label = stringResource(id = R.string.label_income)
        ),
        TransactionTypeChipData(
            type = TransactionType.NEUTRAL,
            label = stringResource(id = R.string.label_neutral)
        )
    )

    NewTransactionScreenContent(
        navController = navController,
        transactionTypeState = transactionTypeState,
        transactionTypeChipData = transactionTypeChipData,
        onTransactionTypeChanged = { newType ->
            newTransactionViewModel.updateTransactionType(newType)
        }
    )


}

@Composable
fun NewTransactionScreenContent(
    navController: NavHostController,
    transactionTypeState: TransactionType,
    transactionTypeChipData: List<TransactionTypeChipData>,
    onTransactionTypeChanged: (TransactionType) -> Unit = {},
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Bottom
    ) {
        CustomToolbar(
            title = "New Transaction",
            showBack = true,
            onBack = { navController.popBackStack() })
        LazyColumn(
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(
                horizontal = 16.dp,
                vertical = 4.dp
            ),
            verticalArrangement = Arrangement.spacedBy(space = 12.dp)
        ) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(space = 8.dp)
                ) {
                    transactionTypeChipData.forEach { chip ->
                        Box(
                            modifier = Modifier.weight(weight = 1f)
                        ) {
                            TextChip(
                                modifier = Modifier.fillMaxWidth(),
                                text = chip.label,
                                isSelected = transactionTypeState == chip.type,
                                onSelected = {
                                    onTransactionTypeChanged(chip.type)
                                },
                            )
                        }
                    }
                }
            }
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(
                            width = 1.dp,
                            color = Color.LightGray,
                            shape = RoundedCornerShape(12.dp)
                        )
                        .clip(RoundedCornerShape(12.dp)) 
                        .padding(16.dp)
                )
                {
                    Text(
                        text = "Notes",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
        BannerAdView(adUnitId = stringResource(id = R.string.ad_fixed_size_banner))
    }
}

@Preview
@Composable
fun PreviewNewTransaction() {
    val navController = rememberNavController()
    DailyExpenseTheme {
        NewTransactionScreenContent(
            navController = navController,
            transactionTypeState = TransactionType.EXPENSE,
            transactionTypeChipData = fakeTransactionTypeChipDataNewTransaction,
            onTransactionTypeChanged = { }
        )
    }
}