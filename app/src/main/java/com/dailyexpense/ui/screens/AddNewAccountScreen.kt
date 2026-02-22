package com.dailyexpense.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.dailyexpense.data.room.entity.AccountEntity
import com.dailyexpense.presentation.AccountViewModel
import com.dailyexpense.ui.bottomsheet.BottomSheetHost
import com.dailyexpense.ui.bottomsheet.factory.rememberCustomBottomSheetController
import com.dailyexpense.ui.bottomsheet.sheets.DeleteAccountBottomSheet
import com.dailyexpense.ui.components.CustomTextField
import com.dailyexpense.ui.components.CustomToolbar
import com.dailyexpense.ui.theme.LocalCustomColors

@Composable
fun AddNewAccountScreen(
    navController: NavHostController,
    accountId: Int,
    accountViewModel: AccountViewModel = hiltViewModel()
) {
    val accountHolderName by accountViewModel.accountHolderName.collectAsState()
    val accountBankName by accountViewModel.accountBankName.collectAsState()
    val accountNumber by accountViewModel.accountNumber.collectAsState()
    val balance by accountViewModel.balance.collectAsState()
    val saveResult by accountViewModel.saveResult.collectAsState()
    val currentAccount by accountViewModel.currentAccount.collectAsState()

    val isExistingAccount = accountId > 0
    var isEditMode by remember { mutableStateOf(!isExistingAccount) }
    val bottomSheetController = rememberCustomBottomSheetController()

    // Load account data if viewing/editing existing account
    LaunchedEffect(accountId) {
        if (isExistingAccount) {
            accountViewModel.loadAccount(accountId)
        } else {
            accountViewModel.resetForm()
        }
    }

    // Handle save result
    LaunchedEffect(saveResult) {
        when (saveResult) {
            is AccountViewModel.SaveResult.Success -> {
                if (isExistingAccount) {
                    // After saving, go back to detail mode
                    isEditMode = false
                } else {
                    navController.popBackStack()
                }
                accountViewModel.clearSaveResult()
            }
            is AccountViewModel.SaveResult.Error -> {
                accountViewModel.clearSaveResult()
            }
            null -> {}
        }
    }

    BottomSheetHost(controller = bottomSheetController) {
        AddNewAccountScreenContent(
            isExistingAccount = isExistingAccount,
            isEditMode = isEditMode,
            accountHolderName = accountHolderName,
            accountBankName = accountBankName,
            accountNumber = accountNumber,
            balance = balance,
            currentAccount = currentAccount,
            onAccountHolderNameChange = accountViewModel::updateAccountHolderName,
            onAccountBankNameChange = accountViewModel::updateAccountBankName,
            onAccountNumberChange = accountViewModel::updateAccountNumber,
            onBalanceChange = accountViewModel::updateBalance,
            onSaveClick = {
                accountViewModel.saveAccount {}
            },
            onBackClick = {
                if (isEditMode && isExistingAccount) {
                    // Go back to detail mode
                    isEditMode = false
                    // Reload account data to discard changes
                    accountViewModel.loadAccount(accountId)
                } else {
                    navController.popBackStack()
                }
            },
            onEditClick = {
                isEditMode = true
            },
            onDeleteClick = {
                currentAccount?.let { account ->
                    bottomSheetController.show {
                        DeleteAccountBottomSheet(
                            account = account,
                            onConfirm = {
                                accountViewModel.deleteAccount(account) {
                                    navController.popBackStack()
                                }
                            },
                            closeSheet = { bottomSheetController.hide() }
                        )
                    }
                }
            }
        )
    }
}

@Composable
fun AddNewAccountScreenContent(
    isExistingAccount: Boolean,
    isEditMode: Boolean,
    accountHolderName: String,
    accountBankName: String,
    accountNumber: String,
    balance: String,
    currentAccount: AccountEntity?,
    onAccountHolderNameChange: (String) -> Unit,
    onAccountBankNameChange: (String) -> Unit,
    onAccountNumberChange: (String) -> Unit,
    onBalanceChange: (String) -> Unit,
    onSaveClick: () -> Unit,
    onBackClick: () -> Unit,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }

    val title = when {
        !isExistingAccount -> "Add New Account"
        isEditMode -> "Edit Account"
        else -> "Account Detail"
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        CustomToolbar(
            title = title,
            showBack = true,
            onBack = onBackClick,
            actions = {
                // Show menu only in detail mode (existing account and not editing)
                if (isExistingAccount && !isEditMode) {
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
                            text = { Text("Edit") },
                            onClick = {
                                showMenu = false
                                onEditClick()
                            },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = null
                                )
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Delete") },
                            onClick = {
                                showMenu = false
                                onDeleteClick()
                            },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = null
                                )
                            }
                        )
                    }
                }
            }
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(LocalCustomColors.current.appBg)
                .imePadding(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                CustomTextField(
                    value = accountHolderName,
                    onValueChange = onAccountHolderNameChange,
                    label = "Account Holder Name",
                    placeholder = "Enter account holder name",
                    capitalization = KeyboardCapitalization.Words,
                    enabled = isEditMode
                )
            }

            item {
                CustomTextField(
                    value = accountBankName,
                    onValueChange = onAccountBankNameChange,
                    label = "Bank Name",
                    placeholder = "Enter bank name",
                    capitalization = KeyboardCapitalization.Words,
                    enabled = isEditMode
                )
            }

            item {
                CustomTextField(
                    value = accountNumber,
                    onValueChange = onAccountNumberChange,
                    label = "Account Number",
                    placeholder = "Enter account number",
                    keyboardType = KeyboardType.Number,
                    enabled = isEditMode
                )
            }

            item {
                CustomTextField(
                    value = balance,
                    onValueChange = { newValue ->
                        if (newValue.isEmpty() || newValue.matches(Regex("^\\d*\\.?\\d{0,2}$"))) {
                            onBalanceChange(newValue)
                        }
                    },
                    label = if (isExistingAccount) "Current Balance" else "Initial Balance",
                    placeholder = "0.00",
                    prefix = "\u20B9",
                    keyboardType = KeyboardType.Decimal,
                    enabled = isEditMode
                )
            }

            // Show save button only in edit mode
            if (isEditMode) {
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = onSaveClick,
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = LocalCustomColors.current.primaryColor
                        ),
                        shape = RoundedCornerShape(12.dp),
                        enabled = accountHolderName.isNotBlank() &&
                                  accountBankName.isNotBlank() &&
                                  accountNumber.isNotBlank() &&
                                  balance.isNotBlank()
                    ) {
                        Text(
                            text = if (isExistingAccount) "Update Account" else "Save Account",
                            style = MaterialTheme.typography.labelLarge,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}
