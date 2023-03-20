package com.finalprojectteam11.noteworthy.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.finalprojectteam11.noteworthy.R
import com.finalprojectteam11.noteworthy.data.Note
import com.google.firebase.FirebaseApp

sealed class Destination(val route: String){
    object Home: Destination("home")
    object Settings: Destination("settings")
    object AddNote: Destination("addNote")
}
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)
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

@SuppressLint("UnusedMaterialScaffoldPaddingParameter", "UnrememberedMutableState")
@Composable
fun MainScreen() {
    val navController = rememberNavController()
    val items = listOf(Screen.Home, Screen.AddNote, Screen.Settings)
    val currentScreen = navController.currentBackStackEntryAsState()
    val currentDisplayChoice = remember { mutableStateOf(false) }
    var searchQuery = mutableStateOf("")
    // Get the current screen and its title
    items.find { it.route == currentScreen.value?.destination?.route }
    val snackbarHostState = remember { SnackbarHostState() }

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
        topBar = { TopNavBar(navController = navController) },
        bottomBar = { BottomNavBar(navController = navController) },
        floatingActionButton = { FloatingActionButton(navController) },
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
                            SearchBox(
                                searchQuery = searchQuery,
                                onSearchQueryChange = {
                                    searchQuery.value = it
                                },
                                snackbarHostState,
                                navController
                            )
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
                // Position the SnackbarHost at the top
                SnackbarHost(
                    hostState = snackbarHostState,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(top = 1.dp)
                )
            }
        },
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
fun SearchBox(searchQuery: MutableState<String>, onSearchQueryChange: (String) -> Unit, snackbarHostState: SnackbarHostState, navController: NavController) {
    val focusRequester = remember { FocusRequester() }

    TextField(
        singleLine = true,
        value = searchQuery.value,
        onValueChange = onSearchQueryChange,
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
        keyboardActions = KeyboardActions(onSearch = {
            navController.navigate("search/" + searchQuery.value)
            focusRequester.freeFocus()
        }),
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
            .height(56.dp)
            .clip(RoundedCornerShape(10.dp))
            .focusRequester(focusRequester),
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


