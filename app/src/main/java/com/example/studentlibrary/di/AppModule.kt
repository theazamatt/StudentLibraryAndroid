package com.example.studentlibrary.di

import android.content.Context
import androidx.room.Room
import com.example.studentlibrary.data.AppDatabase
import com.example.studentlibrary.data.LibraryDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context,
    ): AppDatabase =
        Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "student_library.db",
        ).fallbackToDestructiveMigration().build()

    @Provides
    fun provideDao(db: AppDatabase): LibraryDao = db.libraryDao()
}
