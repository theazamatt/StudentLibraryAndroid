package com.example.studentlibrary.ui.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.studentlibrary.data.BookEntity
import com.example.studentlibrary.data.LibraryRepository
import com.example.studentlibrary.ui.navigation.Args
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BookDetailsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val repository: LibraryRepository,
) : ViewModel() {
    private val bookId: Long =
        savedStateHandle.get<String>(Args.BookId)?.toLong() ?: 0L

    val book: StateFlow<BookEntity?> =
        repository.observeBook(bookId)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    fun toggleFavorite(current: BookEntity) {
        viewModelScope.launch {
            repository.setFavorite(current.id, !current.isFavorite)
        }
    }
}
