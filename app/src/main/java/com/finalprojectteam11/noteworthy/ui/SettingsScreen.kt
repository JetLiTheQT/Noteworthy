package com.finalprojectteam11.noteworthy.ui

import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.finalprojectteam11.noteworthy.data.AppSettings
import java.util.*

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun SettingsScreen(navController: NavHostController) {
    val context = LocalContext.current
//    val languageOptions = listOf("English", "Spanish", "French")
    val sortByOptions = listOf("Name (A-Z)", "Name (Z-A)", "Date (Newest-Oldest)", "Date (Oldest-Newest)")
    val displayOptions = listOf("List", "Grid")

//    val selectedLanguage = remember { mutableStateOf(AppSettings.selectedLanguage) }
    val selectedSortBy = remember { mutableStateOf(AppSettings.selectedSortBy) }
    val displayPreferences = remember { mutableStateOf(AppSettings.displayChoice) }


    Scaffold {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {

            GeneralOptionsUI(navController)
            SupportOptionsUI(navController)

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Display Preference",
                style = MaterialTheme.typography.h6,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            RadioGroup(
                options = displayOptions,
                selectedOption = if (displayPreferences.value) "List" else "Grid",
                onOptionSelected = { option ->
                    displayPreferences.value = option == "List"
                }
            )

            Text(
                text = "Sort by",
                style = MaterialTheme.typography.h6,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            RadioGroup(
                options = sortByOptions,
                selectedOption = if (selectedSortBy.value != "") selectedSortBy.value else "Date (Newest-Oldest)",
                onOptionSelected = { option -> selectedSortBy.value = option }
            )
        }
    }
    SaveSettingsOnDispose(selectedSortBy, displayPreferences)
}

@Composable
fun SaveSettingsOnDispose(selectedSortBy: MutableState<String>, displayPreferences: MutableState<Boolean>) {
    val context = LocalContext.current
    DisposableEffect(Unit) {
        onDispose {
            AppSettings.selectedSortBy = selectedSortBy.value
            AppSettings.displayChoice = displayPreferences.value
            AppSettings.updateSettings()
        }
    }
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



