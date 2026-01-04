package com.dailyexpense.ui.bottomsheet.sheets

import android.content.res.Configuration
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.dailyexpense.R
import com.dailyexpense.data.enums.TransactionCategory
import com.dailyexpense.data.enums.TransactionType
import com.dailyexpense.data.fakeCategoryChipData
import com.dailyexpense.data.fakeDurationChipData
import com.dailyexpense.data.fakeTagChipDataList
import com.dailyexpense.data.fakeTransactionCategoryChipData
import com.dailyexpense.data.fakeTransactionTypeChipData
import com.dailyexpense.data.models.ChipData
import com.dailyexpense.data.models.Duration
import com.dailyexpense.data.models.FilterState
import com.dailyexpense.data.models.TransactionCategoryChipData
import com.dailyexpense.data.models.TransactionTypeChipData
import com.dailyexpense.data.models.isSameDay
import com.dailyexpense.presentation.FilterViewModel
import com.dailyexpense.ui.components.CheckboxChip
import com.dailyexpense.ui.components.TextChip
import com.dailyexpense.ui.components.calendar.CalendarTitle
import com.dailyexpense.ui.components.calendar.Day
import com.dailyexpense.ui.components.calendar.MonthHeader
import com.dailyexpense.ui.theme.DailyExpenseTheme
import com.dailyexpense.ui.theme.LocalCustomColors
import com.dailyexpense.utils.DateUtil.calculateDateRange
import com.dailyexpense.utils.DateUtil.endOfDayOrTodayMillis
import com.dailyexpense.utils.DateUtil.startOfDayOrTodayMillis
import com.dailyexpense.utils.DateUtil.toLocalDate
import com.dailyexpense.utils.extensions.ContinuousSelectionHelper.getSelection
import com.dailyexpense.utils.extensions.DateSelection
import com.dailyexpense.utils.extensions.toReadableFormat
import com.dailyexpense.utils.rememberFirstMostVisibleMonth
import com.kizitonwose.calendar.compose.HorizontalCalendar
import com.kizitonwose.calendar.compose.rememberCalendarState
import com.kizitonwose.calendar.core.daysOfWeek
import com.kizitonwose.calendar.core.minusDays
import com.kizitonwose.calendar.core.minusMonths
import com.kizitonwose.calendar.core.now
import com.kizitonwose.calendar.core.plusMonths
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate
import kotlinx.datetime.YearMonth
import kotlinx.datetime.minusMonth
import kotlinx.datetime.plusMonth
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterBottomSheet(
    closeSheet: () -> Unit,
    viewModel: FilterViewModel = hiltViewModel()
) {
    val filterState by viewModel.filterState.collectAsState()

    val durationChipData = listOf(
        Duration.Today to stringResource(R.string.label_today),
        Duration.ThisWeek to stringResource(R.string.label_this_week),
        Duration.ThisMonth to stringResource(R.string.label_this_month),
        Duration.ThisYear to stringResource(R.string.label_this_year),
        Duration.Custom to stringResource(R.string.label_custom),
    )

    val categoryChipData =
        viewModel.categories
            .collectAsState(initial = emptyList())
            .value
            .mapIndexed { index, category ->
                ChipData(id = category.categoryId, label = category.name)
            }

    val transactionTypeChipData = listOf(
        TransactionTypeChipData(
            type = TransactionType.EXPENSE,
            label = stringResource(id = R.string.label_expense)
        ),
        TransactionTypeChipData(
            type = TransactionType.INCOME,
            label = stringResource(id = R.string.label_income)
        )
    )

    val transactionCategoryChipData = listOf(
        TransactionCategoryChipData(
            type = TransactionCategory.CASH,
            label = TransactionCategory.CASH.name.toReadableFormat()
        ),
        TransactionCategoryChipData(
            type = TransactionCategory.CASH_WITHDRAWAL,
            label = TransactionCategory.CASH_WITHDRAWAL.name.toReadableFormat()
        ),
        TransactionCategoryChipData(
            type = TransactionCategory.DEBIT_CARD,
            label = TransactionCategory.DEBIT_CARD.name.toReadableFormat()
        ),
        TransactionCategoryChipData(
            type = TransactionCategory.CREDIT_CARD,
            label = TransactionCategory.CREDIT_CARD.name.toReadableFormat()
        ),
        TransactionCategoryChipData(
            type = TransactionCategory.UPI,
            label = TransactionCategory.UPI.name.toReadableFormat().uppercase()
        ),
        TransactionCategoryChipData(
            type = TransactionCategory.BANK_TRANSFER,
            label = TransactionCategory.BANK_TRANSFER.name.toReadableFormat()
        )
    )

    val tagsChipData =
        viewModel.tags
            .collectAsState(initial = emptyList())
            .value
            .mapIndexed { _, tag ->
                ChipData(id = tag.tagId, label = tag.name)
            }

    FilterBottomSheetContent(
        initialFilterState = filterState,
        durationChipData = durationChipData,
        categoryChipData = categoryChipData,
        transactionTypeChipData = transactionTypeChipData,
        transactionCategoryChipData = transactionCategoryChipData,
        tagChipData = tagsChipData,
        onApplyFilter = { newState ->
            viewModel.updateFilter(newState)
            closeSheet.invoke()
        },
        onClearFilter = {
            viewModel.updateFilter(FilterState())
            closeSheet.invoke()
        }
    )
}

