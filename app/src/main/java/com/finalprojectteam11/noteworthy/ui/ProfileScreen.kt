package com.finalprojectteam11.noteworthy.ui

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.finalprojectteam11.noteworthy.R
import com.finalprojectteam11.noteworthy.ui.theme.AppTheme
import com.finalprojectteam11.noteworthy.ui.theme.Shapes
import com.google.android.material.color.MaterialColors

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun ProfileScreen(navController: NavHostController) {

    Scaffold {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {

            ProfileCard()

            Spacer(modifier = Modifier.height(16.dp))


        }
    }

}
@Composable
fun ProfileCard(){
    Text(
        text = "Account Info",
        style = MaterialTheme.typography.h6,
        modifier = Modifier.padding(bottom = 8.dp)
    )
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(150.dp)
            .padding(10.dp),
        backgroundColor = MaterialTheme.colors.surface,
        elevation = 0.dp,
        shape = Shapes.large
    ){
        Row(
            modifier = Modifier.padding(20.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ){
            Column{
                Text(
                    text = "Name: John Doe",
                    style = MaterialTheme.typography.body1,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Text(
                    text = "Email: JohnDoe@example.com",
                    style = MaterialTheme.typography.body1,
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
