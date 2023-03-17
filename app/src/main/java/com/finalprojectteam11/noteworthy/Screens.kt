package com.finalprojectteam11.noteworthy

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavHostController

@Composable
fun HomeScreen(navController: NavController) {
    Box(modifier = Modifier.fillMaxSize()) {
//        Text(text = "Home")
    }
}

@Composable
fun AddNoteScreen(navController: NavController) {
    NoteScreen(navController)
}

@Composable
fun SettingsScreen(navController: NavController) {
    SettingsScreen(navController)
}