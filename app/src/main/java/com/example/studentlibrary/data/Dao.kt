package com.example.studentlibrary.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface LibraryDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertBook(book: BookEntity): Long

    @Update
    suspend fun updateBook(book: BookEntity)

    @Delete
    suspend fun deleteBook(book: BookEntity)

    @Transaction
    suspend fun seedIfEmpty() {
        if (getAllOnce().isNotEmpty()) return

        insertBook(
            BookEntity(
                title = "Основы алгоритмов и структур данных",
                author = "Student Library",
                description = "Русскоязычное учебное пособие для студентов по алгоритмическому мышлению, структурам данных и оценке сложности.",
                category = "IT",
                coverUrl = "https://covers.openlibrary.org/b/id/10521270-L.jpg",
                textAssetPath = "books/algorithms-student-guide.txt",
                pdfUrl = "asset://books/algorithms-student-guide.txt",
            ),
        )
        insertBook(
            BookEntity(
                title = "Математический анализ: базовый курс студента",
                author = "Student Library",
                description = "Полноценный вводный курс по пределам, производным, интегралам и рядам на русском языке.",
                category = "Math",
                coverUrl = "https://covers.openlibrary.org/b/id/10130638-L.jpg",
                textAssetPath = "books/calculus-student-guide.txt",
                pdfUrl = "asset://books/calculus-student-guide.txt",
            ),
        )
        insertBook(
            BookEntity(
                title = "Академическое письмо и работа с источниками",
                author = "Student Library",
                description = "Практическая книга для студентов о конспектировании, поиске литературы, цитировании и написании курсовых работ.",
                category = "Skills",
                coverUrl = "https://covers.openlibrary.org/b/id/10909258-L.jpg",
                textAssetPath = "books/academic-writing-student-guide.txt",
                pdfUrl = "asset://books/academic-writing-student-guide.txt",
            ),
        )
    }

    @Query("SELECT * FROM books ORDER BY title COLLATE NOCASE ASC")
    fun observeAll(): Flow<List<BookEntity>>

    @Query(
        """
        SELECT * FROM books
        WHERE
            (:q IS NULL OR :q = '' OR title LIKE '%' || :q || '%' OR author LIKE '%' || :q || '%')
            AND (:category IS NULL OR :category = '' OR category = :category)
        ORDER BY title COLLATE NOCASE ASC
        """,
    )
    fun observeSearch(q: String?, category: String?): Flow<List<BookEntity>>

    @Query("SELECT * FROM books WHERE isFavorite = 1 ORDER BY title COLLATE NOCASE ASC")
    fun observeFavorites(): Flow<List<BookEntity>>

    @Query("SELECT * FROM books WHERE id = :id")
    fun observeBook(id: Long): Flow<BookEntity?>

    @Query("SELECT * FROM books WHERE id = :id")
    suspend fun getBookOnce(id: Long): BookEntity?

    @Query("SELECT * FROM books")
    suspend fun getAllOnce(): List<BookEntity>

    @Query("UPDATE books SET isFavorite = :favorite WHERE id = :id")
    suspend fun setFavorite(id: Long, favorite: Boolean)
}
