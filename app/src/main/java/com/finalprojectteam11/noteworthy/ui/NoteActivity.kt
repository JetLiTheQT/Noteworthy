package com.finalprojectteam11.noteworthy.ui

import android.app.Activity
import android.app.SearchManager
import kotlinx.serialization.*
import kotlinx.serialization.json.*
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.AlarmClock
import android.provider.CalendarContract
import android.speech.RecognizerIntent
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.*
import androidx.compose.ui.text.style.BaselineShift
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.navigation.NavController
import com.finalprojectteam11.noteworthy.R
import com.finalprojectteam11.noteworthy.data.AssistAction
import com.finalprojectteam11.noteworthy.data.LoadingStatus
import com.finalprojectteam11.noteworthy.ui.theme.CompletionViewModel
import com.finalprojectteam11.noteworthy.ui.theme.MyApplicationTheme
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay
import java.util.Calendar

@Composable
fun NoteScreen(navController: NavController, noteId : String?) {
    var searchQuery by remember { mutableStateOf("") }
    val noteText = remember { mutableStateOf("") }
    val noteTextField = remember { mutableStateOf(TextFieldValue()) }
    val completionText = remember { mutableStateOf("") }
    val changedByCompletion = remember { mutableStateOf(0) }
    val snackbarHostState = remember { SnackbarHostState() }
    val currentNoteID = remember { mutableStateOf("") }
    val activityTitle =  remember { mutableStateOf("Add Note") }
    var action by remember { mutableStateOf(AssistAction()) }
    var actionGenerated = remember { mutableStateOf(false) }

    if (noteId != null && noteId != "") {
        var firestoreViewModel = FirestoreViewModel()
        firestoreViewModel.getNote(noteId)

        var completionViewModel = CompletionViewModel()

        // once the completion is fetched, update the completion state
        firestoreViewModel.noteResult.observeForever() {
            if (it != null) {
                currentNoteID.value = it.id
                noteText.value = it.data?.get("content").toString()
                searchQuery = it.data?.get("title").toString()
                noteTextField.value = TextFieldValue(noteText.value)
                activityTitle.value = "Edit Note"
                completionViewModel.fetchAction(noteText.value)
            }
        }

        completionViewModel.actionResults.observeForever() {
            if (!actionGenerated.value) {
                if (it != null) {
                    if (it.choices.isNotEmpty()) {
                        actionGenerated.value = true
                        val actionJSON = it.choices[0].text.substringAfter("{").substringBeforeLast("}")
                        val actionJSONString = "{$actionJSON}"

                        // Get last double quote in JSON string and remove trailing comma if present
                        val lastQuoteIndex = actionJSONString.lastIndexOf("\"")
                        val actionJSONStringFixed = if (actionJSONString[lastQuoteIndex - 1] == ',') {
                            actionJSONString.substring(0, lastQuoteIndex - 1) + actionJSONString.substring(lastQuoteIndex)
                        } else {
                            actionJSONString
                        }

                        try {
                            val json = Json {
                                ignoreUnknownKeys = true
                                coerceInputValues = true
                            }

                            action = json.decodeFromString(actionJSONStringFixed)
                            Log.d("JSON", action.toString())
                            loadActionIntents(action)

                        } catch (e: Exception) {
                            Log.d("JSON", "Error parsing JSON")
                            Log.d("JSON", e.toString())
                        }
                    }
                }
            }
        }
    }

    val launcher = rememberLauncherForActivityResult(contract = ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val matches = result.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            matches?.firstOrNull()?.let { recognizedText ->
                // Get cursor position from noteTextField
                var cursorStart = noteTextField.value.selection.start
                var cursorEnd = noteTextField.value.selection.end
                // Get text before cursor
                var textBeforeCursor = noteTextField.value.text.substring(0, cursorStart)
                // Get text after cursor
                var textAfterCursor = noteTextField.value.text.substring(cursorEnd, noteTextField.value.text.length)

                // Remove completion text from textAfterCursor, if it exists
                if (textAfterCursor.endsWith(completionText.value)) {
                    textAfterCursor = textAfterCursor.substring(0, textAfterCursor.length - completionText.value.length)
                }

                // Clear completion text
                completionText.value = ""

                // Concatenate text before cursor, recognized text, and text after cursor
                noteText.value = "$textBeforeCursor $recognizedText $textAfterCursor"
                // Set cursor position to end of recognized text
                noteTextField.value = TextFieldValue(noteText.value, TextRange(cursorStart + recognizedText.length + 1))

                // Trigger new completion request
                performAutoCompleteSearch(noteText.value, completionText, changedByCompletion)
            }
        }
    }
        MyApplicationTheme {
            Box(modifier = Modifier.fillMaxSize()) {
                Scaffold(
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
                            verticalArrangement = Arrangement.spacedBy(0.dp)
                        ) {
                            item {
                                ComposeNote(searchQuery = searchQuery, onSearchQueryChange = { searchQuery = it })
                            }
                            item {
                                TextInputBox(noteTextField, noteText, completionText, changedByCompletion, actionGenerated)
                            }
                            item {
                                NoteControls(launcher,snackbarHostState, searchQuery, noteText, currentNoteID, activityTitle, action)
                            }
                            item {
                                Spacer(modifier = Modifier.height(50.dp))
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
}


@Composable
fun ComposeNote(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit
) {
    val focusManager = LocalFocusManager.current

    TextField(
        value = searchQuery,
        onValueChange = onSearchQueryChange,
        placeholder = {
            Text(
                text = "Enter Note Title",
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                color = Color.Black
            )
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp, horizontal = 16.dp)
            .height(56.dp)
            .clip(RoundedCornerShape(10.dp)),
        colors = TextFieldDefaults.textFieldColors(
            focusedLabelColor = Color.Black,
            cursorColor = Color.Black,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent,
            backgroundColor = Color(0xFFE5E5E5),
            unfocusedLabelColor = Color.Black,
        ),
        textStyle = TextStyle(
            fontSize = 18.sp,
            color = Color.Black,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Bold
        ),
        singleLine = true,
        maxLines = 1,
        keyboardActions = KeyboardActions(
            onDone = {
                // hide the keyboard when the user presses "Done" or "Enter"
                focusManager.clearFocus()
            }
        )
    )
}




@Composable
fun TextInputBox(
    noteTextField: MutableState<TextFieldValue>,
    noteText: MutableState<String>,
    completion: MutableState<String>,
    changedByCompletion: MutableState<Int>,
    actionGenerated: MutableState<Boolean>
) {
    noteTextField.value = TextFieldValue(noteText.value + completion.value, TextRange(noteTextField.value.selection.start, noteTextField.value.selection.end))
    val coroutineScope = rememberCoroutineScope()
    var cursorPosition by remember { mutableStateOf(0) }
    val scrollState = rememberScrollState()
    val configuration = LocalConfiguration.current
    val density = LocalDensity.current
    val screenHeight = configuration.screenHeightDp.dp

    val statusBarHeight = with(density) { 24.dp } // example value
    val appBarHeight = with(density) { 56.dp } // example value
    val contentHeight = screenHeight - statusBarHeight - appBarHeight

    val textfieldHeight = with(density) { 48.dp } // example value
    val remainingHeight = contentHeight - textfieldHeight

    val lineHeight = with(density) { 16.sp.toPx().dp } // example value
    val numLines = (remainingHeight/lineHeight).toInt()
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 0.dp, bottom = 0.dp, start = 16.dp, end = 16.dp)
            .clip(RoundedCornerShape(10.dp, 10.dp, 0.dp, 0.dp))
            .background(Color(0xFFE5E5E5))
            .height(300.dp) // Set a fixed height for the Box
    ) {
        TextField(
            value = noteTextField.value,
            onValueChange = {
                var performCompletion = true
                cursorPosition = noteTextField.value.selection.start // update cursor position
                if (noteTextField.value.selection.start == noteTextField.value.text.length) {
                    coroutineScope.launch {
                        scrollState.animateScrollTo(scrollState.maxValue) // scroll to the end of the text
                    }
                }
//                Log.d("TextField", "CursorPositionValue:  {$cursorPosition}")
                // Backup previous text for comparison
                val previousText = noteText.value.plus(completion.value)

                // If the user deletes text, clear the completion text
                if (it.text.length < previousText.length) {
                    noteText.value = it.text.substring(0, it.text.length - completion.value.length)
                    completion.value = ""
                    noteTextField.value = TextFieldValue(
                        noteText.value + completion.value,
                        TextRange(it.selection.start, it.selection.start)
                    )
                }

                // If the user adds text, check if it matches the completion text
                else if (it.text.length > previousText.length) {
                    val newText = it.text.substring(0, it.text.length - completion.value.length)
                    val differenceLength = newText.length - noteText.value.length

                    // If previous text starts with new text, add the difference to completion text
                    if (previousText.startsWith(newText)) {

                        // If there is a match, find the difference and move it from completion text to text
                        val oldCompletionText = it.text.substring(
                            it.text.length - completion.value.length,
                            it.text.length
                        )
                        val newCompletionText =
                            completion.value.substring(differenceLength, completion.value.length)

                        // If completion text was modified, clear completion text
                        if (!completion.value.contains(oldCompletionText)) {
                            noteText.value = newText
                            completion.value = ""

                            noteTextField.value = TextFieldValue(
                                noteText.value + completion.value,
                                TextRange(it.selection.start, it.selection.start)
                            )

                            // If completion text was not modified, add new completionText
                        } else {
                            performCompletion = false
                            noteText.value = newText
                            completion.value = newCompletionText
                            noteTextField.value = TextFieldValue(
                                noteText.value + completion.value,
                                TextRange(it.selection.start, it.selection.start)
                            )
                        }

                        // If previousText was modified, clear completion text
                    } else {
                        noteText.value =
                            it.text.substring(0, it.text.length - completion.value.length)
                        completion.value = ""

                        noteTextField.value = TextFieldValue(
                            noteText.value + completion.value,
                            TextRange(it.selection.start, it.selection.start)
                        )
                    }
                    // If text length is the same, check if the user is selecting text, moving the cursor, or modifying the text
                } else {
                    if (it.text != noteText.value + completion.value) {
                        noteText.value =
                            it.text.substring(0, it.text.length - completion.value.length)
                        completion.value = ""
                        noteTextField.value = TextFieldValue(
                            noteText.value + completion.value,
                            TextRange(
                                noteTextField.value.selection.start,
                                noteTextField.value.selection.end
                            )
                        )
                    } else {
                        performCompletion = false
                        // if selectionEnd is less than index of completion text, then set selectionEnd to index of completion text
                        var selectionStart = it.selection.start
                        var selectionEnd = it.selection.end
                        if (it.selection.end > noteText.value.length) {
                            selectionEnd = noteText.value.length
                            if (it.selection.start > noteText.value.length) {
                                selectionStart = noteText.value.length
                            }
                        }
                        noteTextField.value = TextFieldValue(
                            noteText.value + completion.value,
                            TextRange(selectionStart, selectionEnd)
                        )
                    }
                }

                if (performCompletion) {
                    actionGenerated.value = false
                }

                if (changedByCompletion.value == 0 && performCompletion) {
                    // Cancel any ongoing search
                    coroutineScope.coroutineContext.cancelChildren()
                    // Launch a new search after 2 seconds
                    coroutineScope.launch {
                        delay(1500)
                        performAutoCompleteSearch(noteText.value, completion, changedByCompletion)
                    }
                } else {
                    changedByCompletion.value = 0
                }
            },
            label = { Text("Note Content") },
            visualTransformation = ColorsTransformation(completion.value),
              // NOT WORKING confused but it's supposed to scroll to the new line

//            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done), // listen for the Done key event
//            keyboardActions = KeyboardActions(onDone = {
//                coroutineScope.launch {
//                    val cursorLine = cursorPosition / numLines
//                    val scrollPosition = cursorLine * with(density) { lineHeight.toPx() }
//                    scrollState.animateScrollTo(scrollPosition.toInt())
//                }
//            }),
            modifier = Modifier
                .pointerInput(Unit) {
                    detectHorizontalDragGestures { change, dragAmount ->
                        change.consume()
                        // If drag is positive toward the right, then add completion text to text
                        if (dragAmount > 100 && completion.value != "") {
                            noteText.value = noteText.value.plus(completion.value)
                            completion.value = ""
                            noteTextField.value = TextFieldValue(
                                noteText.value + completion.value,
                                TextRange(noteText.value.length, noteText.value.length)
                            )
                        }
                    }
                }
                .padding(16.dp) // Add padding to the TextField to center the text vertically
                .fillMaxWidth()
                .wrapContentHeight() // Allow the TextField to wrap its content height
                .verticalScroll(rememberScrollState()) // Enable vertical scrolling if necessary
                .clip(RoundedCornerShape(10.dp, 10.dp, 0.dp, 0.dp)),
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
}

@Composable
fun NoteControls(
    launcher: ActivityResultLauncher<Intent>,
    snackbarHostState: SnackbarHostState,
    title: String,
    content: MutableState<String>,
    currentNoteID: MutableState<String>,
    activityTitle: MutableState<String>,
    action: AssistAction
) {
    val firestoreViewModel = FirestoreViewModel()

    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val focusManager = LocalFocusManager.current
    Divider(modifier = Modifier
        .fillMaxWidth()
        .padding(top = 0.dp, bottom = 0.dp, start = 16.dp, end = 16.dp),
        color = Color.LightGray
    )

    var bgCorner = if (action.category != "None") 0.dp else 10.dp
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 0.dp, bottom = 0.dp, start = 16.dp, end = 16.dp)
            .clip(RoundedCornerShape(0.dp, 0.dp, bgCorner, bgCorner))
            .background(Color(0xFF3694C9))
            .zIndex(1f),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(0.dp)
                .clip(RoundedCornerShape(0.dp, 0.dp, 10.dp, 10.dp))
                .background(Color(0xFFE5E5E5))
                .zIndex(2f),
        ) {
            IconButton(
                onClick = {
                    val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                        putExtra(
                            RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
                        )
                    }
                    if (isSpeechRecognizerAvailable(context)) {
                        launcher.launch(intent)
                    } else {
                        coroutineScope.launch {
                            snackbarHostState.showSnackbar("Speech recognizer not available")
                        }
                    }
                },
                modifier = Modifier.padding(top = 16.dp, bottom = 16.dp, start = 16.dp, end = 16.dp)
            ) {
                Icon(
                    ImageVector.vectorResource(id = R.drawable.mic_fill1_wght400_grad0_opsz48),
                    contentDescription = "Record Voice Note",
                    tint = Color(0xFF3694C9),
                    modifier = Modifier.size(36.dp)
                )

            }
            Spacer(modifier = Modifier.weight(1f))
            Button(
                onClick = {
                    focusManager.clearFocus() // Close the keyboard tray

                    firestoreViewModel.saveNote(
                        content.value,
                        title,
                        System.currentTimeMillis().toString(),
                        currentNoteID.value
                    )
                    firestoreViewModel.loadingStatus.observeForever {
                        if (it == LoadingStatus.SUCCESS) {
                            activityTitle.value = "Edit Note"

                            coroutineScope.launch {
                                snackbarHostState.showSnackbar("Note Saved Successfully")
                            }
                        } else if (it == LoadingStatus.ERROR) {
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar("Error Saving Note")
                            }
                        }
                    }

                    firestoreViewModel.noteID.observeForever {
                        if (it != null && it != "") {
                            currentNoteID.value = it
                        }
                    }
                },
                shape = RoundedCornerShape(20.dp, 20.dp, 20.dp, 20.dp),
                modifier = Modifier.padding(
                    top = 16.dp,
                    bottom = 16.dp,
                    start = 16.dp,
                    end = 16.dp
                ),
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = Color(0xFF3694C9),
                    contentColor = Color.White
                )

            ) {
                Text("Save")

            }
        }
    }
    if (action.category != "None") {

            Button(
                onClick = {
                    // Launch intent from action
                    val intent = action.intent
                    if (intent != null) {
                        context.startActivity(intent)
                    }
                },
                shape = RoundedCornerShape(0.dp, 0.dp, 10.dp, 10.dp),
                modifier = Modifier
                    .padding(top = 0.dp, bottom = 0.dp, start = 16.dp, end = 16.dp)
                    .fillMaxWidth()
                    .shadow(0.dp)
                    .zIndex(1f)
                    ,
                elevation = ButtonDefaults.elevation(0.dp),
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = Color(0xFF3694C9),
                    contentColor = Color.White
                ),
            )
            {
                if (action.description != "None" && action.description != "" && action.title != "None" && action.title != "") {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 0.dp, bottom = 0.dp, start = 0.dp, end = 0.dp)

                    ) {
                        Text(
                            text = action.title,
                            modifier = Modifier.padding(
                                top = 16.dp,
                                bottom = 4.dp,
                                start = 16.dp,
                                end = 16.dp
                            ),
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = action.description,
                            modifier = Modifier.padding(
                                top = 0.dp,
                                bottom = 16.dp,
                                start = 16.dp,
                                end = 16.dp
                            ),
                            color = Color.White,
                            fontSize = 12.sp
                        )
                    }
                } else if (action.title != "None" && action.title != "") {
                    Text(
                        text = action.title,
                        modifier = Modifier.padding(
                            top = 16.dp,
                            bottom = 16.dp,
                            start = 16.dp,
                            end = 16.dp
                        ),
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                } else {
                    Text(
                        text = action.category,
                        modifier = Modifier.padding(
                            top = 16.dp,
                            bottom = 16.dp,
                            start = 16.dp,
                            end = 16.dp
                        ),
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
    }
}
fun isSpeechRecognizerAvailable(context: Context): Boolean {
    val packageManager = context.packageManager
    val speechRecognizerIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
    val resolveInfoList = packageManager.queryIntentActivities(speechRecognizerIntent, PackageManager.MATCH_DEFAULT_ONLY)
    return resolveInfoList.isNotEmpty()
}

class ColorsTransformation(private var completion: String) : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        return TransformedText(
            buildAnnotatedStringWithColors(text.toString(), completion),
            OffsetMapping.Identity)
    }
}

