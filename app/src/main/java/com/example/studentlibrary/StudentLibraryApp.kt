package com.example.studentlibrary

import android.app.Application
import com.example.studentlibrary.data.LibraryRepository
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltAndroidApp
class StudentLibraryApp : Application() {
    @Inject lateinit var repository: LibraryRepository

    override fun onCreate() {
        super.onCreate()

        // Автозаполнение БД при первом запуске (проверка "пусто/не пусто" внутри).
        CoroutineScope(Dispatchers.IO).launch {
            repository.seedIfEmpty()
        }
    }
}

