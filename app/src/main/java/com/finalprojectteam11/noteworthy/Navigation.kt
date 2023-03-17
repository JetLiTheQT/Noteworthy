package com.finalprojectteam11.noteworthy

import android.annotation.SuppressLint
import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.SnackbarDefaults.backgroundColor
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState



sealed class Screen(val route: String) {
    object Home : Screen("home")
    object AddNote : Screen("add_note")
    object Settings : Screen("settings")
}
@Composable
fun AppNavigator(navController: NavHostController) {
    NavHost(navController = navController, startDestination = Screen.Home.route) {
        composable(Screen.Home.route) { HomeScreen(navController) }
        composable(Screen.AddNote.route) { NoteScreen(navController) }
        composable(Screen.Settings.route) { SettingsScreen(navController) }
    }
}

fun getTitleForScreen(screen: Screen): String {
    return when (screen) {
        is Screen.Home -> "Home"
        is Screen.AddNote -> "Add Note"
        is Screen.Settings -> "Settings"
        else -> {""}
    }
}

@SuppressLint("UnusedMaterialScaffoldPaddingParameter", "UnrememberedMutableState")
@Composable
fun MainScreen() {
    val navController = rememberNavController()
    val items = listOf(Screen.Home, Screen.AddNote, Screen.Settings)
    val currentScreen = navController.currentBackStackEntryAsState()
    val currentDisplayChoice = remember { mutableStateOf(false) }
    var searchQuery by mutableStateOf("")
    // Get the current screen and its title
    val currentRoute = currentScreen.value?.destination?.route
    val currentSelectedScreen = items.find { it.route == currentRoute }
    val title = currentSelectedScreen?.let { getTitleForScreen(it) } ?: ""
    val isChildScreen = currentRoute != Screen.Home.route // Check if current screen is a child screen

    Scaffold(
        topBar = {
            if (!isChildScreen) { // Display regular TopAppBar for non-child screens
                TopAppBar(
                    title = { Text(title) },
                    backgroundColor = Color(0xFF3694C9),
                    contentColor = Color.White
                )
            } else { // Display empty TopAppBar for child screens
            }
        },
        bottomBar = {
            if (!isChildScreen) { // Display regular TopAppBar for non-child screens
                BottomAppBar(
                    cutoutShape = CircleShape,
                    contentColor = Color(0xFF3694C9),
                    backgroundColor = Color(0xFFFFFFFF),
                    modifier = Modifier.clip(
                        RoundedCornerShape(25.dp, 25.dp, 0.dp, 0.dp))
                ) {
                    BottomNavigation(
                        modifier = Modifier.fillMaxWidth(),
                        contentColor = Color(0xFF3694C9),
                        backgroundColor = Color.Transparent,
                        elevation = 0.dp // Remove elevation to avoid multiple layers
                    ) {
                        items.forEach { screen ->
                            if (screen is Screen.AddNote) return@forEach
                            BottomNavigationItem(
                                icon = {
                                    when (screen) {
                                        is Screen.Home -> Icon(
                                            Icons.Filled.Home,
                                            contentDescription = "Home"
                                        )
                                        is Screen.Settings -> Icon(
                                            Icons.Filled.Settings,
                                            contentDescription = "Settings"
                                        )
                                        else -> Unit
                                    }
                                },
                                label = {
                                    Text(
                                        text = when (screen) {
                                            is Screen.Home -> "Home"
                                            is Screen.Settings -> "Settings"
                                            else -> ""
                                        }
                                    )
                                },
                                selected = currentScreen.value?.destination?.route == screen.route,
                                onClick = {
                                    navController.navigate(screen.route) {
                                        popUpTo(navController.graph.startDestinationId)
                                        launchSingleTop = true
                                    }
                                },
                                selectedContentColor = Color.Black,
                                unselectedContentColor = Color.Gray,
                            )
                        }
                    }
                }
            }
             else { // Display empty TopAppBar for child screens
            } },
        floatingActionButton = {
            if (!isChildScreen) {
            FloatingActionButton(navController)}else{}
        },
        floatingActionButtonPosition = FabPosition.Center,
        isFloatingActionButtonDocked = true,
        content = { innerPadding ->
            Box(modifier = Modifier.padding(innerPadding)) {
                Surface(
                    modifier = Modifier
                        .fillMaxSize(),
                    color = Color(0xFFEFEFEF),
                ) {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        item {
                            SearchBox(searchQuery = searchQuery, onSearchQueryChange = { searchQuery = it })
                        }
                        item {
                            filterSection()
                        }
                        item {
                            displayChoice(onDisplayChoiceChange = { newDisplayChoice ->
                                currentDisplayChoice.value = newDisplayChoice
                            })
                        }
                        item {
                            recentNotes(currentDisplayChoice.value)
                        }
                        item {
                            allNotes(currentDisplayChoice.value)
                        }
                        item {
                            Spacer(modifier = Modifier.height(50.dp))
                        }
                    }
                }
                AppNavigator(navController)
            }
        },
    )
}

