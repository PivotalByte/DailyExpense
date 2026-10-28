package com.dailyexpense.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dailyexpense.data.enums.TransactionType
import com.dailyexpense.data.models.CATEGORY_COLORS
import com.dailyexpense.data.models.CATEGORY_ICONS
import com.dailyexpense.data.models.CategoryWithCount
import com.dailyexpense.data.repository.CategoryRepository
import com.dailyexpense.data.room.entity.CategoryEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class CategoryViewModel @Inject constructor(
    private val repository: CategoryRepository
) : ViewModel() {

    private val _selectedType = MutableStateFlow(TransactionType.EXPENSE)
    val selectedType: StateFlow<TransactionType> = _selectedType.asStateFlow()

    val categories: StateFlow<List<CategoryWithCount>> =
        _selectedType.flatMapLatest { type -> repository.getCategoriesWithCount(type) }
            .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    /** Non-null while the add/edit sheet is open. */
    private val _editor = MutableStateFlow<EditorState?>(null)
    val editor: StateFlow<EditorState?> = _editor.asStateFlow()

    fun selectType(type: TransactionType) {
        _selectedType.value = type
    }

    fun startNew() {
        _editor.value = EditorState(transactionType = _selectedType.value)
    }

    fun startEdit(item: CategoryWithCount) {
        val category = item.category
        _editor.value = EditorState(
            categoryId = category.categoryId,
            name = category.name,
            transactionType = category.transactionType,
            colorCode = category.colorCode,
            iconResName = category.iconResName,
            transactionCount = item.transactionCount
        )
    }

    fun updateName(name: String) = updateEditor { it.copy(name = name, error = null) }

    /** Only a new category can change type; existing transactions depend on it. */
    fun updateType(type: TransactionType) = updateEditor { if (it.isNew) it.copy(transactionType = type) else it }

    fun updateColor(colorCode: String) = updateEditor { it.copy(colorCode = colorCode) }

    fun updateIcon(iconResName: String) = updateEditor { it.copy(iconResName = iconResName) }

    fun dismissEditor() {
        _editor.value = null
    }

    fun save() {
        val state = _editor.value ?: return
        val name = state.name.trim()
        viewModelScope.launch {
            val existing = repository.findCategoryByName(name, state.transactionType)
            val error = when {
                name.isEmpty() -> EditorError.EmptyName
                existing != null && existing.categoryId != state.categoryId -> EditorError.DuplicateName
                else -> null
            }
            if (error != null) {
                _editor.value = state.copy(error = error)
                return@launch
            }
            repository.saveCategory(
                CategoryEntity(
                    categoryId = state.categoryId,
                    name = name,
                    transactionType = state.transactionType,
                    colorCode = state.colorCode,
                    iconResName = state.iconResName
                )
            )
            _selectedType.value = state.transactionType
            _editor.value = null
        }
    }

    /**
     * Deleting a category would cascade to its transactions, so only unused
     * categories can be deleted.
     */
    fun delete() {
        val state = _editor.value ?: return
        if (state.isNew) return
        if (state.transactionCount > 0) {
            _editor.value = state.copy(error = EditorError.InUse)
            return
        }
        viewModelScope.launch {
            repository.deleteCategory(
                CategoryEntity(
                    categoryId = state.categoryId,
                    name = state.name,
                    transactionType = state.transactionType,
                    colorCode = state.colorCode,
                    iconResName = state.iconResName
                )
            )
            _editor.value = null
        }
    }

    private inline fun updateEditor(transform: (EditorState) -> EditorState) {
        _editor.value = _editor.value?.let(transform)
    }

    data class EditorState(
        val categoryId: Int = 0,
        val name: String = "",
        val transactionType: TransactionType,
        val colorCode: String = CATEGORY_COLORS.first(),
        val iconResName: String = CATEGORY_ICONS.last(),
        val transactionCount: Int = 0,
        val error: EditorError? = null
    ) {
        val isNew: Boolean get() = categoryId == 0
    }

    enum class EditorError { EmptyName, DuplicateName, InUse }
}
