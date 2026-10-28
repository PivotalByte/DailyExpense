package com.dailyexpense.presentation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dailyexpense.data.enums.TransactionCategory
import com.dailyexpense.data.enums.TransactionType
import com.dailyexpense.data.repository.AccountRepository
import com.dailyexpense.data.repository.CategoryRepository
import com.dailyexpense.data.repository.TagRepository
import com.dailyexpense.data.repository.TransactionRepository
import com.dailyexpense.data.models.AccountWithBalance
import com.dailyexpense.data.room.entity.CategoryEntity
import com.dailyexpense.data.room.entity.TagEntity
import com.dailyexpense.data.room.entity.TransactionEntity
import com.dailyexpense.navigation.ARG_TRANSACTION_ID
import com.dailyexpense.navigation.NO_ID
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.math.RoundingMode
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class NewTransactionViewModel @Inject constructor(
    private val transactionRepository: TransactionRepository,
    private val categoryRepository: CategoryRepository,
    private val tagRepository: TagRepository,
    private val accountRepository: AccountRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    // Id of the transaction being edited, or NO_ID when adding a new one
    private val editingTransactionId: Int = savedStateHandle[ARG_TRANSACTION_ID] ?: NO_ID
    val isEditMode: Boolean = editingTransactionId > 0

    private val _transactionTypeState = MutableStateFlow(TransactionType.EXPENSE)
    val transactionTypeState: StateFlow<TransactionType> = _transactionTypeState

    private val _amountState = MutableStateFlow("")
    val amountState: StateFlow<String> = _amountState

    private val _selectedCategoryState = MutableStateFlow<CategoryEntity?>(null)
    val selectedCategoryState: StateFlow<CategoryEntity?> = _selectedCategoryState

    private val _selectedAccountState = MutableStateFlow<AccountWithBalance?>(null)
    val selectedAccountState: StateFlow<AccountWithBalance?> = _selectedAccountState

    private val _selectedPaymentMethodState = MutableStateFlow<TransactionCategory?>(null)
    val selectedPaymentMethodState: StateFlow<TransactionCategory?> = _selectedPaymentMethodState

    private val _notesState = MutableStateFlow("")
    val notesState: StateFlow<String> = _notesState

    private val _dateState = MutableStateFlow(System.currentTimeMillis())
    val dateState: StateFlow<Long> = _dateState

    private val _hourState = MutableStateFlow(java.util.Calendar.getInstance().get(java.util.Calendar.HOUR_OF_DAY))
    val hourState: StateFlow<Int> = _hourState

    private val _minuteState = MutableStateFlow(java.util.Calendar.getInstance().get(java.util.Calendar.MINUTE))
    val minuteState: StateFlow<Int> = _minuteState

    private val _selectedTagsState = MutableStateFlow<List<TagEntity>>(emptyList())
    val selectedTagsState: StateFlow<List<TagEntity>> = _selectedTagsState

    private val _tagSearchQueryState = MutableStateFlow("")
    val tagSearchQueryState: StateFlow<String> = _tagSearchQueryState

    @OptIn(ExperimentalCoroutinesApi::class)
    val suggestedTagsState: StateFlow<List<TagEntity>> =
        tagSearchQueryState.flatMapLatest { query ->
            if (query.isBlank()) {
                tagRepository.getAllTags()
            } else {
                tagRepository.searchTags(query)
            }
        }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    @OptIn(ExperimentalCoroutinesApi::class)
    val categoriesState: StateFlow<List<CategoryEntity>> =
        transactionTypeState.flatMapLatest { type ->
            if (type == TransactionType.NEUTRAL) {
                categoryRepository.getAllCategories()
            } else {
                categoryRepository.getCategoriesByType(type)
            }
        }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val accountsState: StateFlow<List<AccountWithBalance>> =
        accountRepository.getAllAccounts()
            .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    private val _saveResultState = MutableStateFlow<SaveResult?>(null)
    val saveResultState: StateFlow<SaveResult?> = _saveResultState

    init {
        if (isEditMode) loadTransaction(editingTransactionId)
    }

    private fun loadTransaction(transactionId: Int) {
        viewModelScope.launch {
            val full = transactionRepository.getTransactionById(transactionId) ?: return@launch
            val transaction = full.transaction
            val calendar = java.util.Calendar.getInstance().apply { time = transaction.date }

            _transactionTypeState.value = transaction.transactionType
            _amountState.value = BigDecimal.valueOf(transaction.amount)
                .setScale(2, RoundingMode.HALF_UP)
                .stripTrailingZeros()
                .toPlainString()
            _notesState.value = transaction.title
            _selectedCategoryState.value = full.category
            _selectedAccountState.value = accountRepository.getAccountById(transaction.accountId)
            _selectedPaymentMethodState.value = transaction.transactionCategory
            _dateState.value = transaction.date.time
            _hourState.value = calendar.get(java.util.Calendar.HOUR_OF_DAY)
            _minuteState.value = calendar.get(java.util.Calendar.MINUTE)
            _selectedTagsState.value = full.tags
        }
    }

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

    fun updateSelectedAccount(account: AccountWithBalance) {
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

    fun updateTime(hour: Int, minute: Int) {
        _hourState.value = hour
        _minuteState.value = minute
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
                val tag = tagRepository.getOrCreateTag(tagName.trim())
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
            // Non-null once validation has passed
            val category = selectedCategoryState.value ?: return@launch
            val account = selectedAccountState.value ?: return@launch
            val paymentMethod = selectedPaymentMethodState.value ?: return@launch

            // Combine date and time into a single timestamp
            val calendar = java.util.Calendar.getInstance()
            calendar.timeInMillis = dateState.value
            calendar.set(java.util.Calendar.HOUR_OF_DAY, hourState.value)
            calendar.set(java.util.Calendar.MINUTE, minuteState.value)
            calendar.set(java.util.Calendar.SECOND, 0)
            calendar.set(java.util.Calendar.MILLISECOND, 0)

            val transaction = TransactionEntity(
                transactionId = if (isEditMode) editingTransactionId else 0,
                title = notesState.value,
                categoryId = category.categoryId,
                accountId = account.account.accountId,
                date = Date(calendar.timeInMillis),
                transactionType = transactionTypeState.value,
                transactionCategory = paymentMethod,
                amount = amountState.value.toDouble()
            )

            try {
                val tagIds = selectedTagsState.value.map { it.tagId }
                when {
                    isEditMode -> transactionRepository.updateTransactionWithTags(transaction, tagIds)
                    tagIds.isNotEmpty() -> transactionRepository.insertTransactionWithTags(transaction, tagIds)
                    else -> transactionRepository.insertTransaction(transaction)
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
