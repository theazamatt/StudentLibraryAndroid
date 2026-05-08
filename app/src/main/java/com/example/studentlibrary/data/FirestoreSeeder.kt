package com.example.studentlibrary.data

import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.tasks.await

object FirestoreSeeder {
    suspend fun seedTextbooks() {
        val db = Firebase.firestore
        val textbooks = listOf(
            mapOf(
                "title" to "Advanced Engineering Mathematics",
                "author" to "Erwin Kreyszig",
                "subject" to "Mathematics",
                "course" to 2,
                "publisher" to "Wiley",
                "year" to 2011,
                "description" to "A comprehensive guide to engineering mathematics.",
                "pdfUrl" to "https://example.com/math.pdf",
                "coverUrl" to "https://covers.openlibrary.org/b/id/8231856-L.jpg"
            ),
            mapOf(
                "title" to "Introduction to Algorithms",
                "author" to "Thomas H. Cormen",
                "subject" to "Computer Science",
                "course" to 1,
                "publisher" to "MIT Press",
                "year" to 2009,
                "description" to "The standard textbook for algorithm design and analysis.",
                "pdfUrl" to "https://example.com/algorithms.pdf",
                "coverUrl" to "https://covers.openlibrary.org/b/id/13101037-L.jpg"
            ),
            mapOf(
                "title" to "Physics for Scientists and Engineers",
                "author" to "Raymond A. Serway",
                "subject" to "Physics",
                "course" to 1,
                "publisher" to "Cengage Learning",
                "year" to 2018,
                "description" to "A detailed introduction to classical and modern physics.",
                "pdfUrl" to "https://example.com/physics.pdf",
                "coverUrl" to "https://covers.openlibrary.org/b/id/12543500-L.jpg"
            ),
            mapOf(
                "title" to "Organic Chemistry",
                "author" to "Paula Yurkanis Bruice",
                "subject" to "Chemistry",
                "course" to 2,
                "publisher" to "Pearson",
                "year" to 2016,
                "description" to "An essential guide to organic chemistry principles.",
                "pdfUrl" to "https://example.com/chemistry.pdf",
                "coverUrl" to "https://covers.openlibrary.org/b/id/10578642-L.jpg"
            ),
            mapOf(
                "title" to "Molecular Biology of the Cell",
                "author" to "Bruce Alberts",
                "subject" to "Biology",
                "course" to 3,
                "publisher" to "Garland Science",
                "year" to 2014,
                "description" to "The fundamental text for cell biology.",
                "pdfUrl" to "https://example.com/biology.pdf",
                "coverUrl" to "https://covers.openlibrary.org/b/id/11488195-L.jpg"
            ),
            mapOf(
                "title" to "Economics: Principles, Problems, and Policies",
                "author" to "Campbell R. McConnell",
                "subject" to "Economics",
                "course" to 1,
                "publisher" to "McGraw-Hill",
                "year" to 2020,
                "description" to "A leading introductory economics textbook.",
                "pdfUrl" to "https://example.com/economics.pdf",
                "coverUrl" to "https://covers.openlibrary.org/b/id/10185078-L.jpg"
            )
        )

        val collection = db.collection("textbooks")
        for (book in textbooks) {
            collection.add(book).await()
        }
    }
}
