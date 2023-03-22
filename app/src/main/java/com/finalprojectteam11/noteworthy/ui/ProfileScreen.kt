package com.finalprojectteam11.noteworthy.ui

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.finalprojectteam11.noteworthy.R
import com.finalprojectteam11.noteworthy.data.Note
import com.finalprojectteam11.noteworthy.ui.theme.AppTheme
import com.finalprojectteam11.noteworthy.ui.theme.Shapes
import com.google.android.material.color.MaterialColors


@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun ProfileScreen(navController: NavHostController, firestoreViewModel: FirestoreViewModel) {
    val notesList = remember { mutableStateListOf<Note>() }
    firestoreViewModel.noteResults.observeForever {
        notesList.clear()
        if (it != null) {
            for (document in it) {
                val note = Note(
                    title = if (document.data!!["title"] == null) "" else document.data!!["title"].toString(),
                    content = if (document.data!!["content"] == null) "" else document.data!!["content"].toString(),
                    time = if (document.data!!["time"] == null) "" else document.data!!["time"].toString(),
                    id = document.id,
                    pinned = if (document.data!!["pinned"] == null) false else document.data!!["pinned"] as Boolean,
                    private = if (document.data!!["private"] == null) false else document.data!!["private"] as Boolean,
                )
                notesList.add(note)
            }
        }
    }
    Scaffold {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            val gradient = Brush.linearGradient(
                colors = listOf(
                    Color.DarkGray.copy(alpha = 0.6f),
                    Color.Red.copy(alpha = 0.6f)
                ))
            ProfileCard(gradient)

            Spacer(modifier = Modifier.height(16.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                backgroundColor = MaterialTheme.colors.surface,
                elevation = 4.dp,
            ) {

                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(
                        text = "Total Notes",
                        style = MaterialTheme.typography.h6,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    Text(
                        text = notesList.size.toString(),
                        style = MaterialTheme.typography.h1,
                        color = MaterialTheme.colors.primary,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = { /* TODO Navigate to the login screen if implemented for now we redirect to home*/navController.navigate((Screen.Home.route)) },
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Text(text = "Log out")
            }
        }
    }

}
@Composable
fun ProfileCard(gradient: Brush) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        shape = RoundedCornerShape(8.dp),
        backgroundColor = MaterialTheme.colors.surface,
        elevation = 9.dp,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                Text(
                    text = "John Doe",
                    style = MaterialTheme.typography.h6,
                    modifier = Modifier.padding(bottom = 8.dp)

                )

                Text(
                    text = "JohnDoe@email.com",
                    style = MaterialTheme.typography.h6,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }
        }
    }
}



@Composable
fun GeneralOptionsUI(navController: NavHostController) {
    Column{
        Text(
            text = "General",
            style = MaterialTheme.typography.h6,
            modifier = Modifier.padding(vertical = 8.dp)
        )
        GeneralItem(navController)
    }
}
@Composable
fun GeneralItem(navController: NavHostController){
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp)
            .clickable { navController.navigate(Screen.Profile.route) }
        ,
        content = {
            Row(
                modifier = Modifier.padding(vertical = 10.dp, horizontal = 16.dp),
                verticalAlignment = CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ){
                Row(verticalAlignment = Alignment.CenterVertically){
                    Box(modifier= Modifier
                        .size(34.dp)
                        .clip(shape = Shapes.medium)
                    ){
                        Icon(
                            painter = painterResource(id = R.drawable.ic_profile),
                            contentDescription = "General",
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(14.dp))
                    Text(
                        text = "View Profile"
                    )
                }
            }
        }
    )
}
