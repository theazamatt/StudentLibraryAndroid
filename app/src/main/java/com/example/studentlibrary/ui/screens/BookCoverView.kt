package com.example.studentlibrary.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.studentlibrary.data.BookEntity
import kotlin.math.abs

@Composable
fun BookCoverView(
    book: BookEntity,
    modifier: Modifier = Modifier
) {
    if (book.coverUrl.isNotBlank()) {
        // Трансформируем ссылку, если это Google Drive
        val finalUrl = GoogleDriveUrlTransformer.transform(book.coverUrl)
        
        AsyncImage(
            model = finalUrl,
            contentDescription = book.title,
            contentScale = ContentScale.Crop,
            placeholder = painterResource(android.R.drawable.ic_menu_gallery),
            error = painterResource(android.R.drawable.ic_menu_report_image),
            modifier = modifier
        )
    } else {
        GeneratedCover(book, modifier)
    }
}

@Composable
private fun GeneratedCover(book: BookEntity, modifier: Modifier) {
    val colors = listOf(
        Color(0xFF1A237E), // Deep Blue
        Color(0xFF004D40), // Teal
        Color(0xFF311B92), // Deep Purple
        Color(0xFF880E4F), // Pink/Wine
        Color(0xFF3E2723), // Brown
        Color(0xFF212121), // Grey
        Color(0xFFBF360C)  // Deep Orange
    )
    
    val colorIndex = abs(book.title.hashCode()) % colors.size
    val mainColor = colors[colorIndex]
    
    Box(
        modifier = modifier
            .background(
                Brush.verticalGradient(
                    colors = listOf(mainColor, mainColor.copy(alpha = 0.8f))
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = Icons.Default.AutoStories,
                contentDescription = null,
                tint = Color.White.copy(alpha = 0.6f),
                modifier = Modifier.size(32.dp)
            )
            
            Text(
                text = book.title,
                color = Color.White,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    lineHeight = 18.sp
                ),
                textAlign = TextAlign.Center,
                maxLines = 4,
                overflow = TextOverflow.Ellipsis
            )
            
            Text(
                text = book.author,
                color = Color.White.copy(alpha = 0.8f),
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Center,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
