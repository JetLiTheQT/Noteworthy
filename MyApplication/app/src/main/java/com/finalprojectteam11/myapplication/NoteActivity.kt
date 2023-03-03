package com.finalprojectteam11.myapplication

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.consumeAllChanges
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.BaselineShift
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.finalprojectteam11.myapplication.ui.theme.MyApplicationTheme
import java.sql.Wrapper

class NoteActivity : ComponentActivity() {
    var currentDisplayChoice by mutableStateOf(false)
    var searchQuery by mutableStateOf("")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyApplicationTheme {
                Scaffold(
                    topBar = {
                        TopAppBar(
                            title = { Text(text = "Add Note") },
                            navigationIcon = {
                                IconButton(onClick = {
                                    navigateUpTo(Intent(this@NoteActivity, MainActivity::class.java))
                                },) {
                                    Icon(Icons.Filled.ArrowBack, contentDescription = "Back", modifier = Modifier.padding(14.dp))
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
                                if (false) {
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
                            verticalArrangement = Arrangement.spacedBy(0.dp)
                        ) {
                            item {
                                ComposeNote()
                            }
                            item {
                                TextInputBox()
                            }
                            item {
                                NoteControls()
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
    fun ComposeNote() {
        TextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            label = { Text(text = "Note Title") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp, bottom = 16.dp, start = 16.dp, end = 16.dp)
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
    fun TextInputBox() {
        var text by remember { mutableStateOf("Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.\n\nUt enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat.. \n\nExcepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.") }
        var completionText by remember { mutableStateOf(" You can swipe to the right, or continue typing out this messsage to auto complete. Hit the backspace, or enter a different character than from this message to clear.\n\nThis demo prevents you from moving the cursor into the suggested text section. If you tap on the suggested text, the cursor will move to the last character before the suggested text.\n\nAt the end of the suggested text, a \"Swipe\" icon is shown that goes away once the text is completed.") }
        var text1 by remember { mutableStateOf(TextFieldValue(text + completionText, TextRange(text.length, text.length))) }

        TextField(
            value = text1,
            onValueChange = {
                val previousText = text.plus(completionText)
                if (it.text.length < previousText.length) {
                    text = it.text.substring(0, it.text.length - completionText.length)
                    completionText = ""
                    text1 = TextFieldValue(text + completionText, TextRange(it.selection.start, it.selection.end))
                }
                else if (it.text.length > previousText.length) {
                    val newText = it.text.substring(0, it.text.length - completionText.length)
                    if (previousText.startsWith(newText)) {
                        // If there is a match, find the difference and move it from completionText to text
                        val differenceLength = newText.length - text.length
                        val oldCompletionText = it.text.substring(it.text.length - completionText.length, it.text.length)
                        val newCompletionText = completionText.substring(differenceLength, completionText.length)
                        if (!completionText.contains(oldCompletionText)) {
                            text = newText
                            completionText = ""
                            text1 = TextFieldValue(text + completionText, TextRange(text.length, text.length))
                        } else {
                            text = newText
                            completionText = newCompletionText
                            text1 = TextFieldValue(text + completionText, TextRange(text.length, text.length))
                        }
                    } else {
                        text = it.text.substring(0, it.text.length - completionText.length)
                        completionText = ""
                        text1 = TextFieldValue(text + completionText, TextRange(text.length, text.length))
                    }
                } else {
                    if (it.text != text + completionText) {
                        text = it.text.substring(0, it.text.length - completionText.length)
                        completionText = ""
                        text1 = TextFieldValue(text + completionText, TextRange(text.length, text.length))
                    } else {
                        // if selectionEnd is less than index of completionText, then set selectionEnd to index of completionText
                        var selectionStart = it.selection.start
                        var selectionEnd = it.selection.end
                        if (it.selection.end > text.length) {
                            selectionEnd = text.length
                            if (it.selection.start > text.length) {
                                selectionStart = text.length
                            }
                        }
                        text1 = TextFieldValue(text + completionText, TextRange(selectionStart, selectionEnd))
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
                            text = text.plus(completionText)
                            completionText = ""
                            text1 = TextFieldValue(
                                text + completionText,
                                TextRange(text.length, text.length)
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
    fun NoteControls() {
        Divider(modifier = Modifier
            .fillMaxWidth()
            .padding(top = 0.dp, bottom = 0.dp, start = 16.dp, end = 16.dp))
        Row(modifier = Modifier
            .fillMaxWidth()
            .padding(top = 0.dp, bottom = 0.dp, start = 16.dp, end = 16.dp)
            .clip(RoundedCornerShape(0.dp, 0.dp, 10.dp, 10.dp))
            .background(Color(0xFFE5E5E5)),
        ) {
            IconButton(onClick = { /*TODO*/ },
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

    class ColorsTransformation(private var completionText: String) : VisualTransformation {
        override fun filter(text: AnnotatedString): TransformedText {
            return TransformedText(
                buildAnnotatedStringWithColors(text.toString(), completionText),
                OffsetMapping.Identity)
        }
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
                        NoteActivity::class.java
                    )
                )
            },
            backgroundColor = Color(0xFF3694C9),
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