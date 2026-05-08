package com.example.studentlibrary.data

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "books",
    indices = [
        Index(value = ["title"]),
        Index(value = ["author"]),
        Index(value = ["category"]),
        Index(value = ["isFavorite"]),
    ],
)
data class BookEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val author: String,
    val description: String,
    val category: String,
    val coverUrl: String,
    val textAssetPath: String,
    val content: String = "",
    val pdfUrl: String,
    val isFavorite: Boolean = false,
)
