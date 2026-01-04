package com.dailyexpense.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dailyexpense.data.enums.TransactionCategory
import com.dailyexpense.data.enums.TransactionType
import com.dailyexpense.data.repository.DatabaseRepository
import com.dailyexpense.data.room.entity.AccountEntity
import com.dailyexpense.data.room.entity.CategoryEntity
import com.dailyexpense.data.room.entity.TagEntity
import com.dailyexpense.data.room.entity.TransactionEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class NewTransactionViewModel @Inject constructor(
    private val repository: DatabaseRepository
) : ViewModel() {

    private val _transactionTypeState = MutableStateFlow(TransactionType.EXPENSE)
    val transactionTypeState: StateFlow<TransactionType> = _transactionTypeState

    private val _amountState = MutableStateFlow("")
    val amountState: StateFlow<String> = _amountState

    private val _selectedCategoryState = MutableStateFlow<CategoryEntity?>(null)
    val selectedCategoryState: StateFlow<CategoryEntity?> = _selectedCategoryState

    private val _selectedAccountState = MutableStateFlow<AccountEntity?>(null)
    val selectedAccountState: StateFlow<AccountEntity?> = _selectedAccountState

    private val _selectedPaymentMethodState = MutableStateFlow<TransactionCategory?>(null)
    val selectedPaymentMethodState: StateFlow<TransactionCategory?> = _selectedPaymentMethodState

    private val _notesState = MutableStateFlow("")
    val notesState: StateFlow<String> = _notesState

    private val _dateState = MutableStateFlow(System.currentTimeMillis())
    val dateState: StateFlow<Long> = _dateState

    private val _selectedTagsState = MutableStateFlow<List<TagEntity>>(emptyList())
    val selectedTagsState: StateFlow<List<TagEntity>> = _selectedTagsState

    private val _tagSearchQueryState = MutableStateFlow("")
    val tagSearchQueryState: StateFlow<String> = _tagSearchQueryState

    @OptIn(ExperimentalCoroutinesApi::class)
    val suggestedTagsState: StateFlow<List<TagEntity>> =
        tagSearchQueryState.flatMapLatest { query ->
            if (query.isBlank()) {
                repository.getAllTags()
            } else {
                repository.searchTags(query)
            }
        }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    @OptIn(ExperimentalCoroutinesApi::class)
    val categoriesState: StateFlow<List<CategoryEntity>> =
        transactionTypeState.flatMapLatest { type ->
            if (type == TransactionType.NEUTRAL) {
                repository.getAllCategories()
            } else {
                repository.getCategoriesByType(type)
            }
        }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val accountsState: StateFlow<List<AccountEntity>> =
        repository.getAllAccounts()
            .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    private val _saveResultState = MutableStateFlow<SaveResult?>(null)
    val saveResultState: StateFlow<SaveResult?> = _saveResultState

    fun updateTransactionType(type: TransactionType) {
        _transactionTypeState.value = type
        if (selectedCategoryState.value?.transactionType != type) {
            _selectedCategoryState.value = null
        }
        _selectedPaymentMethodState.value = null
    }

    fun updateAmount(amount: String) {
        _amountState.value = amount
    }

    fun updateSelectedCategory(category: CategoryEntity) {
        _selectedCategoryState.value = category
    }

    fun updateSelectedAccount(account: AccountEntity) {
        _selectedAccountState.value = account
    }

    fun updatePaymentMethod(method: TransactionCategory) {
        _selectedPaymentMethodState.value = method
    }

    fun updateNotes(notes: String) {
        _notesState.value = notes
    }

    fun updateDate(date: Long) {
        _dateState.value = date
    }

    fun updateTagSearchQuery(query: String) {
        _tagSearchQueryState.value = query
    }

    fun addTag(tag: TagEntity) {
        if (!_selectedTagsState.value.contains(tag)) {
            _selectedTagsState.value = _selectedTagsState.value + tag
        }
        _tagSearchQueryState.value = ""
    }

    fun removeTag(tag: TagEntity) {
        _selectedTagsState.value = _selectedTagsState.value - tag
    }

    fun createAndAddTag(tagName: String) {
        if (tagName.isNotBlank()) {
            viewModelScope.launch {
                val tag = repository.getOrCreateTag(tagName.trim())
                addTag(tag)
            }
        }
    }

    fun clearSaveResult() {
        _saveResultState.value = null
    }

    private fun validateTransaction(): ValidationResult {
        return when {
            amountState.value.isEmpty() || amountState.value.toDoubleOrNull() == null ->
                ValidationResult(false, "Please enter a valid amount")
            amountState.value.toDouble() <= 0 ->
                ValidationResult(false, "Amount must be greater than 0")
            notesState.value.isEmpty() || notesState.value.isBlank() ->
                ValidationResult(false, "Please enter notes")
            selectedCategoryState.value == null ->
                ValidationResult(false, "Please select a category")
            selectedAccountState.value == null ->
                ValidationResult(false, "Please select an account")
            selectedPaymentMethodState.value == null ->
                ValidationResult(false, "Please select a payment method")
            else -> ValidationResult(true, "")
        }
    }

    fun saveTransaction(onSuccess: () -> Unit) {
        viewModelScope.launch {
            val validation = validateTransaction()
            if (!validation.isValid) {
                _saveResultState.value = SaveResult.Error(validation.message)
                return@launch
            }

            val transaction = TransactionEntity(
                title = selectedCategoryState.value!!.name,
                categoryId = selectedCategoryState.value!!.categoryId,
                accountId = selectedAccountState.value!!.accountId,
                date = Date(dateState.value),
                transactionType = transactionTypeState.value,
                transactionCategory = selectedPaymentMethodState.value!!,
                amount = amountState.value.toDouble()
            )

            try {
                if (selectedTagsState.value.isNotEmpty()) {
                    val tagIds = selectedTagsState.value.map { it.tagId }
                    repository.insertTransactionWithTags(transaction, tagIds)
                } else {
                    repository.insertTransaction(transaction)
                }
                _saveResultState.value = SaveResult.Success
                onSuccess()
            } catch (e: android.database.sqlite.SQLiteException) {
                _saveResultState.value = SaveResult.Error("Failed to save: ${e.message}")
            } catch (e: IllegalStateException) {
                _saveResultState.value = SaveResult.Error("Failed to save: ${e.message}")
            }
        }
    }

    sealed class SaveResult {
        object Success : SaveResult()
        data class Error(val message: String) : SaveResult()
    }

    data class ValidationResult(val isValid: Boolean, val message: String)
}
