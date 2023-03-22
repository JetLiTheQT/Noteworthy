package com.finalprojectteam11.noteworthy.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.*
import androidx.compose.runtime.*
import com.google.firebase.FirebaseApp
import com.finalprojectteam11.noteworthy.ui.theme.AppTheme
import com.finalprojectteam11.noteworthy.data.AppSettings


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)
        AppSettings.init(this)
        setContent {
            AppTheme {
                MyApp {
                    MainScreen()
                }
            }
        }
    }
}

@Composable
fun MyApp(content: @Composable () -> Unit) {
    Surface {
        content()
    }
}