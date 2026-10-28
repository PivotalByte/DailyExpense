package com.dailyexpense.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.dailyexpense.R
import com.dailyexpense.data.enums.TransactionCategory
import com.dailyexpense.data.enums.TransactionType
import com.dailyexpense.data.enums.getTransactionCategoryAsString
import com.dailyexpense.data.room.entity.AccountEntity
import com.dailyexpense.data.room.entity.CategoryEntity
import com.dailyexpense.data.room.entity.TagEntity
import com.dailyexpense.data.room.entity.TransactionEntity
import com.dailyexpense.data.room.wrapper.TransactionFull
import com.dailyexpense.navigation.Route
import com.dailyexpense.presentation.TransactionDetailViewModel
import com.dailyexpense.presentation.TransactionDetailViewModel.UiState
import com.dailyexpense.ui.bottomsheet.BottomSheetHost
import com.dailyexpense.ui.bottomsheet.factory.rememberCustomBottomSheetController
import com.dailyexpense.ui.bottomsheet.sheets.ConfirmDeleteBottomSheet
import com.dailyexpense.ui.components.CustomToolbar
import com.dailyexpense.ui.components.categoryColor
import com.dailyexpense.ui.components.categoryIconRes
import com.dailyexpense.ui.theme.DailyExpenseTheme
import com.dailyexpense.ui.theme.ExpenseColor
import com.dailyexpense.ui.theme.IncomeColor
import com.dailyexpense.ui.theme.LocalCustomColors
import com.dailyexpense.ui.theme.NeutralColor
import com.dailyexpense.utils.DateUtil.toOrdinalAnnotatedDate
import java.util.Calendar
import java.util.Date

@Composable
fun TransactionDetailScreen(
    navController: NavHostController,
    viewModel: TransactionDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val bottomSheetController = rememberCustomBottomSheetController()

    LaunchedEffect(uiState) {
        if (uiState is UiState.Deleted) navController.popBackStack()
    }

    BottomSheetHost(controller = bottomSheetController) {
        val deleteTitle = stringResource(id = R.string.title_delete_transaction)
        val deleteMessage = stringResource(id = R.string.message_delete_transaction)
        TransactionDetailScreenContent(
            uiState = uiState,
            onBackClick = { navController.popBackStack() },
            onEditClick = { transactionId ->
                navController.navigate(Route.NewTransaction.createRoute(transactionId))
            },
            onDeleteClick = {
                bottomSheetController.show {
                    ConfirmDeleteBottomSheet(
                        title = deleteTitle,
                        message = deleteMessage,
                        onConfirm = viewModel::deleteTransaction,
                        closeSheet = { bottomSheetController.hide() }
                    )
                }
            }
        )
    }
}

@Composable
fun TransactionDetailScreenContent(
    uiState: UiState,
    onBackClick: () -> Unit,
    onEditClick: (Int) -> Unit,
    onDeleteClick: () -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }
    val loaded = uiState as? UiState.Loaded

    Column(modifier = Modifier.fillMaxSize()) {
        CustomToolbar(
            title = stringResource(id = R.string.title_transaction_detail),
            showBack = true,
            onBack = onBackClick,
            actions = {
                if (loaded != null) {
                    IconButton(onClick = { showMenu = true }) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "More options",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text(stringResource(id = R.string.label_edit)) },
                            onClick = {
                                showMenu = false
                                onEditClick(loaded.transaction.transaction.transactionId)
                            },
                            leadingIcon = { Icon(imageVector = Icons.Default.Edit, contentDescription = null) }
                        )
                        DropdownMenuItem(
                            text = { Text(stringResource(id = R.string.label_delete)) },
                            onClick = {
                                showMenu = false
                                onDeleteClick()
                            },
                            leadingIcon = { Icon(imageVector = Icons.Default.Delete, contentDescription = null) }
                        )
                    }
                }
            }
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(LocalCustomColors.current.appBg)
        ) {
            when (uiState) {
                is UiState.Loaded -> TransactionDetailBody(full = uiState.transaction)
                UiState.NotFound -> Text(
                    text = stringResource(id = R.string.message_transaction_not_found),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.align(Alignment.Center)
                )
                UiState.Loading, UiState.Deleted -> Unit
            }
        }
    }
}