@Composable
fun FloatingActionButton(navController: NavController){
    var context = LocalContext.current
    FloatingActionButton(
        shape = CircleShape,
        onClick = {
            navController.navigate(Screen.AddNote.route) {
                popUpTo(navController.graph.startDestinationId)
                launchSingleTop = true
            }
        },
        backgroundColor = Color(0xFF3694C9),
        contentColor = Color.White,
        modifier = Modifier
            .size(72.dp)
    ) {
        Icon(
            imageVector = ImageVector.vectorResource(id = R.drawable.edit_fill1_wght400_grad0_opsz48),
            contentDescription = "Add Note",
            modifier = Modifier
                .padding(18.dp)
        )
    }

}

@Composable
fun SearchBox(searchQuery: String, onSearchQueryChange: (String) -> Unit) {
    TextField(
        value = searchQuery,
        onValueChange = onSearchQueryChange,
        label = { Text(text = "Search") },
        leadingIcon = {
            Icon(
                imageVector = Icons.Filled.Search,
                contentDescription = "Search",
                tint = Color.Black
            )
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp, bottom = 0.dp, start = 16.dp, end = 16.dp)
            .height(48.dp)
            .clip(RoundedCornerShape(10.dp)),
        colors = TextFieldDefaults.textFieldColors(
            focusedLabelColor = Color.Black,
            cursorColor = Color.Black,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent,
            backgroundColor = Color(0xFFE5E5E5),
            unfocusedLabelColor = Color.Black,
        )
    )
}
@Composable
fun FilterButton(text: String) {
    Button(
        onClick = { /*TODO*/ },
        modifier = Modifier
            .padding(start = 8.dp, end = 4.dp)
            .height(38.dp),
        shape = RoundedCornerShape(20.dp),
        colors = ButtonDefaults.buttonColors(
            backgroundColor = Color(0xFFE5E5E5),
            contentColor = Color.Black
        )
    ) {
        Text(text = text)
    }
}

@Composable
fun filterSection() {
    Column(modifier = Modifier
        .padding(top = 16.dp, bottom = 0.dp, start = 0.dp, end = 0.dp)
        .fillMaxWidth()) {
        Text(text = "Filter", fontSize = 20.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(start = 16.dp, end = 16.dp))
        LazyRow(modifier = Modifier
            .padding(top = 16.dp)
            .fillMaxWidth(),
            contentPadding = PaddingValues(16.dp, 0.dp, 16.dp, 0.dp),
            content = {
                item {
                    FilterButton(text = "AI Assist")
                }
                item {
                    FilterButton(text = "Private")
                }
                item {
                    FilterButton(text = "New")
                }
                item {
                    FilterButton(text = "Location")
                }

            })
    }
}

