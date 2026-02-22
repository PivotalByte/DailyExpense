package com.dailyexpense.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.dailyexpense.data.room.entity.AccountEntity
import com.dailyexpense.navigation.Route
import com.dailyexpense.presentation.AccountViewModel
import com.dailyexpense.ui.bottomsheet.BottomSheetHost
import com.dailyexpense.ui.bottomsheet.factory.rememberCustomBottomSheetController
import com.dailyexpense.ui.components.AccountListItem
import com.dailyexpense.ui.components.CustomToolbar
import com.dailyexpense.ui.theme.LocalCustomColors

@Composable
fun AccountListScreen(
    navController: NavHostController,
    accountViewModel: AccountViewModel = hiltViewModel()
) {
    val accounts by accountViewModel.accounts.collectAsState()
    val bottomSheetController = rememberCustomBottomSheetController()

    BottomSheetHost(controller = bottomSheetController) {
        AccountListScreenContent(
            accounts = accounts,
            onAccountClick = { account ->
                navController.navigate(Route.AddNewAccount.createRoute(account.accountId))
            },
            onAddNewClick = {
                navController.navigate(Route.AddNewAccount.createRoute())
            },
            onBackClick = {
                navController.popBackStack()
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountListScreenContent(
    accounts: List<AccountEntity>,
    onAccountClick: (AccountEntity) -> Unit,
    onAddNewClick: () -> Unit,
    onBackClick: () -> Unit
) {
    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddNewClick,
                containerColor = LocalCustomColors.current.primaryColor
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Account",
                    tint = Color.White
                )
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            CustomToolbar(
                title = "Accounts",
                showBack = true,
                onBack = onBackClick
            )

            if (accounts.isEmpty()) {
                EmptyAccountsState(
                    onAddAccountClick = onAddNewClick
                )
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(LocalCustomColors.current.appBg),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(
                        count = accounts.size,
                        key = { index -> accounts[index].accountId }
                    ) { index ->
                        AccountListItem(
                            account = accounts[index],
                            onClick = { onAccountClick(accounts[index]) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun EmptyAccountsState(
    onAddAccountClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(LocalCustomColors.current.appBg),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(
                imageVector = Icons.Default.AccountBalance,
                contentDescription = null,
                modifier = Modifier.size(80.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f)
            )
            Text(
                text = "No accounts yet",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Button(
                onClick = onAddAccountClick,
                colors = ButtonDefaults.buttonColors(
                    containerColor = LocalCustomColors.current.primaryColor
                )
            ) {
                Text(text = "Add Your First Account", color = Color.White)
            }
        }
    }
}
