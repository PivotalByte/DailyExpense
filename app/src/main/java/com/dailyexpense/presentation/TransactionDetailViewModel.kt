package com.dailyexpense.presentation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dailyexpense.data.repository.TransactionRepository
import com.dailyexpense.data.room.wrapper.TransactionFull
import com.dailyexpense.navigation.ARG_TRANSACTION_ID
import com.dailyexpense.navigation.NO_ID
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TransactionDetailViewModel @Inject constructor(
    private val transactionRepository: TransactionRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val transactionId: Int = savedStateHandle[ARG_TRANSACTION_ID] ?: NO_ID

    private val isDeleted = MutableStateFlow(false)

    val uiState: StateFlow<UiState> =
        combine(transactionRepository.observeTransactionById(transactionId), isDeleted) { full, deleted ->
            when {
                deleted -> UiState.Deleted
                full == null -> UiState.NotFound
                else -> UiState.Loaded(full)
            }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS), UiState.Loading)

    fun deleteTransaction() {
        val loaded = uiState.value as? UiState.Loaded ?: return
        viewModelScope.launch {
            // Mark first so the screen closes instead of briefly showing "not found"
            isDeleted.value = true
            transactionRepository.deleteTransaction(loaded.transaction.transaction)
        }
    }

    sealed class UiState {
        object Loading : UiState()
        data class Loaded(val transaction: TransactionFull) : UiState()
        object NotFound : UiState()
        object Deleted : UiState()
    }

    private companion object {
        const val STOP_TIMEOUT_MILLIS = 5_000L
    }
}
