package com.example.studentlibrary.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.studentlibrary.data.BookEntity
import com.example.studentlibrary.ui.viewmodel.AdminViewModel
import kotlinx.coroutines.launch

private data class BookDraft(
    val id: Long = 0,
    val title: String = "",
    val author: String = "",
    val description: String = "",
    val category: String = "",
    val coverUrl: String = "",
    val content: String = "",
    val textAssetPath: String = "",
    val pdfUrl: String = "",
    val isFavorite: Boolean = false,
)

private fun BookEntity.toDraft(): BookDraft =
    BookDraft(
        id = id,
        title = title,
        author = author,
        description = description,
        category = category,
        coverUrl = coverUrl,
        content = content,
        textAssetPath = textAssetPath,
        pdfUrl = pdfUrl,
        isFavorite = isFavorite,
    )

private fun BookDraft.toEntity(): BookEntity =
    BookEntity(
        id = id,
        title = title.trim(),
        author = author.trim(),
        description = description.trim(),
        category = category.trim(),
        coverUrl = coverUrl.trim(),
        content = content.trim(),
        textAssetPath = textAssetPath.trim(),
        pdfUrl = pdfUrl.trim(),
        isFavorite = isFavorite,
    )

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminRoute(
    vm: AdminViewModel,
    contentPadding: PaddingValues,
    onBack: () -> Unit,
    onLogout: () -> Unit,
) {
    val books by vm.books.collectAsState()
    val saveState by vm.saveState.collectAsState()
    val listState = rememberLazyListState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    var draft by remember { mutableStateOf(BookDraft()) }
    var infoMessage by remember { mutableStateOf("Администратор может добавлять новые книги, редактировать и удалять существующие.") }
    var pendingDeleteBook by remember { mutableStateOf<BookEntity?>(null) }

    LaunchedEffect(saveState.message, saveState.isSaving) {
        val message = saveState.message ?: return@LaunchedEffect
        if (!saveState.isSaving) {
            snackbarHostState.showSnackbar(message)
            if (saveState.isSuccess) {
                draft = BookDraft()
                infoMessage = "Форма очищена. Можно продолжать работу с библиотекой."
            }
            vm.clearSaveState()
        }
    }

    pendingDeleteBook?.let { book ->
        AlertDialog(
            onDismissRequest = { pendingDeleteBook = null },
            title = { Text("Удалить книгу?") },
            text = { Text("Книга \"${book.title}\" будет удалена из библиотеки.") },
            confirmButton = {
                Button(
                    onClick = {
                        if (draft.id == book.id) {
                            draft = BookDraft()
                            infoMessage = "Редактируемая форма очищена, книга будет удалена."
                        }
                        pendingDeleteBook = null
                        vm.deleteBook(book)
                    },
                    enabled = !saveState.isSaving,
                ) {
                    Text("Удалить")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { pendingDeleteBook = null },
                    enabled = !saveState.isSaving,
                ) {
                    Text("Отмена")
                }
            },
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Панель администратора") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Назад")
                    }
                },
                actions = {
                    TextButton(onClick = onLogout) {
                        Text("Выход")
                    }
                },
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
    ) { inner ->
        LazyColumn(
            state = listState,
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier
                .padding(inner)
                .padding(contentPadding)
                .fillMaxSize(),
        ) {
            item {
                AdminEditorCard(
                    draft = draft,
                    infoMessage = infoMessage,
                    isSaving = saveState.isSaving,
                    onDraftChange = { draft = it },
                    onReset = {
                        draft = BookDraft()
                        infoMessage = "Форма очищена. Можно добавить новую книгу."
                    },
                    onSave = {
                        val candidate = draft
                        if (
                            candidate.title.isBlank() ||
                            candidate.author.isBlank() ||
                            candidate.category.isBlank() ||
                            candidate.description.isBlank()
                        ) {
                            infoMessage = "Заполните название, автора, категорию и описание."
                        } else {
                            vm.saveBook(candidate.toEntity())
                        }
                    },
                )
            }

            item {
                Text(
                    text = "Книги в библиотеке",
                    style = MaterialTheme.typography.titleLarge,
                )
            }

            items(books, key = { it.id }) { book ->
                AdminBookRow(
                    book = book,
                    onEdit = {
                        draft = book.toDraft()
                        infoMessage = "Редактируется: ${book.title}"
                        scope.launch {
                            listState.animateScrollToItem(0)
                        }
                    },
                    onDelete = {
                        pendingDeleteBook = book
                    },
                )
            }
        }
    }
}