@Composable
private fun TransactionDetailBody(full: TransactionFull) {
    val transaction = full.transaction
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item { TransactionSummaryCard(transaction = transaction) }
        item {
            DetailCard {
                full.category?.let { category ->
                    DetailRow(label = stringResource(id = R.string.label_category)) {
                        CategoryValue(category = category)
                    }
                    HorizontalDivider(color = LocalCustomColors.current.searchBoxBorder)
                }
                full.account?.let { account ->
                    DetailRow(label = stringResource(id = R.string.label_account)) {
                        Column {
                            DetailValue(text = AnnotatedString(account.accountBankName))
                            Text(
                                text = account.accountHolderName,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    HorizontalDivider(color = LocalCustomColors.current.searchBoxBorder)
                }
                DetailRow(label = stringResource(id = R.string.label_payment_mode)) {
                    DetailValue(
                        text = AnnotatedString(
                            transaction.transactionCategory.getTransactionCategoryAsString(LocalContext.current)
                        )
                    )
                }
                HorizontalDivider(color = LocalCustomColors.current.searchBoxBorder)
                DetailRow(label = stringResource(id = R.string.label_date_time)) {
                    val calendar = Calendar.getInstance().apply { time = transaction.date }
                    DetailValue(
                        text = AnnotatedString.Builder().apply {
                            append(transaction.date.toOrdinalAnnotatedDate())
                            append(", ")
                            append(
                                formatTimeTo12Hour(
                                    hour = calendar.get(Calendar.HOUR_OF_DAY),
                                    minute = calendar.get(Calendar.MINUTE)
                                )
                            )
                        }.toAnnotatedString()
                    )
                }
            }
        }
        if (full.tags.isNotEmpty()) {
            item {
                DetailCard {
                    DetailRow(label = stringResource(id = R.string.label_tags)) {
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            full.tags.forEach { tag -> TagChip(tag = tag) }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TransactionSummaryCard(transaction: TransactionEntity) {
    val (iconRes, color, amountRes) = when (transaction.transactionType) {
        TransactionType.INCOME -> Triple(R.drawable.ic_income, IncomeColor, R.string.rupee_sign_income)
        TransactionType.EXPENSE -> Triple(R.drawable.ic_expense, ExpenseColor, R.string.rupee_sign_expense)
        TransactionType.NEUTRAL -> Triple(R.drawable.ic_neutral, NeutralColor, R.string.rupee_sign_neutral)
    }
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = LocalCustomColors.current.cardBg,
            contentColor = MaterialTheme.colorScheme.onSurfaceVariant
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 24.dp, horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .background(color = color.copy(alpha = 0.25f), shape = RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = iconRes),
                    contentDescription = null,
                    modifier = Modifier.size(36.dp)
                )
            }
            Text(
                text = stringResource(id = amountRes, transaction.amount),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.SemiBold,
                color = color
            )
            Text(
                text = transaction.title,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
private fun DetailCard(content: @Composable () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = LocalCustomColors.current.cardBg),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(horizontal = 14.dp, vertical = 4.dp)) {
            content()
        }
    }
}

@Composable
private fun DetailRow(label: String, value: @Composable () -> Unit) {
    Column(modifier = Modifier.padding(vertical = 10.dp)) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 4.dp)
        )
        value()
    }
}

@Composable
private fun DetailValue(text: AnnotatedString) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleMedium,
        color = MaterialTheme.colorScheme.onSurface
    )
}

@Composable
private fun CategoryValue(category: CategoryEntity) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(
                    categoryColor(category.colorCode, fallback = MaterialTheme.colorScheme.primary),
                    RoundedCornerShape(8.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = categoryIconRes(category.iconResName)),
                contentDescription = null,
                modifier = Modifier.size(24.dp)
            )
        }
        DetailValue(text = AnnotatedString(category.name))
    }
}

@Preview
@Composable
fun PreviewTransactionDetail() {
    DailyExpenseTheme {
        TransactionDetailScreenContent(
            uiState = UiState.Loaded(
                TransactionFull(
                    transaction = TransactionEntity(
                        title = "Weekly groceries",
                        categoryId = 1,
                        accountId = 1,
                        date = Date(),
                        transactionType = TransactionType.EXPENSE,
                        transactionCategory = TransactionCategory.UPI,
                        amount = 1250.0
                    ),
                    category = CategoryEntity(
                        name = "Groceries",
                        transactionType = TransactionType.EXPENSE,
                        colorCode = "#4CAF50",
                        iconResName = "ic_category_groceries"
                    ),
                    account = AccountEntity(
                        accountId = 1,
                        accountHolderName = "Holder",
                        accountBankName = "Axis Bank",
                        accountNumber = "1234",
                        openingBalance = 0.0
                    ),
                    tags = listOf(TagEntity(tagId = 1, name = "Family"))
                )
            ),
            onBackClick = {},
            onEditClick = {},
            onDeleteClick = {}
        )
    }
}
