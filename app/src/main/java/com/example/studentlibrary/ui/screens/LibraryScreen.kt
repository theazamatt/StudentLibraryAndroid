package com.example.studentlibrary.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.studentlibrary.data.BookEntity
import com.example.studentlibrary.ui.auth.AuthSession
import com.example.studentlibrary.ui.auth.UserRole
import com.example.studentlibrary.ui.viewmodel.LibraryViewModel

private val Categories = listOf("IT", "Math", "Skills")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LibraryRoute(
    vm: LibraryViewModel,
    session: AuthSession,
    contentPadding: PaddingValues,
    onOpenDetails: (Long) -> Unit,
    onOpenAdmin: () -> Unit,
    onLogout: () -> Unit,
) {
    val query by vm.query.collectAsState()
    val selectedCategory by vm.category.collectAsState()
    val books by vm.books.collectAsState()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Student Library") },
                actions = {
                    if (session.role == UserRole.ADMIN) {
                        IconButton(onClick = onOpenAdmin) {
                            Icon(Icons.Default.AdminPanelSettings, contentDescription = "Панель администратора")
                        }
                    }
                    TextButton(onClick = onLogout) {
                        Text("Выход")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(),
            )
        },
    ) { inner ->
        LibraryScreen(
            query = query,
            selectedCategory = selectedCategory,
            books = books,
            onQueryChange = vm::setQuery,
            onClearQuery = { vm.setQuery("") },
            onSelectCategory = vm::setCategory,
            onToggleFavorite = vm::toggleFavorite,
            onOpenDetails = onOpenDetails,
            modifier = Modifier.padding(inner).padding(contentPadding),
        )
    }
}

@Composable
private fun LibraryScreen(
    query: String,
    selectedCategory: String?,
    books: List<BookEntity>,
    onQueryChange: (String) -> Unit,
    onClearQuery: () -> Unit,
    onSelectCategory: (String?) -> Unit,
    onToggleFavorite: (BookEntity) -> Unit,
    onOpenDetails: (Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    androidx.compose.foundation.layout.Column(
        modifier = modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        OutlinedTextField(
            value = query,
            onValueChange = onQueryChange,
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Поиск") },
            trailingIcon = {
                if (query.isNotBlank()) {
                    IconButton(onClick = onClearQuery) {
                        Icon(Icons.Default.Close, contentDescription = "Очистить")
                    }
                }
            },
            singleLine = true,
            placeholder = { Text("Поиск по названию или автору") },
            modifier = Modifier.fillMaxWidth().padding(bottom = 4.dp),
        )

        androidx.compose.foundation.layout.Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            FilterChip(
                selected = selectedCategory == null,
                onClick = { onSelectCategory(null) },
                label = { Text("Все") },
            )
            Categories.forEach { cat ->
                FilterChip(
                    selected = selectedCategory == cat,
                    onClick = { onSelectCategory(cat) },
                    label = { Text(cat) },
                )
            }
        }

        if (books.isEmpty()) {
            AssistChip(
                onClick = {},
                enabled = false,
                label = { Text("Ничего не найдено") },
                colors = AssistChipDefaults.assistChipColors(
                    disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                    disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                ),
            )
        } else {
            LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = 160.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 8.dp),
                modifier = Modifier.fillMaxSize(),
            ) {
                items(books, key = { it.id }) { book ->
                    BookCard(
                        book = book,
                        onOpen = { onOpenDetails(book.id) },
                        onToggleFavorite = { onToggleFavorite(book) },
                    )
                }
            }
        }
    }
}
