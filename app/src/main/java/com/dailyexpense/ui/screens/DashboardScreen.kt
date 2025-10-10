package com.dailyexpense.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.compose.rememberNavController
import com.dailyexpense.R
import com.dailyexpense.data.fakeCategories
import com.dailyexpense.data.fakeTransactions
import com.dailyexpense.data.room.entity.CategoryEntity
import com.dailyexpense.data.room.entity.TransactionEntity
import com.dailyexpense.presentation.DashboardViewModel
import com.dailyexpense.ui.components.AmountCard
import com.dailyexpense.ui.components.CategoryGrid
import com.dailyexpense.ui.components.TransactionRow
import com.dailyexpense.ui.theme.ExpenseColor
import com.dailyexpense.ui.theme.IncomeColor
import com.dailyexpense.ui.theme.LocalCustomColors
import com.dailyexpense.utils.extensions.toIndianCurrencyFormat

@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel = hiltViewModel()
) {
    val categories by viewModel.categories.collectAsState()
    val recentTransactions by viewModel.recentTransactions.collectAsState()
    val totalBalance by viewModel.totalBalance.collectAsState()
    val totalExpense by viewModel.totalExpense.collectAsState()
    val totalIncome by viewModel.totalIncome.collectAsState()

    DashboardScreenView(
        categories,
        recentTransactions,
        totalBalance = totalBalance.toIndianCurrencyFormat(),
        totalExpense = totalExpense.toIndianCurrencyFormat(),
        totalIncome = totalIncome.toIndianCurrencyFormat(),
    )
}

@Composable
fun DashboardScreenView(
    categories: List<CategoryEntity>,
    recentTransactions: List<TransactionEntity>,
    totalBalance: String,
    totalExpense: String,
    totalIncome: String
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(color = LocalCustomColors.current.appBg),
        verticalArrangement = Arrangement.spacedBy(space = 16.dp),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 80.dp)
    ) {
        item {
            Row(modifier = Modifier.fillMaxWidth()) {
                AmountCard(
                    modifier = Modifier.weight(weight = 1f),
                    shape = RoundedCornerShape(size = 8.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = LocalCustomColors.current.cardBg,
                        contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                    cardTitle = stringResource(id = R.string.label_total_balance),
                    cardAmount = stringResource(id = R.string.label_rupee_sign_with_value, totalBalance),
                    cardTitleColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    tooltipMsg = stringResource(id = R.string.tooltip_total_balance)
                )
            }
        }

        item {
            Row(modifier = Modifier.fillMaxWidth()) {
                AmountCard(
                    modifier = Modifier
                        .padding(end = 8.dp)
                        .weight(weight = 1f),
                    shape = RoundedCornerShape(size = 8.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = LocalCustomColors.current.cardBg,
                        contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                    cardTitle = stringResource(id = R.string.label_expense),
                    cardAmount = stringResource(id = R.string.label_rupee_sign_with_value, totalExpense),
                    cardTitleColor = ExpenseColor,
                    tooltipMsg = stringResource(id = R.string.tooltip_expense)
                )

                AmountCard(
                    modifier = Modifier
                        .padding(start = 8.dp)
                        .weight(weight = 1f),
                    shape = RoundedCornerShape(size = 8.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = LocalCustomColors.current.cardBg,
                        contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                    cardTitle = stringResource(id = R.string.label_income),
                    cardAmount = stringResource(id = R.string.label_rupee_sign_with_value, totalIncome),
                    cardTitleColor = IncomeColor,
                    tooltipMsg = stringResource(id = R.string.tooltip_income)
                )
            }
        }

        item {
            Text(
                text = stringResource(id = R.string.label_categories),
                style = MaterialTheme.typography.bodyLarge
            )
        }

        item {
            val context = LocalContext.current
            CategoryGrid(categories = categories, onCategoryClick = { clickedCategory ->
                Toast.makeText(context, clickedCategory.name, Toast.LENGTH_SHORT).show()
            })
        }

        item {
            Text(
                text = stringResource(id = R.string.label_recent_transactions),
                style = MaterialTheme.typography.bodyLarge
            )
        }

        items(count = recentTransactions.size) { index ->
            TransactionRow(transactionEntity = recentTransactions[index])
        }
    }
}

@Preview(
    showBackground = true,
    showSystemUi = true
)
@Composable
fun DashboardScreenPreview() {
    val navController = rememberNavController()

    MaterialTheme {
        DashboardScreenView(
            navController = navController,
            categories = fakeCategories,
            recentTransactions = fakeTransactions,
            totalBalance = "10,000.00",
            totalExpense = "10,000.00",
            totalIncome = "10,000.00"
        )
    }
}
