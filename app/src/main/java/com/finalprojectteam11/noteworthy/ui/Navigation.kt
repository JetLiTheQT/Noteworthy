package com.finalprojectteam11.noteworthy.ui

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.navigation.*
import androidx.navigation.compose.currentBackStackEntryAsState
import com.finalprojectteam11.noteworthy.R

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object AddNote : Screen("add_note")
    object Settings : Screen("settings")
    object EditNote : Screen("edit_note/{note_id}")
    object Search: Screen("search/{query}")
    object Profile: Screen("profile")
    object Contact: Screen("contact")
    object Assist: Screen("assist/{title}/{description}/{response}")
}

@Composable
fun AppNavigator(navController: NavHostController, sharedViewModel: SharedViewModel, firestoreViewModel: FirestoreViewModel) {
    NavHost(navController = navController, startDestination = Screen.Home.route) {
        composable(Screen.Home.route) { HomeScreen(navController) }
        composable(Screen.AddNote.route) { NoteScreen(navController, sharedViewModel, "")  }
        composable(Screen.Settings.route) { SettingsScreen(navController) }
        composable(
            Screen.EditNote.route,
            arguments = listOf(
                navArgument("note_id") {
                    type = NavType.StringType
                },
            )
        ) { backStackEntry ->
            NoteScreen(navController, sharedViewModel, backStackEntry.arguments?.getString("note_id"))
        }
        composable(
            Screen.Search.route,
            arguments = listOf(
                navArgument("query") {
                    type = NavType.StringType
                },
            )
        ) { backStackEntry ->
            SearchScreen(navController, backStackEntry.arguments?.getString("query"))

        }
        composable(Screen.Profile.route) { ProfileScreen(navController, firestoreViewModel) }

        composable(Screen.Contact.route) { ContactScreen(navController) }

        composable(
            Screen.Assist.route,
            arguments = listOf(
                navArgument("title") {
                    type = NavType.StringType
                },
                navArgument("description") {
                    type = NavType.StringType
                },
                navArgument("response") {
                    type = NavType.StringType
                },
            )
        ) { backStackEntry ->
            AssistScreen(navController, backStackEntry.arguments?.getString("title"), backStackEntry.arguments?.getString("description"), backStackEntry.arguments?.getString("response"))

        }
    }
}

@Composable
fun FloatingActionButton(navController: NavController){
    if (navController.currentBackStackEntryAsState().value?.destination?.route == Screen.Home.route) {
        FloatingActionButton(
            onClick = { navController.navigate(Screen.AddNote.route) },
            modifier = Modifier
                .padding(2.dp)
                .size(72.dp),
            shape = CircleShape
        ) {
            Icon(
                imageVector = ImageVector.vectorResource(id = R.drawable.edit_fill1_wght400_grad0_opsz48),
                contentDescription = "Add Note",
                modifier = Modifier.padding(18.dp)
            )
        }
    }
}


@SuppressLint("UnrememberedMutableState")
@Composable
fun TopNavBar (navController: NavHostController, sharedViewModel: SharedViewModel) {
    val canGoBack = navController.previousBackStackEntry != null
    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route
    val searchQuery = mutableStateOf("")
    var title by remember { mutableStateOf("") }

    sharedViewModel.appBarTitle.observeForever() {
        Log.d("TAG", "TopNavBar: $it")
        title = when (navController.currentBackStackEntry?.destination?.route) {
            Screen.AddNote.route -> if (it != "") {
                it
            } else {
                "Add Note"
            }
            Screen.Settings.route -> "Settings"
            Screen.EditNote.route -> "Edit Note"
            Screen.Search.route -> "Search Results"
            Screen.Contact.route -> "Contact Us"
            Screen.Profile.route -> "Your Profile"
            Screen.Assist.route -> "AI Assist"
            else -> "Noteworthy"
        }
    }

    TopAppBar(
        title = { Text(title) },
        navigationIcon = if (canGoBack) {
            {
                IconButton(onClick = { navController.navigateUp() }) {
                    Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                }
            }
        } else if (!canGoBack) {{
            IconButton(onClick = { navController.navigate(Screen.Home.route) {
                popUpTo(navController.graph.startDestinationId)
                launchSingleTop = true
            } }){
                Icon(painter = painterResource(id = R.mipmap.ic_launcher_foreground), contentDescription = "Icon")
            }
        }}
        else null,
        actions = {
            if(currentRoute == Screen.Home.route){
                IconButton(onClick = {navController.navigate(Screen.Search.route)}){
                    Icon(Icons.Filled.Search, contentDescription = "Search")
                }
            }
            if(currentRoute == Screen.Search.route){
                            SearchBox(
                                searchQuery = searchQuery,
                                onSearchQueryChange = {
                                    searchQuery.value = it
                                },
                                                                navController
                            )
            }
        }
    )
}

@Composable
fun BottomNavBar(navController: NavHostController) {
    val items = listOf(Screen.Home, Screen.AddNote, Screen.Settings)
    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route
    // Display regular TopAppBar for non-child screens
    if (currentRoute == Screen.Home.route) {
        BottomAppBar(
            cutoutShape = CircleShape,
            modifier = Modifier
                .fillMaxWidth()
                .shadow(0.dp),
            elevation = 0.dp // Remove elevation to avoid multiple layers
        ) {
            BottomNavigation(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(0.dp),
                elevation = 0.dp // Remove elevation to avoid multiple layers
            ) {
                items.forEach { screen ->
                    val isSelected = currentRoute == screen.route
                    BottomNavigationItem(
                        icon = {
                            when (screen) {
                                is Screen.Home -> Icon(Icons.Filled.Home, contentDescription = "Home")
                                is Screen.Settings -> Icon(Icons.Filled.Settings, contentDescription = "Settings")
                                else -> { }
                            }
                        },
                        label = {
                            Text(
                                text = when (screen) {
                                    is Screen.Home -> "Home"
                                    is Screen.Settings -> "Settings"
                                    else -> {""}
                                }
                            )
                        },
                        selected = isSelected,
                        onClick = {
                            navController.navigate(screen.route) {
                                popUpTo(navController.graph.startDestinationId)
                                launchSingleTop = true
                            }
                        },
                    )
                }
            }
        }
    }
}