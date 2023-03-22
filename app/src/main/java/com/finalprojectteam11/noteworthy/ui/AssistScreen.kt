package com.finalprojectteam11.noteworthy.ui

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun AssistScreen(navController: NavHostController, title: String?, description: String?, response: String?) {

    Scaffold {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
           if (title != null && title != "None") {
                 Text(
                     text = title,
                     style = MaterialTheme.typography.h5,
                        fontWeight = FontWeight.Bold
                 )
                Spacer(modifier = Modifier.height(16.dp))
           }

            if (description != null && description != "None") {
                Text(
                    text = description,
                    style = MaterialTheme.typography.body1
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            if (response != null && response != "None") {
                Text(
                    text = response,
                    style = MaterialTheme.typography.body1
                )
            }




        }
    }

}