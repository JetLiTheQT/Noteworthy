package com.example.material3test

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.*
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.BaselineShift
import com.example.material3test.ui.theme.Material3TestTheme

class MainActivity : ComponentActivity() {
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Material3TestTheme {
                Scaffold(
                    floatingActionButton = {
                        FloatingActionButton(onClick = {
                            val intent = intent
                            finish()
                            startActivity(intent)
                        }) {
                            Text("Reset")
                        }
                    }
                ) {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        TextInputThing()
                    }
                }
            }
        }
    }

    @Composable
    fun TextInputThing() {
        var text by remember { mutableStateOf("Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.\n\nUt enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. \n\nExcepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.") }
        var completionText by remember { mutableStateOf(" This is a test completion text. You can swipe to the right, or continue typing out this messsage to auto complete. If you hit the backspace, or enter a different character than from this message, the completion will be cleared.\n\nThis demo prevents you from moving the cursor into the suggested text section. If you tap on the suggested text, the cursor will move to the last character before the suggested text.\n\nAt the end of the suggested text, a \"Swipe\" icon is shown that goes away once the text is completed.") }
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
            label = { Text("Autocompleting Demo Title") },
            visualTransformation = ColorsTransformation(completionText),
            modifier = Modifier
            .pointerInput(Unit) {
                detectDragGestures { change, dragAmount ->
                    change.consume()
                    Log.d("Drag", dragAmount.toString())
                    // If drag is positive toward the right, then add completionText to text
                    if (dragAmount.x > 100 && completionText != "") {
                        text = text.plus(completionText)
                        completionText = ""
                        text1 = TextFieldValue(text + completionText, TextRange(text.length, text.length))
                    }
                }
            },
        )
    }
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