@Composable
private fun AdminEditorCard(
    draft: BookDraft,
    infoMessage: String,
    isSaving: Boolean,
    onDraftChange: (BookDraft) -> Unit,
    onReset: () -> Unit,
    onSave: () -> Unit,
) {
    Card(
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Text(
                text = if (draft.id == 0L) "Добавить книгу" else "Редактировать книгу",
                style = MaterialTheme.typography.titleLarge,
            )
            Text(
                text = infoMessage,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            OutlinedTextField(
                value = draft.title,
                onValueChange = { onDraftChange(draft.copy(title = it)) },
                label = { Text("Название") },
                enabled = !isSaving,
                modifier = Modifier.fillMaxWidth(),
            )
            OutlinedTextField(
                value = draft.author,
                onValueChange = { onDraftChange(draft.copy(author = it)) },
                label = { Text("Автор") },
                enabled = !isSaving,
                modifier = Modifier.fillMaxWidth(),
            )
            OutlinedTextField(
                value = draft.category,
                onValueChange = { onDraftChange(draft.copy(category = it)) },
                label = { Text("Категория") },
                enabled = !isSaving,
                modifier = Modifier.fillMaxWidth(),
            )
            OutlinedTextField(
                value = draft.coverUrl,
                onValueChange = { onDraftChange(draft.copy(coverUrl = it)) },
                label = { Text("Ссылка на обложку") },
                enabled = !isSaving,
                modifier = Modifier.fillMaxWidth(),
            )
            OutlinedTextField(
                value = draft.description,
                onValueChange = { onDraftChange(draft.copy(description = it)) },
                label = { Text("Описание") },
                enabled = !isSaving,
                minLines = 3,
                modifier = Modifier.fillMaxWidth(),
            )
            OutlinedTextField(
                value = draft.pdfUrl,
                onValueChange = { onDraftChange(draft.copy(pdfUrl = it)) },
                label = { Text("Ссылка на PDF") },
                enabled = !isSaving,
                modifier = Modifier.fillMaxWidth(),
            )
            OutlinedTextField(
                value = draft.content,
                onValueChange = { onDraftChange(draft.copy(content = it)) },
                label = { Text("Текст книги (Content)") },
                enabled = !isSaving,
                minLines = 5,
                modifier = Modifier.fillMaxWidth(),
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxWidth(),
            ) {
                Button(
                    onClick = onSave,
                    enabled = !isSaving,
                    modifier = Modifier.weight(1f),
                ) {
                    if (isSaving) {
                        CircularProgressIndicator(
                            strokeWidth = 2.dp,
                            modifier = Modifier.padding(end = 8.dp),
                        )
                    }
                    Text(if (draft.id == 0L) "Добавить" else "Сохранить")
                }
                TextButton(
                    onClick = onReset,
                    enabled = !isSaving,
                    modifier = Modifier.weight(1f),
                ) {
                    Text("Очистить")
                }
            }
        }
    }
}

@Composable
private fun AdminBookRow(
    book: BookEntity,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
) {
    Card(
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Text(
                    text = book.title,
                    style = MaterialTheme.typography.titleMedium,
                )
                Text(
                    text = "${book.author} - ${book.category}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                TextButton(onClick = onEdit) {
                    Text("Изменить")
                }
                TextButton(onClick = onDelete) {
                    Text("Удалить", color = Color(0xFFB3261E))
                }
            }
        }
    }
}
