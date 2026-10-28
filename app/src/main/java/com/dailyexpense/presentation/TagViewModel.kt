package com.dailyexpense.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dailyexpense.data.models.TagWithCount
import com.dailyexpense.data.repository.TagRepository
import com.dailyexpense.data.room.entity.TagEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TagViewModel @Inject constructor(
    private val repository: TagRepository
) : ViewModel() {

    val tags: StateFlow<List<TagWithCount>> =
        repository.getTagsWithCount().stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    /** Non-null while the add/rename sheet is open. */
    private val _editor = MutableStateFlow<EditorState?>(null)
    val editor: StateFlow<EditorState?> = _editor.asStateFlow()

    fun startNew() {
        _editor.value = EditorState()
    }

    fun startEdit(item: TagWithCount) {
        _editor.value = EditorState(
            tagId = item.tag.tagId,
            name = item.tag.name,
            transactionCount = item.transactionCount
        )
    }

    fun updateName(name: String) {
        _editor.value = _editor.value?.copy(name = name, error = null)
    }

    fun dismissEditor() {
        _editor.value = null
    }

    fun save() {
        val state = _editor.value ?: return
        val name = state.name.trim()
        viewModelScope.launch {
            val existing = repository.findTagByName(name)
            val error = when {
                name.isEmpty() -> EditorError.EmptyName
                existing != null && existing.tagId != state.tagId -> EditorError.DuplicateName
                else -> null
            }
            if (error != null) {
                _editor.value = state.copy(error = error)
                return@launch
            }
            repository.saveTag(TagEntity(tagId = state.tagId, name = name))
            _editor.value = null
        }
    }

    /** Removes the tag from every transaction; the transactions themselves are kept. */
    fun delete() {
        val state = _editor.value ?: return
        if (state.isNew) return
        viewModelScope.launch {
            repository.deleteTag(TagEntity(tagId = state.tagId, name = state.name))
            _editor.value = null
        }
    }

    data class EditorState(
        val tagId: Int = 0,
        val name: String = "",
        val transactionCount: Int = 0,
        val error: EditorError? = null
    ) {
        val isNew: Boolean get() = tagId == 0
    }

    enum class EditorError { EmptyName, DuplicateName }
}
