package com.finalprojectteam11.noteworthy.ui

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
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
    val sortByOptions = listOf("title", "time")
    val queryDirectionOptions = listOf("ASCENDING", "DESCENDING")

//    val selectedLanguage = remember { mutableStateOf(AppSettings.selectedLanguage) }
    val selectedSortBy = remember { mutableStateOf(AppSettings.selectedSortBy) }
    val selectedQueryDirection = remember { mutableStateOf(AppSettings.selectedQueryDirection) }


    Scaffold {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {

            GeneralOptionsUI(navController)
            SupportOptionsUI(navController)

            Spacer(modifier = Modifier.height(16.dp))


            Text(
                text = "Sort by",
                style = MaterialTheme.typography.h6,
                modifier = Modifier.padding(bottom = 8.dp)
            )

//            RadioGroup(
//                options = sortByOptions,
//                selectedOption = selectedSortBy.value,
//                onOptionSelected = { option ->
//                    when(option){
//                        "Name (A-Z)" -> {
//                            selectedSortBy.value = "title"
//                            selectedQueryDirection.value = "DESCENDING"
//                        }
//                        "Name (Z-A)" -> {
//                            selectedSortBy.value = "title"
//                            selectedQueryDirection.value = "ASCENDING"
//                        }
//                        "Date (Newest-Oldest)" -> {
//                            selectedSortBy.value = "time"
//                            selectedQueryDirection.value = "DESCENDING"
//                            }
//                        "Date (Oldest-Newest)" -> {
//                            selectedSortBy.value = "time"
//                            selectedQueryDirection.value = "ASCENDING"
//                        }
//                        "Default" -> {
//                            selectedSortBy.value = "title"
//                            selectedQueryDirection.value = "DESCENDING"
//                        }
//                    }
//                }
//            )

            RadioGroup(
                options = sortByOptions,
                selectedOption = selectedSortBy.value,
                onOptionSelected = { option -> selectedSortBy.value = option }
            )

            RadioGroup(
                options = queryDirectionOptions,
                selectedOption = selectedQueryDirection.value,
                onOptionSelected = { option -> selectedQueryDirection.value = option }
            )
        }
    }
    SaveSettingsOnDispose(selectedSortBy, selectedQueryDirection)
}

@Composable
fun SaveSettingsOnDispose(selectedSortBy: MutableState<String>, selectedQueryDirection: MutableState<String>) {
    val context = LocalContext.current
    DisposableEffect(Unit) {
        onDispose {
            AppSettings.selectedSortBy = selectedSortBy.value
            AppSettings.selectedQueryDirection = selectedQueryDirection.value
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



