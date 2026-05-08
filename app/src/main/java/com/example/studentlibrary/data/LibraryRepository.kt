package com.example.studentlibrary.data

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import java.io.IOException
import javax.inject.Inject

class LibraryRepository @Inject constructor(
    private val dao: LibraryDao,
    @ApplicationContext private val context: Context,
) {
    suspend fun seedIfEmpty() = dao.seedIfEmpty()

    fun observeBooks(query: String?, category: String?): Flow<List<BookEntity>> =
        dao.observeSearch(query, category)

    fun observeFavorites(): Flow<List<BookEntity>> = dao.observeFavorites()

    fun observeBook(id: Long): Flow<BookEntity?> = dao.observeBook(id)

    suspend fun getBookOnce(id: Long): BookEntity? = dao.getBookOnce(id)

    suspend fun setFavorite(id: Long, favorite: Boolean) = dao.setFavorite(id, favorite)

    suspend fun addBook(book: BookEntity): Long = dao.insertBook(book)

    suspend fun updateBook(book: BookEntity) = dao.updateBook(book)

    suspend fun deleteBook(book: BookEntity) = dao.deleteBook(book)

    suspend fun readBookText(id: Long): String {
        val book = dao.getBookOnce(id) ?: return ""
        if (book.content.isNotBlank()) return book.content
        if (book.textAssetPath.isBlank()) return ""
        return try {
            context.assets.open(book.textAssetPath).bufferedReader(Charsets.UTF_8).use { it.readText() }
        } catch (_: IOException) {
            ""
        }
    }
}

