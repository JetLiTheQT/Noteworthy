package com.finalprojectteam11.noteworthy.ui

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyRow
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.*
import androidx.navigation.compose.currentBackStackEntryAsState
import com.finalprojectteam11.noteworthy.R
import com.finalprojectteam11.noteworthy.data.Note

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object AddNote : Screen("add_note")
    object Settings : Screen("settings")
    object EditNote : Screen("edit_note/{note_id}")
}
@Composable
fun AppNavigator(navController: NavHostController) {
    NavHost(navController = navController, startDestination = Screen.Home.route) {
        composable(Screen.Home.route) { HomeScreen(navController) }
        composable(Screen.AddNote.route) { NoteScreen(navController, "")  }
        composable(Screen.Settings.route) { SettingsScreen(navController) }
        composable(
            Screen.EditNote.route,
            arguments = listOf(
                navArgument("note_id") {
                    type = NavType.StringType
                },
            )
        ) { backStackEntry ->
            NoteScreen(navController, backStackEntry.arguments?.getString("note_id"))

        }
    }
}

fun getTitleForScreen(screen: Screen): String {
    return when (screen) {
        is Screen.Home -> "Noteworthy"
        is Screen.AddNote -> "Add New Note"
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

    val firestoreViewModel = FirestoreViewModel()
    firestoreViewModel.getNotes()
    firestoreViewModel.getPinnedNotes()

    val notesList = remember { mutableStateListOf<Note>() }
    val pinnedNotesList = remember { mutableStateListOf<Note>() }

    firestoreViewModel.noteResults.observeForever() {
        if (it != null) {
            notesList.clear()

            for (document in it) {
                val note = Note(
                    title = if (document.data!!["title"] == null) "" else document.data!!["title"].toString(),
                    content = if (document.data!!["content"] == null) "" else document.data!!["content"].toString(),
                    time = if (document.data!!["time"] == null) "" else document.data!!["time"].toString(),
                    id = document.id,
                    pinned = if (document.data!!["pinned"] == null) false else document.data!!["pinned"] as Boolean,
                )
                notesList.add(note)
            }
        }
    }

    firestoreViewModel.pinnedNoteResults.observeForever() {
        if (it != null) {
            pinnedNotesList.clear()

            for (document in it) {
                val note = Note(
                    title = if (document.data!!["title"] == null) "" else document.data!!["title"].toString(),
                    content = if (document.data!!["content"] == null) "" else document.data!!["content"].toString(),
                    time = if (document.data!!["time"] == null) "" else document.data!!["time"].toString(),
                    id = document.id,
                    pinned = if (document.data!!["pinned"] == null) false else document.data!!["pinned"] as Boolean,
                )
                pinnedNotesList.add(note)
            }
        }
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .padding(0.dp),
        backgroundColor = Color.Transparent,
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
                    backgroundColor = Color.Transparent,
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(0.dp),
                    elevation = 0.dp // Remove elevation to avoid multiple layers
                ) {
                    BottomNavigation(
                        modifier = Modifier
                            .fillMaxWidth()
                            .shadow(0.dp),
                        contentColor = Color(0xFF3694C9),
                        backgroundColor = Color(0xFFFFFFFF),
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
            FloatingActionButton(navController)
            }else{}
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
                            pinnedNotes(currentDisplayChoice.value, pinnedNotesList)
                        }
                        item {
                            allNotes(currentDisplayChoice.value, notesList, navController)
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
fun pinnedNotes(currentDisplayChoice: Boolean, notesList: SnapshotStateList<Note>) {
    Column(modifier = Modifier
        .padding(top = 16.dp, bottom = 0.dp, start = 0.dp, end = 0.dp)
        .fillMaxWidth()) {
        Text(
            text = "Pinned Notes",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(start = 16.dp, end = 16.dp)
        )

        if (notesList.isEmpty()) {
            Text(
                text = "No pinned notes.\nPress and hold on a note to pin it.",
                fontSize = 18.sp,
                fontWeight = FontWeight.Normal,
                modifier = Modifier.padding(horizontal = 32.dp, vertical = 16.dp)
            )
        } else {

            if (!currentDisplayChoice) {
                LazyRow(modifier = Modifier
                    .padding(top = 16.dp)
                    .fillMaxWidth(),
                    contentPadding = PaddingValues(16.dp, 0.dp, 16.dp, 0.dp),
                    content = {
//                    items(notesList.size) {
//                        NoteCard(notesList[it])
//                    }
                    })
            } else {
                Column(content = {
//                NoteListItem()
//                Divider(color = Color.LightGray)
//                NoteListItem()
//                Divider(color = Color.LightGray)
//                NoteListItem()
                })
            }
        }
    }
}

@Composable
fun allNotes(currentDisplayChoice: Boolean, notesList: SnapshotStateList<Note>, navController: NavController) {

    Column(
        modifier = Modifier
            .padding(top = 16.dp, bottom = 0.dp, start = 0.dp, end = 0.dp)
            .fillMaxWidth()
    ) {
        Text(
            text = "All Notes",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(start = 16.dp, end = 16.dp)
        )

        if (notesList.isEmpty()) {
            Text(
                text = "No notes found.\nPress the pencil button to add a note.",
                fontSize = 18.sp,
                fontWeight = FontWeight.Normal,
                modifier = Modifier.padding(horizontal = 32.dp, vertical = 16.dp)
            )
        } else {
            if (!currentDisplayChoice) {
                LazyRow(modifier = Modifier
                    .padding(top = 16.dp)
                    .fillMaxWidth(),
                    contentPadding = PaddingValues(16.dp, 0.dp, 16.dp, 0.dp),
                    content = {
                        items(notesList.size) {
                            NoteCard(notesList[it], navController)
                        }
                    }
                )
            } else {
                Column(content = {

                    for (note in notesList) {
                        NoteListItem(note)
                        if (note != notesList.last()) {
                            Divider(color = Color.LightGray)
                        }
                    }
                })
            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class, ExperimentalFoundationApi::class)
@Composable
fun NoteCard(note: Note, navController: NavController) {
        Card(
            modifier = Modifier
                .padding(start = 8.dp, end = 8.dp)
                .width(200.dp)
                .height(200.dp)
                .combinedClickable(
                    onClick = {
                        Log.d("NoteCard", "Clicked on note: " + note.id)
                        navController.navigate("edit_note/" + note.id)
                    },
                    onLongClick = {
                       // Show action menu
                    }
                ),
            shape = RoundedCornerShape(20.dp),
            backgroundColor = Color(0xFFE5E5E5),
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = if (note.title == "") "Untitled" else note.title,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = if (note.content.length > 50) note.content.substring(0, 50) + "..." else note.content,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Normal
                )
            }
        }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun NoteListItem(note: Note) {
    ListItem(
        text = {
            Text(
                text = if (note.title == "") "Untitled" else note.title,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        },
        secondaryText = {
            Text(
                text = if (note.content.length > 50) note.content.substring(0, 50) + "..." else note.content,
                fontSize = 16.sp,
                fontWeight = FontWeight.Normal
            )
        },
        modifier = Modifier
            .padding(14.dp)
    )
}

@Composable
fun Greeting(name: String) {
    Text(text = "Hello $name!")
}



