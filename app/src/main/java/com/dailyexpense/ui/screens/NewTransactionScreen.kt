package com.dailyexpense.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.ui.Alignment
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.dailyexpense.R
import com.dailyexpense.data.enums.TransactionCategory
import com.dailyexpense.data.enums.TransactionType
import com.dailyexpense.data.fakeTransactionTypeChipDataNewTransaction
import com.dailyexpense.data.models.TransactionTypeChipData
import com.dailyexpense.data.room.entity.AccountEntity
import com.dailyexpense.data.room.entity.CategoryEntity
import com.dailyexpense.data.room.entity.TagEntity
import com.dailyexpense.presentation.NewTransactionViewModel
import com.dailyexpense.ui.bottomsheet.BottomSheetHost
import com.dailyexpense.ui.bottomsheet.factory.rememberCustomBottomSheetController
import com.dailyexpense.ui.bottomsheet.sheets.AccountSelectionBottomSheet
import com.dailyexpense.ui.bottomsheet.sheets.CategorySelectionBottomSheet
import com.dailyexpense.ui.bottomsheet.sheets.DateSelectionBottomSheet
import com.dailyexpense.ui.bottomsheet.sheets.TimeSelectionBottomSheet
import com.dailyexpense.ui.components.BannerAdView
import com.dailyexpense.ui.components.CustomTextField
import com.dailyexpense.ui.components.CustomToolbar
import com.dailyexpense.ui.components.TextChip
import com.dailyexpense.ui.theme.DailyExpenseTheme
import com.dailyexpense.ui.theme.LocalCustomColors
import androidx.core.graphics.toColorInt
import com.dailyexpense.ui.actions.NewTransactionActions
import com.dailyexpense.ui.states.NewTransactionState
import com.dailyexpense.utils.DateUtil.toOrdinalAnnotatedDate
import java.util.Date
import java.util.Locale