@OptIn(ExperimentalTime::class)
@Composable
fun FilterBottomSheetContent(
    initialFilterState: FilterState,
    onApplyFilter: (FilterState) -> Unit,
    onClearFilter: () -> Unit,
    durationChipData: List<Pair<Duration, String>>,
    categoryChipData: List<ChipData>,
    transactionTypeChipData: List<TransactionTypeChipData>,
    transactionCategoryChipData: List<TransactionCategoryChipData>,
    tagChipData: List<ChipData>,
) {
    var localFilterState by remember { mutableStateOf(initialFilterState) }

    val currentMonth = remember {
        localFilterState.startDate?.toLocalDate()?.let { YearMonth(it.year, it.month) }
            ?: YearMonth.now()
    }
    val startMonth = remember { currentMonth.minusMonths(value = 500) }
    val endMonth = remember { currentMonth.plusMonths(value = 500) }
    val today = remember { LocalDate.now() }
    var selection by remember {
        mutableStateOf(
            value = if (localFilterState.selectedDuration == Duration.Custom &&
                localFilterState.startDate != null && localFilterState.endDate != null
            ) {
                DateSelection(
                    startDate = localFilterState.startDate?.toLocalDate()
                        ?: today.minusDays(value = 2),
                    endDate = if (localFilterState.isSameDay()) {
                        null
                    } else {
                        localFilterState.endDate?.toLocalDate() ?: today
                    }

                )
            } else {
                DateSelection(
                    startDate = today.minusDays(value = 2),
                    endDate = today
                )
            }
        )
    }
    val daysOfWeek = remember { daysOfWeek() }
    var showCalendar by remember {
        mutableStateOf(
            value = localFilterState.selectedDuration == Duration.Custom &&
                localFilterState.startDate != null && localFilterState.endDate != null
        )
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(all = 16.dp),
        verticalArrangement = Arrangement.spacedBy(space = 4.dp)
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(weight = 1f),
            verticalArrangement = Arrangement.spacedBy(space = 20.dp)
        ) {
            item {
                Column(verticalArrangement = Arrangement.spacedBy(space = 4.dp)) {
                    Text(
                        text = stringResource(id = R.string.label_by_durations),
                        style = MaterialTheme.typography.bodyLarge.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                    LazyRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(space = 8.dp)
                    ) {
                        items(count = durationChipData.size) { index ->
                            val (duration, label) = durationChipData[index]
                            TextChip(
                                text = label,
                                isSelected = localFilterState.selectedDuration == duration,
                                onSelected = {
                                    localFilterState = localFilterState.copy(selectedDuration = duration)
                                    showCalendar = duration is Duration.Custom
                                }
                            )
                        }
                    }
                    AnimatedVisibility(
                        visible = showCalendar,
                        enter = expandVertically() + fadeIn(),
                        exit = shrinkVertically() + fadeOut()
                    ) {
                        val state = rememberCalendarState(
                            startMonth = startMonth,
                            endMonth = endMonth,
                            firstVisibleMonth = currentMonth,
                            firstDayOfWeek = daysOfWeek.first(),
                        )
                        val coroutineScope = rememberCoroutineScope()
                        val visibleMonth =
                            rememberFirstMostVisibleMonth(state, viewportPercent = 90f)
                        Column(verticalArrangement = Arrangement.spacedBy(space = 4.dp)) {
                            CalendarTitle(
                                modifier = Modifier.padding(vertical = 10.dp),
                                today = today,
                                currentMonth = visibleMonth.yearMonth,
                                goToPrevious = {
                                    coroutineScope.launch {
                                        state.animateScrollToMonth(
                                            month = state.firstVisibleMonth.yearMonth.minusMonth()
                                        )
                                    }
                                },
                                goToNext = {
                                    coroutineScope.launch {
                                        state.animateScrollToMonth(
                                            month = state.firstVisibleMonth.yearMonth.plusMonth()
                                        )
                                    }
                                },
                            )
                            HorizontalCalendar(
                                state = state,
                                dayContent = { day ->
                                    Day(day = day, today = today, selection = selection) { day ->
                                        selection = getSelection(
                                            clickedDate = day.date,
                                            dateSelection = selection,
                                        )
                                    }
                                },
                                monthHeader = {
                                    MonthHeader(daysOfWeek = daysOfWeek)
                                },
                            )
                        }
                    }
                }
            }
            item {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(space = 4.dp)
                ) {
                    Text(
                        text = stringResource(id = R.string.label_by_category),
                        style = MaterialTheme.typography.bodyLarge.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )

                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(space = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(space = 8.dp)
                    ) {
                        categoryChipData.forEach { category ->
                            val isSelected =
                                localFilterState.selectedCategories.contains(category.id)
                            CheckboxChip(
                                text = category.label,
                                isSelected = isSelected,
                                onSelected = {
                                    val newCategories = if (isSelected) {
                                        localFilterState.selectedCategories - category.id
                                    } else {
                                        localFilterState.selectedCategories + category.id
                                    }
                                    localFilterState = localFilterState.copy(selectedCategories = newCategories)
                                }
                            )
                        }
                    }
                }
            }
            item {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(space = 4.dp)
                ) {
                    Text(
                        text = stringResource(id = R.string.label_by_transaction_type),
                        style = MaterialTheme.typography.bodyLarge.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                    LazyRow(
                        modifier = Modifier
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(space = 8.dp)
                    ) {
                        items(count = transactionTypeChipData.size) { index ->
                            val chip = transactionTypeChipData[index]
                            val isSelected =
                                localFilterState.selectedTransactionTypes.contains(chip.type)
                            TextChip(
                                text = transactionTypeChipData[index].label,
                                isSelected = isSelected,
                                onSelected = {
                                    val updatedTypes = if (isSelected) {
                                        localFilterState.selectedTransactionTypes - chip.type
                                    } else {
                                        localFilterState.selectedTransactionTypes + chip.type
                                    }
                                    localFilterState = localFilterState.copy(selectedTransactionTypes = updatedTypes)
                                }
                            )
                        }
                    }
                }
            }
            item {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(space = 4.dp)
                ) {
                    Text(
                        text = stringResource(id = R.string.label_by_payment_type),
                        style = MaterialTheme.typography.bodyLarge.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )

                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(space = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(space = 8.dp)
                    ) {
                        transactionCategoryChipData.forEach { transactionCategory ->
                            val isSelected =
                                localFilterState.selectedTransactionCategories.contains(
                                    transactionCategory.type
                                )
                            CheckboxChip(
                                text = transactionCategory.label,
                                isSelected = isSelected,
                                onSelected = {
                                    val updatedCategories = if (isSelected) {
                                        localFilterState.selectedTransactionCategories - transactionCategory.type
                                    } else {
                                        localFilterState.selectedTransactionCategories + transactionCategory.type
                                    }
                                    localFilterState = localFilterState.copy(
                                        selectedTransactionCategories = updatedCategories
                                    )
                                }
                            )
                        }
                    }
                }
            }
            item {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(space = 4.dp)
                ) {
                    Text(
                        text = stringResource(id = R.string.label_by_tags),
                        style = MaterialTheme.typography.bodyLarge.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )

                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(space = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(space = 8.dp)
                    ) {
                        tagChipData.forEach { tag ->
                            val isSelected = localFilterState.selectedTags.contains(tag.id)
                            CheckboxChip(
                                text = tag.label,
                                isSelected = isSelected,
                                onSelected = {
                                    val newTags = if (isSelected) {
                                        localFilterState.selectedTags - tag.id
                                    } else {
                                        localFilterState.selectedTags + tag.id
                                    }
                                    localFilterState = localFilterState.copy(selectedTags = newTags)
                                }
                            )
                        }
                    }
                }
            }
        }

        Button(
            onClick = {
                val (start, end) = if (localFilterState.selectedDuration == Duration.Custom) {
                    Pair(
                        first = selection.startDate.startOfDayOrTodayMillis(),
                        second = (selection.endDate ?: selection.startDate).endOfDayOrTodayMillis()
                    )
                } else {
                    calculateDateRange(localFilterState.selectedDuration)
                }

                val updatedState = localFilterState.copy(
                    startDate = start,
                    endDate = end
                )

                onApplyFilter(updatedState)
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = LocalCustomColors.current.primaryColor
            ),
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(size = 12.dp)
        ) {
            Text(
                text = stringResource(id = R.string.label_apply_filter),
                style = MaterialTheme.typography.labelLarge.copy(
                    color = Color.White
                )
            )
        }

        Button(
            onClick = {
                onClearFilter.invoke()
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = LocalCustomColors.current.primaryColor
            ),
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(size = 12.dp)
        ) {
            Text(
                text = stringResource(id = R.string.label_clear_filter),
                style = MaterialTheme.typography.labelLarge.copy(
                    color = Color.White
                )
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(name = "Filter Bottom Sheet - Light")
@Composable
fun PreviewFilterLightBottomSheet() {
    val fakeFilterState = remember {
        FilterState(
            selectedDuration = Duration.Custom,
            selectedCategories = setOf(1, 3),
            selectedTransactionTypes = setOf(TransactionType.EXPENSE),
            selectedTransactionCategories = setOf(
                TransactionCategory.UPI,
                TransactionCategory.CREDIT_CARD
            ),
            selectedTags = setOf(2, 5)
        )
    }

    DailyExpenseTheme(modifier = Modifier.wrapContentSize()) {
        FilterBottomSheetContent(
            initialFilterState = fakeFilterState,
            durationChipData = fakeDurationChipData,
            categoryChipData = fakeCategoryChipData,
            transactionTypeChipData = fakeTransactionTypeChipData,
            transactionCategoryChipData = fakeTransactionCategoryChipData,
            tagChipData = fakeTagChipDataList,
            onApplyFilter = {},
            onClearFilter = {}
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(
    name = "Filter Bottom Sheet - Dark",
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    showBackground = true
)
@Composable
fun PreviewFilterDarkBottomSheet() {
    val fakeFilterState = remember {
        FilterState(
            selectedDuration = Duration.Custom,
            selectedCategories = setOf(1, 3),
            selectedTransactionTypes = setOf(TransactionType.EXPENSE),
            selectedTransactionCategories = setOf(
                TransactionCategory.UPI,
                TransactionCategory.CREDIT_CARD
            ),
            selectedTags = setOf(2, 5)
        )
    }

    DailyExpenseTheme(modifier = Modifier.wrapContentSize()) {
        FilterBottomSheetContent(
            initialFilterState = fakeFilterState,
            durationChipData = fakeDurationChipData,
            categoryChipData = fakeCategoryChipData,
            transactionTypeChipData = fakeTransactionTypeChipData,
            transactionCategoryChipData = fakeTransactionCategoryChipData,
            tagChipData = fakeTagChipDataList,
            onApplyFilter = {},
            onClearFilter = {}
        )
    }
}
