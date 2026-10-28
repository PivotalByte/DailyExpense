package com.dailyexpense.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Label
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.dailyexpense.BuildConfig
import com.dailyexpense.R
import com.dailyexpense.data.models.AccountWithBalance
import com.dailyexpense.data.room.entity.AccountEntity
import com.dailyexpense.navigation.Route
import com.dailyexpense.presentation.AccountTabViewModel
import com.dailyexpense.presentation.AccountTabViewModel.Event
import com.dailyexpense.ui.bottomsheet.BottomSheetHost
import com.dailyexpense.ui.bottomsheet.factory.rememberCustomBottomSheetController
import com.dailyexpense.ui.bottomsheet.sheets.ConfirmDeleteBottomSheet
import com.dailyexpense.ui.components.AccountListItem
import com.dailyexpense.ui.components.SectionHeader
import com.dailyexpense.ui.components.SettingsDivider
import com.dailyexpense.ui.components.SettingsGroupCard
import com.dailyexpense.ui.components.SettingsRow
import com.dailyexpense.ui.theme.DailyExpenseTheme
import com.dailyexpense.ui.theme.ExpenseColor
import com.dailyexpense.ui.theme.LocalCustomColors
import com.dailyexpense.utils.shareCsvFile

@Composable
fun AccountTabScreen(
    navController: NavHostController,
    viewModel: AccountTabViewModel = hiltViewModel()
) {
    val accounts by viewModel.accounts.collectAsState()
    val categoryCount by viewModel.categoryCount.collectAsState()
    val tagCount by viewModel.tagCount.collectAsState()
    val isWorking by viewModel.isWorking.collectAsState()
    val event by viewModel.event.collectAsState()
    val bottomSheetController = rememberCustomBottomSheetController()
    val context = LocalContext.current

    val shareTitle = stringResource(id = R.string.label_export_transactions)
    val nothingToExport = stringResource(id = R.string.message_nothing_to_export)
    val exportFailed = stringResource(id = R.string.message_export_failed)
    val dataCleared = stringResource(id = R.string.message_data_cleared)
    LaunchedEffect(event) {
        when (val current = event) {
            is Event.ExportReady -> context.shareCsvFile(current.file, shareTitle)
            Event.NothingToExport -> Toast.makeText(context, nothingToExport, Toast.LENGTH_SHORT).show()
            Event.ExportFailed -> Toast.makeText(context, exportFailed, Toast.LENGTH_SHORT).show()
            Event.DataCleared -> Toast.makeText(context, dataCleared, Toast.LENGTH_SHORT).show()
            null -> return@LaunchedEffect
        }
        viewModel.consumeEvent()
    }

    BottomSheetHost(controller = bottomSheetController) {
        val clearTitle = stringResource(id = R.string.title_clear_all_data)
        val clearMessage = stringResource(id = R.string.message_clear_all_data)
        AccountTabScreenContent(
            accounts = accounts,
            categoryCount = categoryCount,
            tagCount = tagCount,
            isWorking = isWorking,
            onManageAccountsClick = { navController.navigate(Route.AccountList.route) },
            onAccountClick = { account -> navController.navigate(Route.AddNewAccount.createRoute(account.accountId)) },
            onAddAccountClick = { navController.navigate(Route.AddNewAccount.createRoute()) },
            onCategoriesClick = { navController.navigate(Route.CategoryList.route) },
            onTagsClick = { navController.navigate(Route.TagList.route) },
            onExportClick = viewModel::exportTransactions,
            onClearDataClick = {
                bottomSheetController.show {
                    ConfirmDeleteBottomSheet(
                        title = clearTitle,
                        message = clearMessage,
                        onConfirm = viewModel::clearAllData,
                        closeSheet = { bottomSheetController.hide() }
                    )
                }
            }
        )
    }
}

