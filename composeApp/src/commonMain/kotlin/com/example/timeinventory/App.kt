package com.example.timeinventory

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import com.example.timeinventory.ui.MainScreen
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun App() {
    MaterialTheme {
        MainScreen()
    }
}