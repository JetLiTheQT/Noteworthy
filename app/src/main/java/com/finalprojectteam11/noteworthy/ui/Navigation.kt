package com.finalprojectteam11.noteworthy.ui

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
}
@Composable
fun AppNavigator(navController: NavHostController, sharedViewModel: SharedViewModel) {
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


@Composable
fun TopNavBar (navController: NavHostController, sharedViewModel: SharedViewModel) {
    val canGoBack = navController.previousBackStackEntry != null

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
        } else null,
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