package com.example.studentlibrary.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.studentlibrary.ui.viewmodel.FavoritesViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritesRoute(
    vm: FavoritesViewModel,
    onLogout: () -> Unit,
    contentPadding: PaddingValues,
    onOpenDetails: (Long) -> Unit,
) {
    val favorites by vm.favorites.collectAsState()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Избранное") },
                actions = {
                    TextButton(onClick = onLogout) {
                        Text("Выход")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(),
            )
        },
    ) { inner ->
        if (favorites.isEmpty()) {
            androidx.compose.foundation.layout.Box(
                modifier = Modifier
                    .padding(inner)
                    .padding(contentPadding)
                    .fillMaxSize()
                    .padding(16.dp),
            ) {
                AssistChip(
                    onClick = {},
                    enabled = false,
                    label = { Text("Вы еще ничего не добавили в избранное") },
                    colors = AssistChipDefaults.assistChipColors(
                        disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                        disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    ),
                )
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = 160.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(16.dp),
                modifier = Modifier
                    .padding(inner)
                    .padding(contentPadding)
                    .fillMaxSize(),
            ) {
                items(favorites, key = { it.id }) { book ->
                    BookCard(
                        book = book,
                        onOpen = { onOpenDetails(book.id) },
                        onToggleFavorite = { vm.removeFromFavorites(book) },
                    )
                }
            }
        }
    }
}
