package com.dailyexpense.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dailyexpense.data.models.AccountWithBalance
import com.dailyexpense.data.repository.AccountRepository
import com.dailyexpense.data.room.entity.AccountEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class AccountViewModel @Inject constructor(
    private val repository: AccountRepository
) : ViewModel() {

    // State for account list screen
    val accounts: StateFlow<List<AccountWithBalance>> =
        repository.getAllAccounts()
            .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    // State for add/edit screen
    private val _accountHolderName = MutableStateFlow("")
    val accountHolderName: StateFlow<String> = _accountHolderName

    private val _accountBankName = MutableStateFlow("")
    val accountBankName: StateFlow<String> = _accountBankName

    private val _accountNumber = MutableStateFlow("")
    val accountNumber: StateFlow<String> = _accountNumber

    private val _balance = MutableStateFlow("")
    val balance: StateFlow<String> = _balance

    private val _saveResult = MutableStateFlow<SaveResult?>(null)
    val saveResult: StateFlow<SaveResult?> = _saveResult

    // 0 means a new account; Room assigns the id on insert
    private val _currentAccountId = MutableStateFlow(0)

    private val _currentAccount = MutableStateFlow<AccountEntity?>(null)
    val currentAccount: StateFlow<AccountEntity?> = _currentAccount

    // Update functions
    fun updateAccountHolderName(name: String) {
        _accountHolderName.value = name
    }

    fun updateAccountBankName(name: String) {
        _accountBankName.value = name
    }

    fun updateAccountNumber(number: String) {
        _accountNumber.value = number
    }

    fun updateBalance(balance: String) {
        _balance.value = balance
    }

    // Load account for editing
    fun loadAccount(accountId: Int) {
        if (accountId > 0) {
            viewModelScope.launch {
                repository.getAccountById(accountId)?.let { (account, currentBalance) ->
                    _currentAccount.value = account
                    _currentAccountId.value = account.accountId
                    _accountHolderName.value = account.accountHolderName
                    _accountBankName.value = account.accountBankName
                    _accountNumber.value = account.accountNumber
                    _balance.value = String.format(Locale.US, "%.2f", currentBalance)
                }
            }
        }
    }

    // Validation
    private fun validate(): ValidationResult {
        return when {
            accountHolderName.value.isBlank() ->
                ValidationResult(false, "Account holder name is required")
            accountBankName.value.isBlank() ->
                ValidationResult(false, "Bank name is required")
            accountNumber.value.isBlank() ->
                ValidationResult(false, "Account number is required")
            balance.value.isBlank() || balance.value.toDoubleOrNull() == null ->
                ValidationResult(false, "Valid balance is required")
            // An existing account can legitimately be overdrawn by its transactions
            balance.value.toDouble() < 0 && _currentAccountId.value <= 0 ->
                ValidationResult(false, "Balance cannot be negative")
            else -> ValidationResult(true, "")
        }
    }

    // Save account (create or update)
    fun saveAccount(onSuccess: () -> Unit) {
        viewModelScope.launch {
            val validation = validate()
            if (!validation.isValid) {
                _saveResult.value = SaveResult.Error(validation.message)
                return@launch
            }

            try {
                val account = AccountEntity(
                    accountId = _currentAccountId.value,
                    accountHolderName = accountHolderName.value.trim(),
                    accountBankName = accountBankName.value.trim(),
                    accountNumber = accountNumber.value.trim(),
                    openingBalance = 0.0
                )
                repository.saveAccount(account, currentBalance = balance.value.toDouble())
                _saveResult.value = SaveResult.Success
                onSuccess()
            } catch (e: android.database.sqlite.SQLiteException) {
                _saveResult.value = SaveResult.Error("Failed to save: ${e.message}")
            } catch (e: IllegalStateException) {
                _saveResult.value = SaveResult.Error("Failed to save: ${e.message}")
            }
        }
    }

    // Delete account
    fun deleteAccount(account: AccountEntity, onSuccess: () -> Unit) {
        viewModelScope.launch {
            try {
                repository.deleteAccount(account)
                onSuccess()
            } catch (e: android.database.sqlite.SQLiteException) {
                _saveResult.value = SaveResult.Error("Failed to delete: ${e.message}")
            } catch (e: IllegalStateException) {
                _saveResult.value = SaveResult.Error("Failed to delete: ${e.message}")
            }
        }
    }

    fun clearSaveResult() {
        _saveResult.value = null
    }

    fun resetForm() {
        _accountHolderName.value = ""
        _accountBankName.value = ""
        _accountNumber.value = ""
        _balance.value = ""
        _currentAccountId.value = 0
        _currentAccount.value = null
        _saveResult.value = null
    }

    sealed class SaveResult {
        object Success : SaveResult()
        data class Error(val message: String) : SaveResult()
    }

    data class ValidationResult(val isValid: Boolean, val message: String)
}
