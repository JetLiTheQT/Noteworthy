package com.finalprojectteam11.noteworthy.ui

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Search
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
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.finalprojectteam11.noteworthy.R
import com.finalprojectteam11.noteworthy.data.Note

@Composable
// Dummy route for main screen
fun HomeScreen(navController: NavHostController) { }

@SuppressLint("UnusedMaterialScaffoldPaddingParameter", "UnrememberedMutableState")
@Composable
fun MainScreen() {
    val navController = rememberNavController()
    val items = listOf(Screen.Home, Screen.AddNote, Screen.Settings)
    val currentScreen = navController.currentBackStackEntryAsState()
    val currentDisplayChoice = remember { mutableStateOf(false) }
    val searchQuery = mutableStateOf("")
    // Get the current screen and its title
    items.find { it.route == currentScreen.value?.destination?.route }
    val snackbarHostState = remember { SnackbarHostState() }

    val sharedViewModel = SharedViewModel()

    val firestoreViewModel = FirestoreViewModel()
    firestoreViewModel.getNotes()
    firestoreViewModel.getPinnedNotes()
    firestoreViewModel.getCategories()

    val categoriesList = remember { mutableStateListOf<String>() }
    val notesList = remember { mutableStateListOf<Note>() }
    val pinnedNotesList = remember { mutableStateListOf<Note>() }
    val selectedButtons = remember { mutableStateListOf<Boolean>() }

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
                )
                notesList.add(note)
            }
        }
    }

    firestoreViewModel.pinnedNoteResults.observeForever {
        pinnedNotesList.clear()
        if (it != null) {
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

    firestoreViewModel.categoryResults.observeForever {
        categoriesList.clear()
        selectedButtons.clear()
        if (it != null) {
            for (category in it) {
                categoriesList.add(category)
            }
        }
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .padding(0.dp),
        topBar = { TopNavBar(navController = navController, sharedViewModel) },
        bottomBar = { BottomNavBar(navController = navController) },
        floatingActionButton = { FloatingActionButton(navController) },
        floatingActionButtonPosition = FabPosition.Center,
        isFloatingActionButtonDocked = true,
        content = { innerPadding ->
            Box(modifier = Modifier.padding(innerPadding)) {
                Surface(
                    modifier = Modifier
                        .fillMaxSize(),
                   color = MaterialTheme.colors.background,
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
                            filterSection(categoriesList, selectedButtons, firestoreViewModel)
                        }
                        item {
                            displayChoice(onDisplayChoiceChange = { newDisplayChoice ->
                                currentDisplayChoice.value = newDisplayChoice
                            })
                        }
                        item {
                            pinnedNotes(currentDisplayChoice.value, pinnedNotesList, navController)
                        }
                        item {
                            allNotes(currentDisplayChoice.value, notesList, navController)
                        }
                        item {
                            Spacer(modifier = Modifier.height(50.dp))
                        }
                    }
                }
                AppNavigator(navController, sharedViewModel)
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
fun FilterButton(text: String, id: Int, selectedButtons: SnapshotStateList<Boolean>, firestoreViewModel: FirestoreViewModel) {
    val isSelected = selectedButtons[id]
    Button(
        onClick = {
            // Set all other buttons to false and flip the selected button
            for (i in selectedButtons.indices) {
                selectedButtons[i] = false
            }
            selectedButtons[id] = !isSelected

            if (selectedButtons[id]) {
                when (id) {
                    0 -> {
                        firestoreViewModel.getNotes(filter = "NW_INTERNAL_AI_ASSIST")
                        firestoreViewModel.getPinnedNotes(filter = "NW_INTERNAL_AI_ASSIST")
                    }
                    1 -> {
                        firestoreViewModel.getNotes(filter = "NW_INTERNAL_PRIVATE")
                        firestoreViewModel.getPinnedNotes(filter = "NW_INTERNAL_PRIVATE")
                    }
                    2 -> {
                        firestoreViewModel.getNotes(filter = "NW_INTERNAL_NEW")
                        firestoreViewModel.getPinnedNotes(filter = "NW_INTERNAL_NEW")
                    }
                    else -> {
                        firestoreViewModel.getNotes(filter = text)
                        firestoreViewModel.getPinnedNotes(filter = text)
                    }
                }
            } else {
                firestoreViewModel.getNotes()
                firestoreViewModel.getPinnedNotes()
            }

          },
        modifier = Modifier
            .padding(start = 8.dp, end = 4.dp)
            .height(38.dp),
        elevation = ButtonDefaults.elevation(0.dp),
        shape = RoundedCornerShape(20.dp),
        colors =
            if (isSelected) {
                ButtonDefaults.buttonColors()
            } else {
                ButtonDefaults.buttonColors(
                    backgroundColor = MaterialTheme.colors.surface,
                    contentColor = MaterialTheme.colors.onSurface
                )
            }


    ) {
        Text(text = text)
    }
}

@Composable
fun filterSection(
    categoriesList: List<String>,
    selectedButtons: SnapshotStateList<Boolean>,
    firestoreViewModel: FirestoreViewModel
) {
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
                    FilterButton(text = "AI Assist", id = 0, selectedButtons, firestoreViewModel)
                }
                item {
                    FilterButton(text = "Private", id = 1, selectedButtons, firestoreViewModel)
                }
                item {
                    FilterButton(text = "New", id = 2, selectedButtons, firestoreViewModel)
                }
                var i = 3
                categoriesList.forEach {
                    item {
                        FilterButton(text = it, id = i, selectedButtons, firestoreViewModel)
                    }
                    i++
                }
                for (a in 0..i) {
                    selectedButtons.add(false)
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

@Composable
fun pinnedNotes(
    currentDisplayChoice: Boolean,
    notesList: SnapshotStateList<Note>,
    navController: NavHostController
) {
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
                        items(notesList.size) {
                            NoteCard(notesList[it], navController)
                        }
                    }
                )
            } else {
                Column(content = {
                    for (note in notesList) {
                        NoteListItem(note, navController)
                        if (note != notesList.last()) {
                            Divider()
                        }
                    }
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
                        NoteListItem(note, navController)
                        if (note != notesList.last()) {
                            Divider()
                        }
                    }
                })
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
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
        elevation = 0.dp,
        backgroundColor = MaterialTheme.colors.surface
    ) {
       Column(modifier = Modifier.padding(14.dp)) {
            Text(
                text = if (note.title == "") "Untitled" else note.title,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
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
fun NoteListItem(note: Note, navController: NavController) {
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
            .clickable {
                navController.navigate("edit_note/" + note.id)
            }
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
            )
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp, bottom = 0.dp, start = 16.dp, end = 16.dp)
            .height(56.dp)
            .clip(RoundedCornerShape(10.dp))
            .focusRequester(focusRequester),
        colors = TextFieldDefaults.textFieldColors(
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent,
            backgroundColor = MaterialTheme.colors.surface
        )
    )
}


