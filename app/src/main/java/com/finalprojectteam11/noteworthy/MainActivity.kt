package com.finalprojectteam11.noteworthy

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.finalprojectteam11.noteworthy.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    var currentDisplayChoice by mutableStateOf(false)
    var searchQuery by mutableStateOf("")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyApplicationTheme {
                Scaffold(
                    topBar = {
                        TopAppBar(
                            title = { Text(text = "Noteworthy") },
                            navigationIcon = {
                                IconButton(onClick = { /*TODO*/ },) {
                                    Icon(Icons.Filled.Menu, contentDescription = "Menu", modifier = Modifier.padding(14.dp))
                                }
                            },
                            backgroundColor = Color(0xFF3694C9),
                            contentColor = Color.White,
                        )
                    },
                    bottomBar = {
                        BottomAppBar(
                            cutoutShape = CircleShape,
                            contentPadding = PaddingValues(48.dp, 8.dp),
                            contentColor = Color(0xFF3694C9),
                            backgroundColor = Color(0xFFFFFFFF),
                            modifier = Modifier.clip(
                                RoundedCornerShape(25.dp, 25.dp, 0.dp, 0.dp))
                        ) {
                            IconToggleButton(checked = true, onCheckedChange = {}) {
                                if (true) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Icon(Icons.Filled.Home, contentDescription = "Home")
                                        Text(text = "Home")
                                    }
                                } else {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Icon(Icons.Filled.Home, contentDescription = "Home", tint = Color.Gray)
                                        Text(text = "Home", color = Color.Gray)
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.weight(1f))
                            IconToggleButton(checked = false, onCheckedChange = {}) {
                                if (false) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Icon(Icons.Filled.Settings, contentDescription = "Settings")
                                        Text(text = "Settings")
                                    }
                                } else {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Icon(Icons.Filled.Settings, contentDescription = "Settings", tint = Color.Gray)
                                        Text(text = "Settings", color = Color.Gray)
                                    }
                                }
                            }

                        }
                    },
                    floatingActionButtonPosition = FabPosition.Center,
                    isFloatingActionButtonDocked = true,
                    floatingActionButton = { FloatingButtons() },
                    backgroundColor = Color(0xFFEFEFEF),
                ) { innerPadding ->
                    Surface(
                        modifier = Modifier
                            .padding(innerPadding),
                        color = Color(0xFFEFEFEF),
                    ) {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            item {
                                SearchBox()
                            }
                            item {
                                filterSection()
                            }
                            item {
                                displayChoice()
                            }
                            item {
                                recentNotes()
                            }
                            item {
                                allNotes()
                            }
                            item {
                                Spacer(modifier = Modifier.height(50.dp))
                            }
                        }
                    }
                }
            }
        }
    }

    @Composable
    fun SearchBox() {
        TextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
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
    fun displayChoice() {
        Row(modifier = Modifier.padding(top = 16.dp, bottom = 0.dp, start = 16.dp, end = 0.dp)) {
            IconButton(onClick = {
                currentDisplayChoice = false
            }) {
                Icon(imageVector = ImageVector.vectorResource(id = R.drawable.grid_view_fill1_wght400_grad0_opsz48), contentDescription = null, modifier = Modifier.size(24.dp))
            }
            IconButton(onClick = { currentDisplayChoice = true }) {
                Icon(Icons.Filled.List, contentDescription = null)
            }
        }
    }

    @OptIn(ExperimentalMaterialApi::class)
    @Composable
    fun recentNotes() {
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
    fun allNotes() {
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

    @Composable
    fun FloatingButtons() {
        var context = LocalContext.current
        FloatingActionButton(
            shape = CircleShape,
            onClick = {
                context.startActivity(
                    Intent(
                        context,
                        TestAIActivity::class.java
                    )
                )
            },
            backgroundColor = Color(55, 71, 79),
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
}