fun performAutoCompleteSearch(searchText: String, completion: MutableState<String>, changedByCompletion: MutableState<Int>) {
    if (searchText != "") {
        var completionViewModel = CompletionViewModel()
        completionViewModel.fetchCompletion(searchText)

        // once the completion is fetched, update the completion state
        completionViewModel.loadingStatus.observeForever {
            if (it == LoadingStatus.SUCCESS) {
                // check if completion is empty
                if (completionViewModel.completionResults.value?.choices?.isNotEmpty() == true) {

                    // if last character of searchText is a space, then don't add a space
                    if (searchText.last() == ' ') {
                        completion.value =
                            completionViewModel.completionResults.value?.choices?.get(0)?.text?.trimStart()
                                ?: ""
                    } else {
                        completion.value =
                            (" " + completionViewModel.completionResults.value?.choices?.get(0)?.text?.trimStart())
                    }
                } else {
                    completion.value = ""
                }
            } else {
                completion.value = ""
            }
            changedByCompletion.value = 1
        }
    }
}

fun buildAnnotatedStringWithColors(text:String, completion: String): AnnotatedString{
    val builder = AnnotatedString.Builder()
    // Add text string before completion text string to builder
    val textBeforeCompletion = text.substring(0, text.length - completion.length)
    builder.append(textBeforeCompletion)
    // Add completion text string to builder with gray color
    val completionTextArray = completion.split(" ")
    completionTextArray.forEach { word ->
        builder.withStyle(style = SpanStyle(color = Color.Gray)) {
            append("$word ")
        }
    }
    // append icon if completion text is not empty
    if (completion != "") {
        builder.append(" ")
        // Append right arrow with border
        builder.withStyle(style = SpanStyle(
            color = Color.Gray,
            background = Color.LightGray,
            shadow = Shadow(
                color = Color.Gray,
                offset = Offset(1f, 1f),
                blurRadius = 1f
            ),
            baselineShift = BaselineShift(0.1f),
        )) {
            append(" Swipe ➟ ")
        }
    }

    return builder.toAnnotatedString()
}

