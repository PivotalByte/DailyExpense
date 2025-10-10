@file:OptIn(ExperimentalTime::class)

package com.dailyexpense.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.dailyexpense.R
import com.dailyexpense.data.enums.getTransactionCategoryAsString
import com.dailyexpense.data.fakeDurationAnalyticsChipData
import com.dailyexpense.data.models.CategorySummary
import com.dailyexpense.data.models.Duration
import com.dailyexpense.data.models.TransactionCategorySummary
import com.dailyexpense.data.models.TransactionStats
import com.dailyexpense.presentation.AnalyticsViewModel
import com.dailyexpense.ui.bottomsheet.BottomSheetHost
import com.dailyexpense.ui.bottomsheet.controller.BottomSheetController
import com.dailyexpense.ui.bottomsheet.factory.rememberCustomBottomSheetController
import com.dailyexpense.ui.bottomsheet.sheets.CalendarPickerBottomSheet
import com.dailyexpense.ui.components.AmountCard
import com.dailyexpense.ui.components.StatsRow
import com.dailyexpense.ui.components.TextChip
import com.dailyexpense.ui.components.calendar.CalendarRangeSelector
import com.dailyexpense.ui.components.chart.PieSummary
import com.dailyexpense.ui.theme.DailyExpenseTheme
import com.dailyexpense.ui.theme.ExpenseColor
import com.dailyexpense.ui.theme.IncomeColor
import com.dailyexpense.ui.theme.LocalCustomColors
import com.dailyexpense.utils.DateUtil.getNextRange
import com.dailyexpense.utils.DateUtil.getPreviousRange
import com.dailyexpense.utils.DateUtil.getSelectedDateRangeAsString
import com.dailyexpense.utils.DateUtil.getThisWeekRange
import com.dailyexpense.utils.DateUtil.isNextEnabledForDuration
import com.dailyexpense.utils.DateUtil.toLocalDate
import com.dailyexpense.utils.DateUtil.toMillis
import com.dailyexpense.utils.extensions.toIndianCurrencyFormat
import com.kizitonwose.calendar.core.minusDays
import com.kizitonwose.calendar.core.now
import kotlinx.datetime.LocalDate
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnalyticsScreen(
    analyticsViewModel: AnalyticsViewModel = hiltViewModel(),
) {
    val durationState by analyticsViewModel.duration.collectAsState()
    val startDate by analyticsViewModel.startDate.collectAsState()
    val endDate by analyticsViewModel.endDate.collectAsState()
    val totalExpense by analyticsViewModel.totalExpense.collectAsState(initial = 0.0)
    val totalIncome by analyticsViewModel.totalIncome.collectAsState(initial = 0.0)
    val categoryExpenseSummary by analyticsViewModel.categoriesExpenseSummary.collectAsState()
    val categoryIncomeSummary by analyticsViewModel.categoriesIncomeSummary.collectAsState()
    val transactionCategoryExpenseSummary by analyticsViewModel.transactionCategoriesExpenseSummary.collectAsState()
    val transactionCategoryIncomeSummary by analyticsViewModel.transactionCategoriesIncomeSummary.collectAsState()
    val stats by analyticsViewModel.stats.collectAsState()

    val durationChipData = listOf(
        Duration.ThisWeek to stringResource(id = R.string.label_week),
        Duration.ThisMonth to stringResource(id = R.string.label_month),
        Duration.ThisYear to stringResource(id = R.string.label_year),
        Duration.Custom to stringResource(id = R.string.label_custom),
    )

    val bottomSheetController = rememberCustomBottomSheetController()
    BottomSheetHost(controller = bottomSheetController) {
        AnalyticsScreenContent(
            durationState = durationState,
            onDurationChanged = { newDuration -> analyticsViewModel.updateDuration(newDuration) },
            durationChipData = durationChipData,
            categoryExpenseSummary = categoryExpenseSummary,
            categoryIncomeSummary = categoryIncomeSummary,
            transactionCategoryExpenseSummary = transactionCategoryExpenseSummary,
            transactionCategoryIncomeSummary = transactionCategoryIncomeSummary,
            stats = stats,
            startDate = startDate,
            endDate = endDate,
            onRangeChanged = { start, end -> analyticsViewModel.updateDateRange(start, end) },
            totalExpense = totalExpense.toIndianCurrencyFormat(),
            totalIncome = totalIncome.toIndianCurrencyFormat(),
            bottomSheetController = bottomSheetController
        )
    }
}

