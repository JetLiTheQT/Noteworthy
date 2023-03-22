package com.finalprojectteam11.noteworthy.data

import android.content.Intent
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
data class AssistAction(
    val title: String = "",
    val description: String = "",
    val response: String = "",
    var category: String = "None",
    val url: String = "",
    val term : String = "",
    val location: String = "",
    val hour: Int = 0,
    val minute: Int = 0,
    @Transient
    var intent: Intent? = null
)