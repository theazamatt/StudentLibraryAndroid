package com.example.studentlibrary.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.studentlibrary.data.BookEntity
import com.example.studentlibrary.data.LibraryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AdminSaveState(
    val isSaving: Boolean = false,
    val message: String? = null,
    val isSuccess: Boolean = false,
)

@HiltViewModel
class AdminViewModel @Inject constructor(
    private val repository: LibraryRepository,
) : ViewModel() {
    val books: StateFlow<List<BookEntity>> =
        repository.observeBooks(query = null, category = null)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    private val _saveState = MutableStateFlow(AdminSaveState())
    val saveState: StateFlow<AdminSaveState> = _saveState.asStateFlow()

    fun saveBook(book: BookEntity) {
        if (_saveState.value.isSaving) return

        viewModelScope.launch {
            _saveState.value = AdminSaveState(isSaving = true)
            runCatching {
                if (book.id == 0L) {
                    repository.addBook(book)
                } else {
                    repository.updateBook(book)
                }
            }.onSuccess {
                _saveState.value =
                    AdminSaveState(
                        isSaving = false,
                        isSuccess = true,
                        message = if (book.id == 0L) "Книга успешно добавлена" else "Изменения книги сохранены",
                    )
            }.onFailure { error ->
                _saveState.value =
                    AdminSaveState(
                        isSaving = false,
                        isSuccess = false,
                        message = error.message ?: "Не удалось сохранить изменения",
                    )
            }
        }
    }

    fun deleteBook(book: BookEntity) {
        if (_saveState.value.isSaving) return

        viewModelScope.launch {
            _saveState.value = AdminSaveState(isSaving = true)
            runCatching {
                repository.deleteBook(book)
            }.onSuccess {
                _saveState.value =
                    AdminSaveState(
                        isSaving = false,
                        isSuccess = true,
                        message = "Книга удалена",
                    )
            }.onFailure { error ->
                _saveState.value =
                    AdminSaveState(
                        isSaving = false,
                        isSuccess = false,
                        message = error.message ?: "Не удалось удалить книгу",
                    )
            }
        }
    }

    fun clearSaveState() {
        _saveState.value = AdminSaveState()
    }
}