@Composable
fun displayChoice(onDisplayChoiceChange: (Boolean) -> Unit) {
    Row(modifier = Modifier.padding(top = 16.dp, bottom = 0.dp, start = 16.dp, end = 0.dp)) {
        IconButton(onClick = {
            onDisplayChoiceChange(false)
        }) {
            Icon(imageVector = ImageVector.vectorResource(id = R.drawable.grid_view_fill1_wght400_grad0_opsz48), contentDescription = null, modifier = Modifier.size(24.dp))
        }
        IconButton(onClick = { onDisplayChoiceChange(true) }) {
            Icon(Icons.Filled.List, contentDescription = null)
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun recentNotes(currentDisplayChoice: Boolean) {
    Column(modifier = Modifier
        .padding(top = 16.dp, bottom = 0.dp, start = 0.dp, end = 0.dp)
        .fillMaxWidth()) {
        Text(text = "Recent Notes", fontSize = 20.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(start = 16.dp, end = 16.dp))

        if (!currentDisplayChoice) {
            LazyRow(modifier = Modifier
                .padding(top = 16.dp)
                .fillMaxWidth(),
                contentPadding = PaddingValues(16.dp, 0.dp, 16.dp, 0.dp),
                content = {
                    items(5) {
                        if (it == 1 || it == 2) {
                            NoteImageCard()
                        } else {
                            NoteCard()
                        }
                    }
                })
        } else {
            Column(content = {
                NoteListItem()
                Divider(color = Color.LightGray)
                NoteListItem()
                Divider(color = Color.LightGray)
                NoteListItem()
            })
        }
    }
}

@Composable
fun allNotes(currentDisplayChoice: Boolean) {
    Column(modifier = Modifier
        .padding(top = 16.dp, bottom = 0.dp, start = 0.dp, end = 0.dp)
        .fillMaxWidth()) {
        Text(text = "All Notes", fontSize = 20.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(start = 16.dp, end = 16.dp))
        if (!currentDisplayChoice) {
            LazyRow(modifier = Modifier
                .padding(top = 16.dp)
                .fillMaxWidth(),
                contentPadding = PaddingValues(16.dp, 0.dp, 16.dp, 0.dp),
                content = {
                    items(10) {
                        if (it == 1 || it == 2) {
                            NoteImageCard()
                        } else {
                            NoteCard()
                        }
                    }
                })
        } else {
            Column(content = {
                NoteListItem()
                Divider(color = Color.LightGray)
                NoteListItem()
                Divider(color = Color.LightGray)
                NoteListItem()
                Divider(color = Color.LightGray)
                NoteListItem()
                Divider(color = Color.LightGray)
                NoteListItem()
                Divider(color = Color.LightGray)
                NoteListItem()
                Divider(color = Color.LightGray)
                NoteListItem()
            })
        }
    }
}

@Composable
fun NoteImageCard() {
    Card(
        modifier = Modifier
            .padding(start = 8.dp, end = 8.dp)
            .width(200.dp)
            .height(200.dp),
        shape = RoundedCornerShape(20.dp),
        backgroundColor = Color(0xFFE5E5E5)
    ) {
        Column(modifier = Modifier.padding(0.dp)) {
            // placeholder background image
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .background(Color(0xFFC5C5C5))
            )
            Column(modifier = Modifier.padding(14.dp)) {
                Text(text = "Title", fontSize = 24.sp, fontWeight = FontWeight.Bold)
                Text(text = "Description", fontSize = 16.sp, fontWeight = FontWeight.Normal)
            }
        }
    }
}

@Composable
fun NoteCard() {
    Card(
        modifier = Modifier
            .padding(start = 8.dp, end = 8.dp)
            .width(200.dp)
            .height(200.dp),
        shape = RoundedCornerShape(20.dp),
        backgroundColor = Color(0xFFE5E5E5)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(text = "Title", fontSize = 24.sp, fontWeight = FontWeight.Bold)
            Text(text = "Description", fontSize = 16.sp, fontWeight = FontWeight.Normal)
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun NoteListItem() {
    ListItem(
        text = {
            Text(text = "Title", fontSize = 20.sp, fontWeight = FontWeight.Bold)
        },
        secondaryText = {
            Text(text = "Description", fontSize = 16.sp, fontWeight = FontWeight.Normal)
        },
        modifier = Modifier
            .padding(14.dp)
    )
}

@Composable
fun Greeting(name: String) {
    Text(text = "Hello $name!")
}



