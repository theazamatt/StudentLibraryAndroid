package com.example.studentlibrary.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.studentlibrary.data.BookEntity
import com.example.studentlibrary.data.LibraryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class LibraryViewModel @Inject constructor(
    private val repository: LibraryRepository,
) : ViewModel() {
    val query = MutableStateFlow("")
    val category = MutableStateFlow<String?>(null)

    val books: StateFlow<List<BookEntity>> =
        combine(query, category) { q, c -> q to c }
            .flatMapLatest { (q, c) ->
                repository.observeBooks(
                    query = q.trim().ifBlank { null },
                    category = c?.trim()?.ifBlank { null },
                )
            }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun setQuery(value: String) {
        query.value = value
    }

    fun setCategory(value: String?) {
        category.value = value
    }

    fun toggleFavorite(book: BookEntity) {
        viewModelScope.launch {
            repository.setFavorite(book.id, !book.isFavorite)
        }
    }
}
