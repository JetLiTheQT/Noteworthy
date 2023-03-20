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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.finalprojectteam11.noteworthy.ui.theme.MyApplicationTheme

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun SettingsScreen(navController: NavHostController) {
    val context = LocalContext.current
    val languageOptions = listOf("English", "Spanish", "French")
    val sortByOptions = listOf("Name (A-Z)", "Name (Z-A)", "Date (oldest first)", "Date (newest first)")

    val selectedLanguageValue = loadSelectedLanguage(context, languageOptions)
    val selectedSortByValue = loadSelectedSortBy(context, sortByOptions)

    val selectedLanguage = remember { mutableStateOf(selectedLanguageValue) }
    val selectedSortBy = remember { mutableStateOf(selectedSortByValue) }


    MyApplicationTheme {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(text = "Settings") },
                    navigationIcon = {
                        IconButton(
                            onClick = {
                                navController.popBackStack()
                            },
                        ) {
                            Icon(
                                Icons.Filled.ArrowBack,
                                contentDescription = "Back",
                                modifier = Modifier.padding(14.dp)
                            )
                        }
                    },
                    backgroundColor = Color(0xFF3694C9),
                    contentColor = Color.White,
                )
            }
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Account Info",
                    style = MaterialTheme.typography.h6,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
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

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Languages",
                    style = MaterialTheme.typography.h6,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                RadioGroup(
                    options = languageOptions,
                    selectedOption = selectedLanguage.value,
                    onOptionSelected = { option -> selectedLanguage.value = option }
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Sort by",
                    style = MaterialTheme.typography.h6,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                RadioGroup(
                    options = sortByOptions,
                    selectedOption = selectedSortBy.value,
                    onOptionSelected = { option -> selectedSortBy.value = option }
                )
            }
        }
    }
    SaveSettingsOnDispose(selectedLanguage, selectedSortBy)

}
@Composable
fun SaveSettingsOnDispose(selectedLanguage: MutableState<String>, selectedSortBy: MutableState<String>) {
    val context = LocalContext.current
    DisposableEffect(Unit) {
        onDispose {
            saveSelectedLanguage(context, selectedLanguage.value)
            saveSelectedSortBy(context, selectedSortBy.value)
        }
    }
}
private const val SELECTED_LANGUAGE_KEY = "selectedLanguage"
private const val SELECTED_SORT_BY_KEY = "selectedSortBy"
@Composable
private fun loadSelectedLanguage(context: Context, options: List<String>): String {
    return getPreferences(context).getString(SELECTED_LANGUAGE_KEY, options[0]) ?: options[0]
}
@Composable
private fun loadSelectedSortBy(context: Context,options: List<String>): String {
    return getPreferences(context).getString(SELECTED_SORT_BY_KEY, options[0]) ?: options[0]
}
private fun saveSelectedLanguage(context: Context, selectedLanguage: String) {
    getPreferences(context).edit().putString(SELECTED_LANGUAGE_KEY, selectedLanguage).apply()
}

private fun saveSelectedSortBy(context: Context, selectedSortBy: String) {
    getPreferences(context).edit().putString(SELECTED_SORT_BY_KEY, selectedSortBy).apply()
}

private fun getPreferences(context: Context): SharedPreferences {
    return PreferenceManager.getDefaultSharedPreferences(context)
}
@Composable
fun RadioGroup(
    options: List<String>,
    selectedOption: String,
    onOptionSelected: (String) -> Unit
) {
    Column {
        options.forEach { option ->
            Row(
                modifier = Modifier
                    .clickable { onOptionSelected(option) }
            ) {
                RadioButton(
                    selected = option == selectedOption,
                    onClick = { onOptionSelected(option) }
                )
                Text(
                    text = option,
                    style = MaterialTheme.typography.body1.merge(),
                    modifier = Modifier
                        .padding(start = 16.dp)
                        .weight(1f)
                        .align(Alignment.CenterVertically)
                )
            }
        }
    }
}