@Composable
fun AnalyticsScreenContent(
    durationState: Duration,
    onDurationChanged: (Duration) -> Unit,
    durationChipData: List<Pair<Duration, String>>,
    startDate: Long,
    endDate: Long,
    onRangeChanged: (Long, Long) -> Unit,
    totalExpense: String,
    totalIncome: String,
    categoryExpenseSummary: List<CategorySummary>,
    categoryIncomeSummary: List<CategorySummary>,
    transactionCategoryExpenseSummary: List<TransactionCategorySummary>,
    transactionCategoryIncomeSummary: List<TransactionCategorySummary>,
    stats: TransactionStats,
    bottomSheetController: BottomSheetController,
) {
    var selectedExpenseCategory by remember { mutableStateOf<String?>(value = null) }
    var selectedIncomeCategory by remember { mutableStateOf<String?>(value = null) }

    val today = remember { LocalDate.now() }
    var customStartDate by remember { mutableStateOf(value = today.minusDays(value = 2)) }
    var customEndDate by remember { mutableStateOf(value = today) }

    LazyColumn(
        modifier = Modifier
            .fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(space = 8.dp),
        contentPadding = PaddingValues(all = 16.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(space = 8.dp)
            ) {
                durationChipData.forEach { (duration, label) ->
                    Box(
                        modifier = Modifier.weight(weight = 1f)
                    ) {
                        TextChip(
                            modifier = Modifier.fillMaxWidth(),
                            text = label,
                            isSelected = durationState == duration,
                            onSelected = {
                                if (duration == Duration.Custom) {
                                    bottomSheetController.show {
                                        CalendarPickerBottomSheet(
                                            startDate = customStartDate,
                                            endDate = customEndDate,
                                            onRangeSelected = { startMillis, endMillis ->
                                                val start = startMillis.toLocalDate()
                                                val end = endMillis.toLocalDate()

                                                customStartDate = start
                                                customEndDate = end

                                                onRangeChanged(startMillis, endMillis)
                                                onDurationChanged(Duration.Custom)
                                            },
                                            closeSheet = { bottomSheetController.hide() }
                                        )
                                    }
                                } else {
                                    onDurationChanged(duration)
                                }
                            },
                        )
                    }
                }
            }
        }
        item {
            CalendarRangeSelector(
                selectedRange = getSelectedDateRangeAsString(
                    duration = durationState,
                    startDate = if (durationState == Duration.Custom) customStartDate.toMillis() else startDate,
                    endDate = if (durationState == Duration.Custom) customEndDate.toMillis() else endDate
                ),
                isNextEnabled = isNextEnabledForDuration(
                    duration = durationState,
                    endDate = endDate
                ),
                goToPrevious = {
                    val (start, end) = getPreviousRange(
                        duration = durationState,
                        startDate = startDate,
                        endDate = endDate
                    )
                    onRangeChanged(start, end)
                },
                goToNext = {
                    if (isNextEnabledForDuration(duration = durationState, endDate = endDate)) {
                        val (start, end) = getNextRange(
                            duration = durationState,
                            startDate = startDate,
                            endDate = endDate
                        )
                        onRangeChanged(start, end)
                    }
                },
                duration = durationState
            )
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
                    cardAmount = stringResource(
                        id = R.string.label_rupee_sign_with_value,
                        totalExpense
                    ),
                    cardTitleColor = ExpenseColor,
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
                    cardAmount = stringResource(
                        id = R.string.label_rupee_sign_with_value,
                        totalIncome
                    ),
                    cardTitleColor = IncomeColor,
                )
            }
        }
        item {
            Text(
                text = stringResource(id = R.string.label_categories),
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold
                ),
            )
        }
        item {
            Column(verticalArrangement = Arrangement.spacedBy(space = 16.dp)) {
                PieSummary(
                    summary = categoryExpenseSummary,
                    title = stringResource(id = R.string.category_wise_expenses),
                    selectedCategory = selectedExpenseCategory,
                    onCategorySelected = { categorySelected: String? ->
                        selectedExpenseCategory = categorySelected
                    }
                )
                PieSummary(
                    summary = categoryIncomeSummary,
                    title = stringResource(id = R.string.category_wise_income),
                    selectedCategory = selectedIncomeCategory,
                    onCategorySelected = { categorySelected: String? ->
                        selectedIncomeCategory = categorySelected
                    }
                )
            }
        }
        item {
            Text(
                text = stringResource(id = R.string.label_payment_modes),
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold
                ),
            )
        }
        item {
            Card(
                shape = RoundedCornerShape(size = 8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = LocalCustomColors.current.cardBg,
                    contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(all = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(space = 8.dp)
                ) {
                    Text(
                        text = stringResource(id = R.string.label_expense),
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.SemiBold
                        ),
                    )

                    Column(
                        verticalArrangement = Arrangement.spacedBy(space = 16.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 4.dp, bottom = 4.dp, end = 4.dp, start = 20.dp)
                    ) {
                        transactionCategoryExpenseSummary.forEach { item ->
                            StatsRow(
                                statTitle = item.transactionCategory.getTransactionCategoryAsString(
                                    context = LocalContext.current
                                ),
                                statAmount = stringResource(
                                    id = R.string.label_rupee_sign_with_value,
                                    item.totalAmount.toIndianCurrencyFormat()
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(height = 16.dp))
                    Text(
                        text = stringResource(id = R.string.label_income),
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.SemiBold
                        ),
                    )

                    Column(
                        verticalArrangement = Arrangement.spacedBy(space = 16.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 4.dp, bottom = 4.dp, end = 4.dp, start = 20.dp)
                    ) {
                        transactionCategoryIncomeSummary.forEach { item ->
                            StatsRow(
                                statTitle = item.transactionCategory.getTransactionCategoryAsString(
                                    context = LocalContext.current
                                ),
                                statAmount = stringResource(
                                    id = R.string.label_rupee_sign_with_value,
                                    item.totalAmount.toIndianCurrencyFormat()
                                )
                            )
                        }
                    }
                }
            }
        }
        item {
            Text(
                text = stringResource(id = R.string.label_stats),
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold
                ),
            )
        }
        item {
            Card(
                shape = RoundedCornerShape(size = 8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = LocalCustomColors.current.cardBg,
                    contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(all = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(space = 8.dp)
                ) {
                    StatsRow(
                        statTitle = stringResource(id = R.string.label_number_of_transactions),
                        statAmount = stats.totalTransactions.toString(),
                        titleStyle = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.SemiBold
                        ),
                        amountStyle = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                    Spacer(modifier = Modifier.height(height = 16.dp))
                    StatsRow(
                        statTitle = stringResource(id = R.string.label_total_expense),
                        statAmount = stringResource(
                            id = R.string.label_rupee_sign_with_value,
                            stats.totalExpense.toIndianCurrencyFormat()
                        ),
                        titleStyle = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.SemiBold
                        ),
                        amountStyle = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                    Column(
                        verticalArrangement = Arrangement.spacedBy(space = 16.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 4.dp, bottom = 4.dp, end = 4.dp, start = 20.dp)
                    ) {
                        StatsRow(
                            statTitle = stringResource(id = R.string.label_average_expense_per_day),
                            statAmount = stringResource(
                                id = R.string.label_rupee_sign_with_value,
                                stats.avgExpensePerDay.toIndianCurrencyFormat()
                            )
                        )
                        StatsRow(
                            statTitle = stringResource(id = R.string.label_average_expense_per_transaction),
                            statAmount = stringResource(
                                id = R.string.label_rupee_sign_with_value,
                                stats.avgExpensePerTransaction.toIndianCurrencyFormat()
                            )
                        )
                        StatsRow(
                            statTitle = stringResource(id = R.string.label_max_expense),
                            statAmount = stringResource(
                                id = R.string.label_rupee_sign_with_value,
                                stats.maxExpense.toIndianCurrencyFormat()
                            )
                        )
                    }
                    Spacer(modifier = Modifier.height(height = 16.dp))
                    StatsRow(
                        statTitle = stringResource(id = R.string.label_total_income),
                        statAmount = stringResource(
                            id = R.string.label_rupee_sign_with_value,
                            stats.totalIncome.toIndianCurrencyFormat()
                        ),
                        titleStyle = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.SemiBold
                        ),
                        amountStyle = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                    Column(
                        verticalArrangement = Arrangement.spacedBy(space = 16.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 4.dp, bottom = 4.dp, end = 4.dp, start = 20.dp)
                    ) {
                        StatsRow(
                            statTitle = stringResource(id = R.string.label_average_income_per_day),
                            statAmount = stringResource(
                                id = R.string.label_rupee_sign_with_value,
                                stats.avgIncomePerDay.toIndianCurrencyFormat()
                            )
                        )
                        StatsRow(
                            statTitle = stringResource(id = R.string.label_average_income_per_transaction),
                            statAmount = stringResource(
                                id = R.string.label_rupee_sign_with_value,
                                stats.avgIncomePerTransaction.toIndianCurrencyFormat()
                            )
                        )
                        StatsRow(
                            statTitle = stringResource(id = R.string.label_max_income),
                            statAmount = stringResource(
                                id = R.string.label_rupee_sign_with_value,
                                stats.maxIncome.toIndianCurrencyFormat()
                            )
                        )
                    }
                }
            }
        }
    }
}

@Composable
@Preview
fun PreviewAnalyticsScreen() {
    DailyExpenseTheme(modifier = Modifier.wrapContentSize()) {
        AnalyticsScreenContent(
            durationState = Duration.ThisWeek,
            onDurationChanged = { },
            durationChipData = fakeDurationAnalyticsChipData,
            startDate = getThisWeekRange().first,
            endDate = getThisWeekRange().second,
            onRangeChanged = { _, _ -> },
            totalExpense = "10,000",
            totalIncome = "10,000",
            categoryExpenseSummary = emptyList(),
            categoryIncomeSummary = emptyList(),
            transactionCategoryExpenseSummary = emptyList(),
            transactionCategoryIncomeSummary = emptyList(),
            stats = TransactionStats(),
            bottomSheetController = rememberCustomBottomSheetController(),
        )
    }
}
