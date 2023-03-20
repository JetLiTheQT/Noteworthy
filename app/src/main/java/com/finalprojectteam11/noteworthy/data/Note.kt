package com.finalprojectteam11.noteworthy.data

// note data type
data class Note(
    val title: String,
    val content: String,
    val time: String,
    val id: String = "",
    val pinned: Boolean = false,
    val categories: List<String> = listOf()
)