@Composable
fun NewTransactionScreen(
    navController: NavHostController,
    newTransactionViewModel: NewTransactionViewModel = hiltViewModel()
) {
    val transactionTypeState by newTransactionViewModel.transactionTypeState.collectAsState()
    val amountState by newTransactionViewModel.amountState.collectAsState()
    val selectedCategory by newTransactionViewModel.selectedCategoryState.collectAsState()
    val selectedAccount by newTransactionViewModel.selectedAccountState.collectAsState()
    val selectedPaymentMethod by newTransactionViewModel.selectedPaymentMethodState.collectAsState()
    val notes by newTransactionViewModel.notesState.collectAsState()
    val selectedDate by newTransactionViewModel.dateState.collectAsState()
    val selectedHour by newTransactionViewModel.hourState.collectAsState()
    val selectedMinute by newTransactionViewModel.minuteState.collectAsState()
    val categories by newTransactionViewModel.categoriesState.collectAsState()
    val accounts by newTransactionViewModel.accountsState.collectAsState()
    val saveResult by newTransactionViewModel.saveResultState.collectAsState()
    val selectedTags by newTransactionViewModel.selectedTagsState.collectAsState()
    val tagSearchQuery by newTransactionViewModel.tagSearchQueryState.collectAsState()
    val suggestedTags by newTransactionViewModel.suggestedTagsState.collectAsState()

    val bottomSheetController = rememberCustomBottomSheetController()

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

    LaunchedEffect(saveResult) {
        when (saveResult) {
            is NewTransactionViewModel.SaveResult.Success -> {
                navController.popBackStack()
            }
            is NewTransactionViewModel.SaveResult.Error -> {
                newTransactionViewModel.clearSaveResult()
            }
            null -> {}
        }
    }

    BottomSheetHost(controller = bottomSheetController) {
        val state = NewTransactionState(
            transactionType = transactionTypeState,
            amount = amountState,
            selectedCategory = selectedCategory,
            selectedAccount = selectedAccount,
            selectedPaymentMethod = selectedPaymentMethod,
            notes = notes,
            selectedDate = selectedDate,
            selectedHour = selectedHour,
            selectedMinute = selectedMinute,
            transactionTypeChipData = transactionTypeChipData,
            selectedTags = selectedTags,
            tagSearchQuery = tagSearchQuery,
            suggestedTags = suggestedTags
        )

        val actions = NewTransactionActions(
            onTransactionTypeChanged = { newType ->
                newTransactionViewModel.updateTransactionType(newType)
            },
            onAmountChanged = { newAmount ->
                newTransactionViewModel.updateAmount(newAmount)
            },
            onCategoryFieldClick = {
                bottomSheetController.show {
                    CategorySelectionBottomSheet(
                        categories = categories,
                        selectedCategory = selectedCategory,
                        onCategorySelected = { category ->
                            newTransactionViewModel.updateSelectedCategory(category)
                        },
                        closeSheet = { bottomSheetController.hide() }
                    )
                }
            },
            onAccountFieldClick = {
                bottomSheetController.show {
                    AccountSelectionBottomSheet(
                        accounts = accounts,
                        selectedAccount = selectedAccount,
                        onAccountSelected = { account ->
                            newTransactionViewModel.updateSelectedAccount(account)
                        },
                        closeSheet = { bottomSheetController.hide() }
                    )
                }
            },
            onPaymentMethodChanged = { method ->
                newTransactionViewModel.updatePaymentMethod(method)
            },
            onNotesChanged = { newNotes ->
                newTransactionViewModel.updateNotes(newNotes)
            },
            onDateFieldClick = {
                bottomSheetController.show {
                    DateSelectionBottomSheet(
                        selectedDate = selectedDate,
                        onDateSelected = { date ->
                            newTransactionViewModel.updateDate(date)
                        },
                        closeSheet = { bottomSheetController.hide() }
                    )
                }
            },
            onTimeFieldClick = {
                bottomSheetController.show {
                    TimeSelectionBottomSheet(
                        selectedHour = selectedHour,
                        selectedMinute = selectedMinute,
                        onTimeSelected = { hour, minute ->
                            newTransactionViewModel.updateTime(hour, minute)
                        },
                        closeSheet = { bottomSheetController.hide() }
                    )
                }
            },
            onTagSearchChanged = { query ->
                newTransactionViewModel.updateTagSearchQuery(query)
            },
            onTagSelected = { tag ->
                newTransactionViewModel.addTag(tag)
            },
            onTagRemoved = { tag ->
                newTransactionViewModel.removeTag(tag)
            },
            onCreateTag = { tagName ->
                newTransactionViewModel.createAndAddTag(tagName)
            },
            onSaveClick = {
                newTransactionViewModel.saveTransaction {}
            }
        )

        NewTransactionScreenContent(
            navController = navController,
            state = state,
            actions = actions
        )
    }
}