@Composable
fun AccountTabScreenContent(
    accounts: List<AccountWithBalance>,
    categoryCount: Int,
    tagCount: Int,
    isWorking: Boolean,
    onManageAccountsClick: () -> Unit,
    onAccountClick: (AccountEntity) -> Unit,
    onAddAccountClick: () -> Unit,
    onCategoriesClick: () -> Unit,
    onTagsClick: () -> Unit,
    onExportClick: () -> Unit,
    onClearDataClick: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(color = LocalCustomColors.current.appBg),
        verticalArrangement = Arrangement.spacedBy(space = 16.dp),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 80.dp)
    ) {
        item {
            SectionHeader(
                title = stringResource(id = R.string.label_accounts),
                actionLabel = stringResource(id = R.string.label_manage),
                onActionClick = onManageAccountsClick
            )
        }
        if (accounts.isEmpty()) {
            item { NoAccountsCard(onAddAccountClick = onAddAccountClick) }
        } else {
            items(count = accounts.size, key = { index -> accounts[index].account.accountId }) { index ->
                AccountListItem(
                    accountWithBalance = accounts[index],
                    onClick = { onAccountClick(accounts[index].account) }
                )
            }
        }

        item { SectionHeader(title = stringResource(id = R.string.label_manage)) }
        item {
            SettingsGroupCard {
                SettingsRow(
                    icon = Icons.Default.Category,
                    title = stringResource(id = R.string.label_categories),
                    subtitle = pluralStringResource(R.plurals.count_categories, categoryCount, categoryCount),
                    onClick = onCategoriesClick
                )
                SettingsDivider()
                SettingsRow(
                    icon = Icons.AutoMirrored.Filled.Label,
                    title = stringResource(id = R.string.label_tags),
                    subtitle = pluralStringResource(R.plurals.count_tags, tagCount, tagCount),
                    onClick = onTagsClick
                )
            }
        }

        item { SectionHeader(title = stringResource(id = R.string.label_data)) }
        item {
            SettingsGroupCard {
                SettingsRow(
                    icon = Icons.Default.Share,
                    title = stringResource(id = R.string.label_export_transactions),
                    subtitle = stringResource(id = R.string.subtitle_export_transactions),
                    onClick = if (isWorking) null else onExportClick
                )
                SettingsDivider()
                SettingsRow(
                    icon = Icons.Default.DeleteForever,
                    title = stringResource(id = R.string.title_clear_all_data),
                    subtitle = stringResource(id = R.string.subtitle_clear_all_data),
                    tint = ExpenseColor,
                    titleColor = ExpenseColor,
                    onClick = if (isWorking) null else onClearDataClick
                )
            }
        }

        item { SectionHeader(title = stringResource(id = R.string.label_about)) }
        item {
            SettingsGroupCard {
                SettingsRow(
                    icon = Icons.Default.Info,
                    title = stringResource(id = R.string.label_version),
                    subtitle = BuildConfig.VERSION_NAME
                )
            }
        }
    }
}

@Composable
private fun NoAccountsCard(onAddAccountClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = LocalCustomColors.current.cardBg),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = stringResource(id = R.string.message_no_accounts),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Button(
                onClick = onAddAccountClick,
                colors = ButtonDefaults.buttonColors(containerColor = LocalCustomColors.current.primaryColor),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(text = stringResource(id = R.string.label_add_account), color = Color.White)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewAccountTabScreen() {
    DailyExpenseTheme {
        AccountTabScreenContent(
            accounts = listOf(
                AccountWithBalance(
                    account = AccountEntity(
                        accountId = 1,
                        accountHolderName = "Holder",
                        accountBankName = "Axis Bank",
                        accountNumber = "1234567890",
                        openingBalance = 0.0
                    ),
                    currentBalance = 25000.0
                )
            ),
            categoryCount = 7,
            tagCount = 24,
            isWorking = false,
            onManageAccountsClick = {},
            onAccountClick = {},
            onAddAccountClick = {},
            onCategoriesClick = {},
            onTagsClick = {},
            onExportClick = {},
            onClearDataClick = {}
        )
    }
}
