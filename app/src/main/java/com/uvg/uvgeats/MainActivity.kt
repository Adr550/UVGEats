package com.uvg.uvgeats

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.uvg.uvgeats.ui.navigation.AppNavigation
import com.uvg.uvgeats.ui.theme.UVGeatsTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            UVGeatsTheme {
                AppNavigation()
            }
        }
    }
}