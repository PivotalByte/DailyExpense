package com.dailyexpense.presentation

import androidx.lifecycle.ViewModel
import com.dailyexpense.data.enums.TransactionType
import com.dailyexpense.data.repository.DatabaseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class NewTransactionViewModel @Inject constructor(
    repository: DatabaseRepository
) : ViewModel() {

    private val _transactionTypeState = MutableStateFlow(TransactionType.EXPENSE)
    val transactionTypeState: StateFlow<TransactionType> = _transactionTypeState

    fun updateTransactionType(type: TransactionType) {
        _transactionTypeState.value = type
    }
}