package com.sb1703.geminiaichatbotcmp

import App
import AppContent
import HomeViewModel
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            AppContent(
                viewModel = HomeViewModel()
            )
        }
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
//    App()
}