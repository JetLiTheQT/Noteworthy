package com.finalprojectteam11.noteworthy.ui

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
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
fun ContactScreen(navController: NavHostController) {
    val contactList = listOf(
        Contact(name = "Carter Roeser - API Integration", email = "roeserc@oregonstate.edu"),
        Contact(name = "Ekkachai Jet Ittihrit - UI/UX",email = "ittihrie@oregonstate.edu"),
        Contact(name = "Kristina Marquez - UI/UX",email = "marquekr@oregonstate.edu")
    )

    Scaffold(
        content = {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxSize()
            ) {
                LazyColumn {
                    itemsIndexed(contactList) { index, contact ->
                        val gradient = remember {
                            when (index % 3) {
                                0 -> Brush.linearGradient(
                                    colors = listOf(
                                        Color.Blue.copy(alpha = 0.6f),
                                        Color.Green.copy(alpha = 0.6f)
                                    )
                                )
                                1 -> Brush.linearGradient(
                                    colors = listOf(
                                        Color.Magenta.copy(alpha = 0.6f),
                                        Color.Yellow.copy(alpha = 0.6f)
                                    )
                                )
                                else -> Brush.linearGradient(
                                    colors = listOf(
                                        Color.Red.copy(alpha = 0.6f),
                                        Color.Magenta.copy(alpha = 0.6f)
                                    )
                                )
                            }
                        }
                        ContactCard(contact = contact, gradient = gradient)
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            }
        }
    )
}

@Composable
fun ContactCard(contact: Contact, gradient: Brush) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .background(gradient)
            .clip(RoundedCornerShape(8.dp))
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = contact.name,
                style = MaterialTheme.typography.h6,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Text(
                text = contact.email,
                style = MaterialTheme.typography.body1,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }
    }
}


data class Contact(
    val name: String,
    val email: String
)


@Composable
fun SupportOptionsUI(navController: NavHostController) {
    Column(modifier = Modifier
        .padding(top = 10.dp)){
        Text(
            text = "Support",
            style = MaterialTheme.typography.h6,
            modifier = Modifier.padding(vertical = 8.dp)
        )
        SupportItem(navController)
    }
}
@Composable
fun SupportItem(navController: NavHostController){
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp)
            .clickable { navController.navigate(Screen.Contact.route) }
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
                            painter = painterResource(id = R.drawable.ic_support),
                            contentDescription = "Support",
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(14.dp))
                    Text(
                        text = "Contact Us"
                    )
                }
            }
        }
    )
}



