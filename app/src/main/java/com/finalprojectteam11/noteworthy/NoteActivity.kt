package com.finalprojectteam11.noteworthy

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.speech.RecognizerIntent
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.*
import androidx.compose.ui.text.input.*
import androidx.compose.ui.text.style.BaselineShift
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.finalprojectteam11.noteworthy.ui.theme.MyApplicationTheme
import kotlinx.coroutines.launch

@Composable
fun NoteScreen(navController: NavController) {
    var currentDisplayChoice by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    var noteText = remember { mutableStateOf("") }
    var noteTextField = remember { mutableStateOf(TextFieldValue()) }
    var completionText = remember { mutableStateOf(" testing") }
    val snackbarHostState = remember { SnackbarHostState() }
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

                // Concatenate text before cursor, recognized text, and text after cursor
                noteText.value = "$textBeforeCursor $recognizedText $textAfterCursor"
                // Set cursor position to end of recognized text
                noteTextField.value = TextFieldValue(noteText.value, TextRange(cursorStart + recognizedText.length + 1))
                Log.d("NoteActivity", "Note text: ${noteText}")
            }
        }
    }
        MyApplicationTheme {
            Box(modifier = Modifier.fillMaxSize()) {
                Scaffold(
                    backgroundColor = Color(0xFFEFEFEF),
                    topBar = {
                        TopAppBar(
                            title = { Text(text = "Add Note") },
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
                                TextInputBox(noteTextField, noteText, completionText)
                            }
                            item {
                                NoteControls(launcher,snackbarHostState)
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
            textAlign = TextAlign.Center
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
fun TextInputBox(noteTextField: MutableState<TextFieldValue>, noteText: MutableState<String>, completion: MutableState<String>) {
    var completionText by remember { mutableStateOf("") }
    completionText = completion.value
    noteTextField.value = TextFieldValue(noteText.value + completionText, TextRange(noteTextField.value.selection.start, noteTextField.value.selection.end))

    TextField(
        value = noteTextField.value,
        onValueChange = {
            // Backup previous text for comparison
            val previousText = noteText.value.plus(completionText)

            // If the user deletes text, clear the completionText
            if (it.text.length < previousText.length) {
                Log.d("NoteActivity", "Text deleted")
                noteText.value = it.text.substring(0, it.text.length - completionText.length)
                completionText = ""
                noteTextField.value = TextFieldValue(noteText.value + completionText, TextRange(it.selection.start, it.selection.start))
            }

            // If the user adds text, check if it matches the completionText
            else if (it.text.length > previousText.length) {
                Log.d("NoteActivity", "Text added")
                val newText = it.text.substring(0, it.text.length - completionText.length)
                val differenceLength = newText.length - noteText.value.length

                // If previous text starts with new text, add the difference to completionText
                if (previousText.startsWith(newText)) {

                    // If there is a match, find the difference and move it from completionText to text
                    val oldCompletionText = it.text.substring(it.text.length - completionText.length, it.text.length)
                    val newCompletionText = completionText.substring(differenceLength, completionText.length)

                    // If completion text was modified, clear completionText
                    if (!completionText.contains(oldCompletionText)) {
                        Log.d("NoteActivity", "Completion text modified")
                        noteText.value = newText
                        completionText = ""

                        noteTextField.value = TextFieldValue(noteText.value + completionText, TextRange(it.selection.start, it.selection.start))

                    // If completion text was not modified, add new completionText
                    } else {
                        Log.d("NoteActivity", "Completion text not modified")
                        noteText.value = newText
                        completionText = newCompletionText
                        noteTextField.value = TextFieldValue(noteText.value + completionText, TextRange(it.selection.start, it.selection.start))
                    }

                // If previousText was modified, clear completionText
                } else {
                    Log.d("NoteActivity", "Previous text modified")
                    noteText.value = it.text.substring(0, it.text.length - completionText.length)
                    completionText = ""

                    noteTextField.value = TextFieldValue(noteText.value + completionText, TextRange(it.selection.start, it.selection.start))
                }
            // If text length is the same, check if the user is selecting text, moving the cursor, or modifying the text
            } else {
                Log.d("NoteActivity", "Text modified")
                if (it.text != noteText.value + completionText) {
                    Log.d("NoteActivity", "Text modified")
                    noteText.value = it.text.substring(0, it.text.length - completionText.length)
                    completionText = ""
                    noteTextField.value = TextFieldValue(noteText.value + completionText, TextRange(noteTextField.value.selection.start, noteTextField.value.selection.end))
                } else {
                    Log.d("NoteActivity", "Cursor moved")
                    // if selectionEnd is less than index of completionText, then set selectionEnd to index of completionText
                    var selectionStart = it.selection.start
                    var selectionEnd = it.selection.end
                    if (it.selection.end > noteText.value.length) {
                        selectionEnd = noteText.value.length
                        if (it.selection.start > noteText.value.length) {
                            selectionStart = noteText.value.length
                        }
                    }
                    noteTextField.value = TextFieldValue(noteText.value + completionText, TextRange(selectionStart, selectionEnd))
                }
            }
        },
        label = { Text("Note Content") },
        visualTransformation = ColorsTransformation(completionText),
        modifier = Modifier
            .pointerInput(Unit) {
                detectDragGestures { change, dragAmount ->
                    change.consume()
                    // If drag is positive toward the right, then add completionText to text
                    if (dragAmount.x > 100 && completionText != "") {
                        noteText.value = noteText.value.plus(completionText)
                        completion.value = ""
                        noteTextField.value = TextFieldValue(
                            noteText.value + completionText,
                            TextRange(noteText.value.length, noteText.value.length)
                        )
                    }
                }
            }
            .fillMaxWidth()
            .padding(top = 0.dp, bottom = 0.dp, start = 16.dp, end = 16.dp)
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

@Composable
fun NoteControls(launcher: ActivityResultLauncher<Intent>, snackbarHostState: SnackbarHostState) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val focusManager = LocalFocusManager.current
    Divider(modifier = Modifier
        .fillMaxWidth()
        .padding(top = 0.dp, bottom = 0.dp, start = 16.dp, end = 16.dp))
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 0.dp, bottom = 0.dp, start = 16.dp, end = 16.dp)
            .clip(RoundedCornerShape(0.dp, 0.dp, 10.dp, 10.dp))
            .background(Color(0xFFE5E5E5)),
    ) {
        IconButton(onClick = {
            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            }
            if (isSpeechRecognizerAvailable(context)) {
                launcher.launch(intent)
            } else {
                coroutineScope.launch {
                    snackbarHostState.showSnackbar("Speech recognizer not available")
                }
            }
        },
        modifier = Modifier.padding(top = 16.dp, bottom = 16.dp, start = 16.dp, end = 16.dp)) {
            Icon(
                ImageVector.vectorResource(id = R.drawable.mic_fill1_wght400_grad0_opsz48),
                contentDescription = "Record Voice Note",
                tint = Color(0xFF3694C9),
                modifier = Modifier.size(36.dp)
            )

        }
        Spacer(modifier = Modifier.weight(1f))
        Button(onClick = {
                focusManager.clearFocus() // Close the keyboard tray
                /* TODO; Make it so that the button gives the user feedback the the note is saved and update the db*/
                coroutineScope.launch {
                    snackbarHostState.showSnackbar("Note Saved Successfully")
                }
                         },
            shape = RoundedCornerShape(20.dp, 20.dp, 20.dp, 20.dp),
            modifier = Modifier.padding(top = 16.dp, bottom = 16.dp, start = 16.dp, end = 16.dp)
                ,
            colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF3694C9), contentColor = Color.White)

        ) {
            Text("Save")

        }
    }
}
fun isSpeechRecognizerAvailable(context: Context): Boolean {
    val packageManager = context.packageManager
    val speechRecognizerIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
    val resolveInfoList = packageManager.queryIntentActivities(speechRecognizerIntent, PackageManager.MATCH_DEFAULT_ONLY)
    return resolveInfoList.isNotEmpty()
}

    class ColorsTransformation(private var completionText: String) : VisualTransformation {
        override fun filter(text: AnnotatedString): TransformedText {
            return TransformedText(
                buildAnnotatedStringWithColors(text.toString(), completionText),
                OffsetMapping.Identity)
        }
    }

fun buildAnnotatedStringWithColors(text:String, completionText: String): AnnotatedString{
    val builder = AnnotatedString.Builder()
    // Add text string before completion text string to builder
    val textBeforeCompletion = text.substring(0, text.length - completionText.length)
    builder.append(textBeforeCompletion)
    // Add completion text string to builder with gray color
    val completionTextArray = completionText.split(" ")
    completionTextArray.forEach { word ->
        builder.withStyle(style = SpanStyle(color = Color.Gray)) {
            append("$word ")
        }
    }
    // append icon if completion text is not empty
    if (completionText != "") {
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