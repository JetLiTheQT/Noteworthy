package com.finalprojectteam11.noteworthy

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.speech.RecognizerIntent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
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
    val snackbarHostState = remember { SnackbarHostState() }
    val launcher = rememberLauncherForActivityResult(contract = ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val matches = result.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            matches?.firstOrNull()?.let { recognizedText ->
                noteText.value = "${noteText.value} $recognizedText"
            }
        }
    }
        MyApplicationTheme {
            Box(modifier = Modifier.fillMaxSize()) {
                Scaffold(
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
                                TextInputBox(noteText)
                            }
                            item {
                                NoteControls(noteText, launcher,snackbarHostState)
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
                        .align(Alignment.TopCenter)
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
                style = TextStyle(
                    fontSize = 18.sp,
                    color = Color.Gray,
                ),
            )
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp, horizontal = 24.dp)
            .height(56.dp)
            .clip(RoundedCornerShape(8.dp)),
        colors = TextFieldDefaults.textFieldColors(
            backgroundColor = Color(0xFFF5F5F5),
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent,
            cursorColor = Color.Black,
            focusedLabelColor = Color.Black,
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
fun TextInputBox(noteText: MutableState<String>) {

    var text by remember { mutableStateOf("Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.\n\nUt enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat.. \n\nExcepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.") }
    var completionText by remember { mutableStateOf(" You can swipe to the right, or continue typing out this messsage to auto complete. Hit the backspace, or enter a different character than from this message to clear.\n\nThis demo prevents you from moving the cursor into the suggested text section. If you tap on the suggested text, the cursor will move to the last character before the suggested text.\n\nAt the end of the suggested text, a \"Swipe\" icon is shown that goes away once the text is completed.") }
    var text1 by remember { mutableStateOf(TextFieldValue(text + completionText, TextRange(text.length, text.length))) }

    TextField(
        value = noteText.value,
        onValueChange = {
                newValue -> noteText.value = newValue },
        label = { Text("Note Content") },
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        textStyle = MaterialTheme.typography.body1
    )
    Spacer(modifier = Modifier.height(8.dp))
}
//@Composable
//fun TextInputBox() {
//    var text by remember { mutableStateOf("Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.\n\nUt enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat.. \n\nExcepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.") }
//    var completionText by remember { mutableStateOf(" You can swipe to the right, or continue typing out this messsage to auto complete. Hit the backspace, or enter a different character than from this message to clear.\n\nThis demo prevents you from moving the cursor into the suggested text section. If you tap on the suggested text, the cursor will move to the last character before the suggested text.\n\nAt the end of the suggested text, a \"Swipe\" icon is shown that goes away once the text is completed.") }
//    var text1 by remember { mutableStateOf(TextFieldValue(text + completionText, TextRange(text.length, text.length))) }
//
//    TextField(
//        value = text1,
//        onValueChange = {
//            val previousText = text.plus(completionText)
//            if (it.text.length < previousText.length) {
//                text = it.text.substring(0, it.text.length - completionText.length)
//                completionText = ""
//                text1 = TextFieldValue(text + completionText, TextRange(it.selection.start, it.selection.end))
//            }
//            else if (it.text.length > previousText.length) {
//                val newText = it.text.substring(0, it.text.length - completionText.length)
//                if (previousText.startsWith(newText)) {
//                    // If there is a match, find the difference and move it from completionText to text
//                    val differenceLength = newText.length - text.length
//                    val oldCompletionText = it.text.substring(it.text.length - completionText.length, it.text.length)
//                    val newCompletionText = completionText.substring(differenceLength, completionText.length)
//                    if (!completionText.contains(oldCompletionText)) {
//                        text = newText
//                        completionText = ""
//                        text1 = TextFieldValue(text + completionText, TextRange(text.length, text.length))
//                    } else {
//                        text = newText
//                        completionText = newCompletionText
//                        text1 = TextFieldValue(text + completionText, TextRange(text.length, text.length))
//                    }
//                } else {
//                    text = it.text.substring(0, it.text.length - completionText.length)
//                    completionText = ""
//                    text1 = TextFieldValue(text + completionText, TextRange(text.length, text.length))
//                }
//            } else {
//                if (it.text != text + completionText) {
//                    text = it.text.substring(0, it.text.length - completionText.length)
//                    completionText = ""
//                    text1 = TextFieldValue(text + completionText, TextRange(text.length, text.length))
//                } else {
//                    // if selectionEnd is less than index of completionText, then set selectionEnd to index of completionText
//                    var selectionStart = it.selection.start
//                    var selectionEnd = it.selection.end
//                    if (it.selection.end > text.length) {
//                        selectionEnd = text.length
//                        if (it.selection.start > text.length) {
//                            selectionStart = text.length
//                        }
//                    }
//                    text1 = TextFieldValue(text + completionText, TextRange(selectionStart, selectionEnd))
//                }
//            }
//        },
//        label = { Text("Note Content") },
//        visualTransformation = ColorsTransformation(completionText),
//        modifier = Modifier
//            .pointerInput(Unit) {
//                detectDragGestures { change, dragAmount ->
//                    change.consume()
//                    // If drag is positive toward the right, then add completionText to text
//                    if (dragAmount.x > 100 && completionText != "") {
//                        text = text.plus(completionText)
//                        completionText = ""
//                        text1 = TextFieldValue(
//                            text + completionText,
//                            TextRange(text.length, text.length)
//                        )
//                    }
//                }
//            }
//            .fillMaxWidth()
//            .padding(top = 0.dp, bottom = 0.dp, start = 16.dp, end = 16.dp)
//            .clip(RoundedCornerShape(10.dp, 10.dp, 0.dp, 0.dp)),
//        colors = TextFieldDefaults.textFieldColors(
//            focusedLabelColor = Color.Black,
//            cursorColor = Color.Black,
//            focusedIndicatorColor = Color.Transparent,
//            unfocusedIndicatorColor = Color.Transparent,
//            disabledIndicatorColor = Color.Transparent,
//            backgroundColor = Color(0xFFE5E5E5),
//            unfocusedLabelColor = Color.Black,
//        )
//    )
//}
@Composable
fun NoteControls(noteText: MutableState<String>, launcher: ActivityResultLauncher<Intent>, snackbarHostState: SnackbarHostState) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
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
        Button(onClick = { /*TODO*/ },
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