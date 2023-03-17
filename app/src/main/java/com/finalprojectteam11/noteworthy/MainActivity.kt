package com.finalprojectteam11.noteworthy

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.compose.rememberNavController
import com.finalprojectteam11.noteworthy.ui.theme.MyApplicationTheme

sealed class Destination(val route: String){
    object Home: Destination("home")
    object Settings: Destination("settings")
    object AddNote: Destination("addNote")
}
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyApp {
                MainScreen()
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

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    MyApp {
        MainScreen()
    }
}
//class MainActivity : ComponentActivity() {
//    var currentDisplayChoice by mutableStateOf(false)
//    var searchQuery by mutableStateOf("")
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContent {
//            val navController = rememberNavController()
//            NavigationAppHost(navController = navController)
//            MyApplicationTheme {
//                Scaffold(
//                    topBar = {
//                        TopAppBar(
//                            title = { Text(text = "Noteworthy") },
//                            navigationIcon = {
//                                IconButton(onClick = { /*TODO*/ },) {
//                                    Icon(Icons.Filled.Menu, contentDescription = "Menu", modifier = Modifier.padding(14.dp))
//                                }
//                            },
//                            backgroundColor = Color(0xFF3694C9),
//                            contentColor = Color.White,
//                        )
//                    },
//                    bottomBar = {
//                        BottomAppBar(
//                            cutoutShape = CircleShape,
//                            contentPadding = PaddingValues(48.dp, 8.dp),
//                            contentColor = Color(0xFF3694C9),
//                            backgroundColor = Color(0xFFFFFFFF),
//                            modifier = Modifier.clip(
//                                RoundedCornerShape(25.dp, 25.dp, 0.dp, 0.dp))
//                        ) {
//                            Box(modifier = Modifier.clickable { navController.navigate(Destination.Home.route) }) {
//                                IconToggleButton(checked = true, onCheckedChange = {}) {
//                                    if (true) {
//                                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
//                                            Icon(Icons.Filled.Home, contentDescription = "Home")
//                                            Text(text = "Home")
//                                        }
//                                    } else {
//                                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
//                                            Icon(
//                                                Icons.Filled.Home,
//                                                contentDescription = "Home",
//                                                tint = Color.Gray
//                                            )
//                                            Text(text = "Home", color = Color.Gray)
//                                        }
//                                    }
//                                }
//                            }
//                            Spacer(modifier = Modifier.weight(1f))
//                            Box(modifier = Modifier.clickable { navController.navigate(Destination.Settings.route) }) {
//                                IconToggleButton(checked = false, onCheckedChange = {}) {
//                                    if (false) {
//                                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
//                                            Icon(
//                                                Icons.Filled.Settings,
//                                                contentDescription = "Settings"
//                                            )
//                                            Text(text = "Settings")
//                                        }
//                                    } else {
//                                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
//                                            Icon(
//                                                Icons.Filled.Settings,
//                                                contentDescription = "Settings",
//                                                tint = Color.Gray
//                                            )
//                                            Text(text = "Settings", color = Color.Gray)
//                                        }
//                                    }
//                                }
//                            }
//
//                        }
//                    },
//                    floatingActionButtonPosition = FabPosition.Center,
//                    isFloatingActionButtonDocked = true,
//                    floatingActionButton = { FloatingButtons() },
//                    backgroundColor = Color(0xFFEFEFEF),
//                ) { innerPadding ->
//                    Surface(
//                        modifier = Modifier
//                            .padding(innerPadding),
//                        color = Color(0xFFEFEFEF),
//                    ) {
//                        LazyColumn(
//                            modifier = Modifier
//                                .fillMaxWidth(),
//                            verticalArrangement = Arrangement.spacedBy(16.dp)
//                        ) {
//                            item {
//                                SearchBox()
//                            }
//                            item {
//                                filterSection()
//                            }
//                            item {
//                                displayChoice()
//                            }
//                            item {
//                                recentNotes()
//                            }
//                            item {
//                                allNotes()
//                            }
//                            item {
//                                Spacer(modifier = Modifier.height(50.dp))
//                            }
//                        }
//                    }
//                }
//            }
//        }
//    }
//@Composable
//fun NavigationAppHost(navController: NavHostController){
//    NavHost(navController = navController, startDestination = "home"){
//        composable(Destination.Settings.route){ SettingsScreen(navController) }
//        composable(Destination.AddNote.route){NoteScreen(navController)}
//        composable(Destination.Home.route){MainActivity()}
//    }
//}


