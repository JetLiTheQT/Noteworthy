package com.finalprojectteam11.noteworthy.ui

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.*
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.finalprojectteam11.noteworthy.data.LoadingStatus
import com.finalprojectteam11.noteworthy.data.SearchResult

@Composable
fun SearchScreen(navController: NavController, query : String?) {
    val searchQuery = remember { mutableStateOf("") }
    val snackbarHostState = remember { SnackbarHostState() }
    val focusManager = LocalFocusManager.current
    val algoliaViewModel = AlgoliaViewModel()
    val searchResults = remember { mutableStateListOf<SearchResult>() }

    if (query == null || query == "") {
        navController.popBackStack()
    } else {
        searchQuery.value = query

        // Use a LaunchedEffect block to perform the initial search
        LaunchedEffect(searchQuery.value) {
            focusManager.clearFocus()
            algoliaViewModel.search(searchQuery.value)

            algoliaViewModel.loadingStatus.observeForever {
                if (it == LoadingStatus.SUCCESS) {
                    searchResults.clear()
                    for(i in algoliaViewModel.searchResults.value!!.hits) {
                        searchResults += i.deserialize(SearchResult.serializer())
                    }
                }
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold { innerPadding ->
            Surface(
                modifier = Modifier
                    .padding(innerPadding),
                color = MaterialTheme.colors.background,
            ) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(0.dp)
                ) {
                    item {
                        SearchResults(navController = navController, searchResults)
                    }
                }
            }
        }
        // Position the SnackbarHost at the top
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(top = 1.dp)
        )
    }
}



@Composable
fun SearchResults(navController: NavController, algoliaResults: List<SearchResult>) {
    Column(
        modifier = Modifier
            .padding(top = 16.dp, bottom = 0.dp, start = 0.dp, end = 0.dp)
            .fillMaxWidth()
    ) {
        if (algoliaResults.isEmpty()) {
            Text(
                text = "No results found.",
                fontSize = 18.sp,
                fontWeight = FontWeight.Normal,
                modifier = Modifier.padding(horizontal = 32.dp, vertical = 16.dp),
                color = MaterialTheme.colors.onBackground
            )
        } else {
            Column(content = {
                for (result in algoliaResults) {
                    SearchResultItem(result, navController)
                    if (algoliaResults.indexOf(result) != algoliaResults.size - 1) {
                        Divider(
                            thickness = 1.dp,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                    }
                }
            })
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun SearchResultItem(result: SearchResult, navController: NavController) {
    Log.d("SearchResultItem", result.toString())

    ListItem(
        text = {
            Text(
                text = if (result.title == "") "Untitled" else result.title,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        },
        secondaryText = {
            Text(
                text = if (result.content.length > 70) result.content.substring(0, 67) + "..." else result.content,
                fontSize = 16.sp,
                fontWeight = FontWeight.Normal
            )
        },
        modifier = Modifier
            .padding(14.dp)
            .clickable {
                navController.navigate("edit_note/${result.objectID}")
            }
    )
}

@Composable
fun SearchBox(searchQuery: MutableState<String>, onSearchQueryChange: (String) -> Unit, navController: NavController) {
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
        enabled = true,
        label = { Text(text = "Search") },
//        leadingIcon = {
//            Icon(
//                imageVector = Icons.Filled.Search,
//                contentDescription = "Search",
//            )
//        },
        textStyle = androidx.compose.ui.text.TextStyle(fontSize = 12.sp),

        modifier = Modifier
            .fillMaxWidth()
            .padding(2.dp)
            .height(56.dp)
            .clip(RoundedCornerShape(10.dp))
            .focusRequester(focusRequester),
        colors = TextFieldDefaults.textFieldColors(
            textColor = MaterialTheme.colors.onSurface,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent,
            backgroundColor = MaterialTheme.colors.surface
        ),
    )
    LaunchedEffect(focusRequester) {
        focusRequester.requestFocus()
    }
}