@Composable
fun NewTransactionScreenContent(
    navController: NavHostController,
    state: NewTransactionState,
    actions: NewTransactionActions
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Bottom
    ) {
        CustomToolbar(
            title = "New Transaction",
            showBack = true,
            onBack = { navController.popBackStack() }
        )
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .imePadding(),
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
                    state.transactionTypeChipData.forEach { chip ->
                        Box(
                            modifier = Modifier.weight(weight = 1f)
                        ) {
                            TextChip(
                                modifier = Modifier.fillMaxWidth(),
                                text = chip.label,
                                isSelected = state.transactionType == chip.type,
                                onSelected = {
                                    actions.onTransactionTypeChanged(chip.type)
                                },
                            )
                        }
                    }
                }
            }
            item {
                CustomTextField(
                    value = state.amount,
                    onValueChange = actions.onAmountChanged,
                    label = "Amount",
                    prefix = "₹",
                    placeholder = "0.00",
                    keyboardType = KeyboardType.Decimal,
                    validation = { newValue ->
                        newValue.isEmpty() || newValue.matches(Regex("^\\d*\\.?\\d{0,2}$"))
                    }
                )
            }
            item {
                CustomTextField(
                    value = state.notes,
                    onValueChange = actions.onNotesChanged,
                    label = "Notes",
                    placeholder = "Enter notes",
                    capitalization = KeyboardCapitalization.Sentences
                )
            }
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(space = 8.dp)
                ) {
                    DateSelectionField(
                        selectedDate = state.selectedDate,
                        onClick = actions.onDateFieldClick,
                        modifier = Modifier.weight(1f)
                    )
                    TimeSelectionField(
                        selectedHour = state.selectedHour,
                        selectedMinute = state.selectedMinute,
                        onClick = actions.onTimeFieldClick,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
            item {
                CategorySelectionField(
                    selectedCategory = state.selectedCategory,
                    onClick = actions.onCategoryFieldClick
                )
            }
            item {
                AccountSelectionField(
                    selectedAccount = state.selectedAccount,
                    onClick = actions.onAccountFieldClick
                )
            }
            item {
                TagSelectionSection(
                    selectedTags = state.selectedTags,
                    searchQuery = state.tagSearchQuery,
                    suggestedTags = state.suggestedTags,
                    onSearchQueryChanged = actions.onTagSearchChanged,
                    onTagSelected = actions.onTagSelected,
                    onTagRemoved = actions.onTagRemoved,
                    onCreateTag = actions.onCreateTag
                )
            }
            item {
                PaymentMethodSection(
                    transactionType = state.transactionType,
                    selectedPaymentMethod = state.selectedPaymentMethod,
                    onPaymentMethodChanged = actions.onPaymentMethodChanged
                )
            }
            item {
                Button(
                    onClick = actions.onSaveClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = LocalCustomColors.current.primaryColor
                    ),
                    shape = RoundedCornerShape(12.dp),
                    enabled = state.amount.isNotEmpty() &&
                        state.notes.isNotEmpty() &&
                        state.selectedCategory != null &&
                        state.selectedAccount != null &&
                        state.selectedPaymentMethod != null
                ) {
                    Text(
                        text = "Save Transaction",
                        style = MaterialTheme.typography.labelLarge,
                        color = Color.White
                    )
                }
            }
        }
        BannerAdView(adUnitId = stringResource(id = R.string.ad_fixed_size_banner))
    }
}

@Composable
fun DateSelectionField(
    selectedDate: Long,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .border(
                width = 1.dp,
                color = LocalCustomColors.current.searchBoxBorder,
                shape = RoundedCornerShape(12.dp)
            )
            .clip(RoundedCornerShape(12.dp))
            .background(
                color = LocalCustomColors.current.cardBg,
                shape = RoundedCornerShape(12.dp)
            )
            .clickable { onClick() }
            .padding(horizontal = 14.dp, vertical = 10.dp)
    ) {
        Text(
            text = "Date",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 4.dp)
        )

        Text(
            text = Date(selectedDate).toOrdinalAnnotatedDate(),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
fun TimeSelectionField(
    selectedHour: Int,
    selectedMinute: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .border(
                width = 1.dp,
                color = LocalCustomColors.current.searchBoxBorder,
                shape = RoundedCornerShape(12.dp)
            )
            .clip(RoundedCornerShape(12.dp))
            .background(
                color = LocalCustomColors.current.cardBg,
                shape = RoundedCornerShape(12.dp)
            )
            .clickable { onClick() }
            .padding(horizontal = 14.dp, vertical = 10.dp)
    ) {
        Text(
            text = "Time",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 4.dp)
        )

        Text(
            text = formatTimeTo12Hour(selectedHour, selectedMinute),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
fun AccountSelectionField(
    selectedAccount: AccountEntity?,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = 1.dp,
                color = LocalCustomColors.current.searchBoxBorder,
                shape = RoundedCornerShape(12.dp)
            )
            .clip(RoundedCornerShape(12.dp))
            .background(
                color = LocalCustomColors.current.cardBg,
                shape = RoundedCornerShape(12.dp)
            )
            .clickable { onClick() }
            .padding(horizontal = 14.dp, vertical = 10.dp)
    ) {
        Text(
            text = "Account",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 4.dp)
        )

        if (selectedAccount != null) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    Text(
                        text = selectedAccount.accountBankName,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = selectedAccount.accountHolderName,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Text(
                    text = "₹${String.format(java.util.Locale.US, "%.2f", selectedAccount.balance)}",
                    style = MaterialTheme.typography.titleSmall,
                    color = LocalCustomColors.current.primaryColor
                )
            }
        } else {
            Text(
                text = "Select Account",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
            )
        }
    }
}