fun loadActionIntents(action: AssistAction) {
    when (action.category) {
        "None" -> {
            action.category = "None"
        }
        "Search" -> {
            if (action.term != "" && action.term != "None") {
                val intent = Intent(Intent.ACTION_WEB_SEARCH)
                intent.putExtra(SearchManager.QUERY, action.term)
                action.intent = intent
            } else {
                action.category = "None"
            }
        }
        "Define" -> {
            if (action.term != "" && action.term != "None") {
                val intent = Intent(Intent.ACTION_WEB_SEARCH)
                intent.putExtra(SearchManager.QUERY, "define ${action.term}")
                action.intent = intent
            } else {
                action.category = "None"
            }
        }
        "Map" -> {
            if (action.location != "" && action.location != "None") {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("geo:0,0?q=${action.location}"))
                action.intent = intent
            } else if (action.term != "" && action.term != "None") {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("geo:0,0?q=${action.term}"))
                action.intent = intent
            } else {
                action.category = "None"
            }
        }
        "Translate" -> {
            if (action.term != "" && action.term != "None") {
                val intent = Intent(Intent.ACTION_WEB_SEARCH)
                intent.putExtra(SearchManager.QUERY, "translate ${action.term}")
                action.intent = intent
            } else {
                action.category = "None"
            }
        }
        "Reminder" -> {
            // Add alarm to alarm manager
            val intent = Intent(AlarmClock.ACTION_SET_ALARM)
            intent.putExtra(AlarmClock.EXTRA_HOUR, action.hour)
            intent.putExtra(AlarmClock.EXTRA_MINUTES, action.minute)
            action.intent = intent
        }
        "Call" -> {
            if (action.url != "" && action.url != "None" && action.url.startsWith("tel:")) {
                val intent = Intent(Intent.ACTION_VIEW)
                intent.data = Uri.parse(action.url)
                action.intent = intent
            } else {
                action.category = "None"
            }
        }
        "Email" -> {
            if (action.url != "" && action.url != "None" && action.url.startsWith("mailto:")) {
                val intent = Intent(Intent.ACTION_VIEW)
                intent.data = Uri.parse(action.url)
                action.intent = intent
            } else {
                action.category = "None"
            }
        }
        "Weather" -> {
            if (action.location != "" && action.location != "None") {
                val intent = Intent(Intent.ACTION_WEB_SEARCH)
                intent.putExtra(SearchManager.QUERY, "weather ${action.location}")
                action.intent = intent
            } else {
                action.category = "None"
            }
        }
        else -> {
            if (action.response != "" && action.response != "None") {
                if (action.url != "" && action.url != "None") {
                    val intent = Intent(Intent.ACTION_VIEW)
                    intent.data = Uri.parse(action.url)
                    action.intent = intent
                } else {
                    action.category = "None"
                }
               // Open action in AI view
            } else {
                action.category = "None"
            }
        }
    }
}