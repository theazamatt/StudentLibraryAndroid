package com.example.studentlibrary

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.studentlibrary.ui.AppRoot
import com.example.studentlibrary.ui.theme.StudentLibraryTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            StudentLibraryTheme {
                AppRoot()
            }
        }
    }
}