@Composable
fun CategorySelectionField(
    selectedCategory: CategoryEntity?,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = 1.dp,
                color = LocalCustomColors.current.searchBoxBorder,
                shape = RoundedCornerShape(12.dp)
            )
            .clip(RoundedCornerShape(12.dp))
            .background(
                color = LocalCustomColors.current.cardBg,
                shape = RoundedCornerShape(12.dp)
            )
            .clickable { onClick() }
            .padding(horizontal = 14.dp, vertical = 10.dp)
    ) {
        Text(
            text = "Category",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 4.dp)
        )

        if (selectedCategory != null) {
            val iconResId = when (selectedCategory.iconResName) {
                "ic_category_groceries" -> R.drawable.ic_category_groceries
                "ic_category_medical" -> R.drawable.ic_category_medical
                "ic_category_fuel_transport" -> R.drawable.ic_category_fuel_transport
                "ic_category_restaurant" -> R.drawable.ic_category_restaurant
                "ic_category_salary" -> R.drawable.ic_category_salary
                "ic_category_business" -> R.drawable.ic_category_business
                "ic_category_investment" -> R.drawable.ic_category_investment
                else -> R.drawable.ic_category_more
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(
                            try {
                                Color(selectedCategory.colorCode.toColorInt())
                            } catch (_: IllegalArgumentException) {
                                MaterialTheme.colorScheme.primary
                            },
                            RoundedCornerShape(8.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    androidx.compose.foundation.Image(
                        painter = painterResource(id = iconResId),
                        contentDescription = selectedCategory.name,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Text(
                    text = selectedCategory.name,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        } else {
            Text(
                text = "Select Category",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
            )
        }
    }
}

@Composable
fun PaymentMethodSection(
    transactionType: TransactionType,
    selectedPaymentMethod: TransactionCategory?,
    onPaymentMethodChanged: (TransactionCategory) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = "Payment Method",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        val paymentMethods = getPaymentMethodsForType(transactionType)

        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            paymentMethods.forEach { method ->
                TextChip(
                    text = method.name.replace("_", " ").lowercase()
                        .split(" ")
                        .joinToString(" ") { it.replaceFirstChar { char -> char.uppercase() } },
                    isSelected = selectedPaymentMethod == method,
                    onSelected = { onPaymentMethodChanged(method) }
                )
            }
        }
    }
}

fun getPaymentMethodsForType(type: TransactionType): List<TransactionCategory> {
    return when (type) {
        TransactionType.INCOME, TransactionType.EXPENSE -> listOf(
            TransactionCategory.CASH,
            TransactionCategory.DEBIT_CARD,
            TransactionCategory.UPI,
            TransactionCategory.BANK_TRANSFER
        )
        TransactionType.NEUTRAL -> listOf(
            TransactionCategory.CASH_WITHDRAWAL,
            TransactionCategory.CREDIT_CARD
        )
    }
}

@Composable
fun TagSelectionSection(
    selectedTags: List<TagEntity>,
    searchQuery: String,
    suggestedTags: List<TagEntity>,
    onSearchQueryChanged: (String) -> Unit,
    onTagSelected: (TagEntity) -> Unit,
    onTagRemoved: (TagEntity) -> Unit,
    onCreateTag: (String) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        CustomTextField(
            value = searchQuery,
            onValueChange = onSearchQueryChanged,
            label = "Tags (Optional)",
            placeholder = "Search or create tags",
            capitalization = KeyboardCapitalization.Words,
            trailingIcon = {
                if (searchQuery.isNotBlank()) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Create",
                            style = MaterialTheme.typography.bodySmall,
                            color = LocalCustomColors.current.primaryColor,
                            modifier = Modifier.clickable {
                                onCreateTag(searchQuery)
                            }
                        )
                    }
                }
            }
        )

        if (selectedTags.isNotEmpty()) {
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                selectedTags.forEach { tag ->
                    TagChip(
                        tag = tag,
                        onRemove = { onTagRemoved(tag) }
                    )
                }
            }
        }

        if (searchQuery.isNotBlank() && suggestedTags.isNotEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = LocalCustomColors.current.cardBg,
                        shape = RoundedCornerShape(12.dp)
                    )
                    .border(
                        width = 1.dp,
                        color = LocalCustomColors.current.searchBoxBorder,
                        shape = RoundedCornerShape(12.dp)
                    )
            ) {
                suggestedTags
                    .filter { tag -> !selectedTags.contains(tag) }
                    .take(3)
                    .forEach { tag ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onTagSelected(tag) }
                                .padding(horizontal = 14.dp, vertical = 12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = tag.name,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
            }
        }
    }
}

