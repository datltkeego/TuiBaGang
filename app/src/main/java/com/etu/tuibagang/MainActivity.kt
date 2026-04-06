package com.etu.tuibagang

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.etu.tuibagang.ui.AppRoot
import com.etu.tuibagang.ui.theme.TuiBaGangTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TuiBaGangTheme {
                AppRoot()
            }
        }
    }
}
