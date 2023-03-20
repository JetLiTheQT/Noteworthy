package com.finalprojectteam11.noteworthy.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController

@Composable
fun HomeScreen(navController: NavController) {
    Box(modifier = Modifier.fillMaxSize()) {
//        Text(text = "Home")
    }
}

@Composable
fun AddNoteScreen(navController: NavController, noteId: String = "") {
    NoteScreen(navController, noteId)
}

@Composable
fun SettingsScreen(navController: NavController) {
    SettingsScreen(navController)
}