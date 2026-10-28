package com.dailyexpense.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dailyexpense.data.models.AccountWithBalance
import com.dailyexpense.data.repository.AccountRepository
import com.dailyexpense.data.repository.CategoryRepository
import com.dailyexpense.data.repository.DataRepository
import com.dailyexpense.data.repository.TagRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.io.File
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class AccountTabViewModel @Inject constructor(
    accountRepository: AccountRepository,
    categoryRepository: CategoryRepository,
    tagRepository: TagRepository,
    private val dataRepository: DataRepository
) : ViewModel() {

    val accounts: StateFlow<List<AccountWithBalance>> =
        accountRepository.getAllAccounts().stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val categoryCount: StateFlow<Int> =
        categoryRepository.getCategoryCount().stateIn(viewModelScope, SharingStarted.Lazily, 0)

    val tagCount: StateFlow<Int> =
        tagRepository.getTagsWithCount().map { it.size }.stateIn(viewModelScope, SharingStarted.Lazily, 0)

    private val _isWorking = MutableStateFlow(false)
    val isWorking: StateFlow<Boolean> = _isWorking.asStateFlow()

    /** One-off result for the screen to act on, then clear with [consumeEvent]. */
    private val _event = MutableStateFlow<Event?>(null)
    val event: StateFlow<Event?> = _event.asStateFlow()

    fun exportTransactions() = runExclusive {
        _event.value = try {
            val result = dataRepository.exportTransactionsCsv()
            if (result.transactionCount == 0) Event.NothingToExport else Event.ExportReady(result.file)
        } catch (_: IOException) {
            Event.ExportFailed
        }
    }

    fun clearAllData() = runExclusive {
        dataRepository.clearAllData()
        _event.value = Event.DataCleared
    }

    fun consumeEvent() {
        _event.value = null
    }

    private fun runExclusive(block: suspend () -> Unit) {
        if (_isWorking.value) return
        _isWorking.value = true
        viewModelScope.launch {
            try {
                block()
            } finally {
                _isWorking.value = false
            }
        }
    }

    sealed class Event {
        data class ExportReady(val file: File) : Event()
        object NothingToExport : Event()
        object ExportFailed : Event()
        object DataCleared : Event()
    }
}