@Composable
fun TagChip(
    tag: TagEntity,
    onRemove: () -> Unit
) {
    Row(
        modifier = Modifier
            .background(
                color = LocalCustomColors.current.primaryColor.copy(alpha = 0.1f),
                shape = RoundedCornerShape(16.dp)
            )
            .border(
                width = 1.dp,
                color = LocalCustomColors.current.primaryColor.copy(alpha = 0.3f),
                shape = RoundedCornerShape(16.dp)
            )
            .padding(horizontal = 12.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = tag.name,
            style = MaterialTheme.typography.bodySmall,
            color = LocalCustomColors.current.primaryColor
        )
        Box(
            modifier = Modifier
                .size(16.dp)
                .background(
                    color = LocalCustomColors.current.primaryColor.copy(alpha = 0.2f),
                    shape = CircleShape
                )
                .clickable { onRemove() },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Remove tag",
                tint = LocalCustomColors.current.primaryColor,
                modifier = Modifier.size(12.dp)
            )
        }
    }
}

fun formatTimeTo12Hour(hour: Int, minute: Int): String {
    val period = if (hour < 12) "AM" else "PM"
    val displayHour = when (hour) {
        0 -> 12
        in 1..12 -> hour
        else -> hour - 12
    }
    return String.format(Locale.US, "%02d:%02d %s", displayHour, minute, period)
}

@Preview
@Composable
fun PreviewNewTransaction() {
    val navController = rememberNavController()
    DailyExpenseTheme {
        val calendar = java.util.Calendar.getInstance()
        val state = NewTransactionState(
            transactionType = TransactionType.EXPENSE,
            amount = "",
            selectedCategory = null,
            selectedAccount = null,
            selectedPaymentMethod = null,
            notes = "",
            selectedDate = System.currentTimeMillis(),
            selectedHour = calendar.get(java.util.Calendar.HOUR_OF_DAY),
            selectedMinute = calendar.get(java.util.Calendar.MINUTE),
            transactionTypeChipData = fakeTransactionTypeChipDataNewTransaction,
            selectedTags = emptyList(),
            tagSearchQuery = "",
            suggestedTags = emptyList()
        )
        val actions = NewTransactionActions()
        NewTransactionScreenContent(
            navController = navController,
            state = state,
            actions = actions
        )
    }
}
