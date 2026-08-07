package com.notes.shared.coreUi

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.withStyle


@Composable
fun AnnotatedBasicTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    textStyle: TextStyle = TextStyle.Default,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    singleLine: Boolean = false,
    maxLines: Int = if (singleLine) 1 else Int.MAX_VALUE,
    minLines: Int = 1,
    onTextLayout: (TextLayoutResult) -> Unit = {},
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    cursorBrush: Brush = SolidColor(Color.Black),
    decorationBox: @Composable (innerTextField: @Composable () -> Unit) -> Unit =
        @Composable { innerTextField -> innerTextField() }
) {
    var textFieldValue = remember(value) { TextFieldValue(value, TextRange(value.length)) }
//    LaunchedEffect(value){
//        if (value != textFieldValue.text) {
//            textFieldValue = textFieldValue.copy(text = value)
//        }
//    }
    BasicTextField(
        enabled = enabled,
        value = textFieldValue,
        onValueChange = {
           // textFieldValue = it
            onValueChange(it.text)
        },
        modifier = modifier,
        textStyle = textStyle,
        decorationBox = decorationBox,
        readOnly = readOnly,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        maxLines = maxLines,
        minLines = minLines,
        singleLine = singleLine,
        visualTransformation = VisualTransformation.None,  //MarkdownVisualTransformation(),
        onTextLayout = onTextLayout,
        interactionSource = interactionSource,
        cursorBrush = cursorBrush,
    )
}


class MarkdownVisualTransformation : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val annotatedString = buildAnnotatedString {
            val rawText = text.text
            // Simple regex to find text between **
            val boldRegex = Regex("\\*\\*(.*?)\\*\\*")
            var lastIndex = 0

            boldRegex.findAll(rawText).forEach { match ->
                append(rawText.substring(lastIndex, match.range.first))
                withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                    append(match.groupValues[1])
                }
                lastIndex = match.range.last + 1
            }
            append(rawText.substring(lastIndex))
        }

        // todo this is buggy
        // OffsetMapping is required to map cursor position between raw and styled text
        return TransformedText(annotatedString, BoldOffsetMapping1(text.text))
    }
}

class BoldOffsetMapping1(private val rawText: String) : OffsetMapping {

    // Maps Cursor in Filtered Text -> Original Text
    override fun transformedToOriginal(offset: Int): Int {
        // This logic gets complex quickly because you have to
        // track every hidden character's index.
        return offset / 2 // Placeholder: Logic required here
    }

    // Maps Cursor in Original Text -> Filtered Text
    override fun originalToTransformed(offset: Int): Int {
        return offset / 2 // Placeholder: Logic required here
    }
}

//class BoldOffsetMapping(
//    private val changes: List<OffsetChange>,
//    private val originalLength: Int
//) : OffsetMapping {
//
//    // Maps Actual String (with **) -> Visual String (without **)
//    override fun originalToTransformed(offset: Int): Int {
//        var totalOffset = 0
//        for (change in changes) {
//            if (offset > change.originalIndex) {
//                // If the cursor is past a removed section, shift it left
//                totalOffset = change.cumulativeOffset
//            } else {
//                break
//            }
//        }
//        return (offset - totalOffset).coerceIn(0, originalLength)
//    }
//
//    // Maps Visual String -> Actual String
//    override fun transformedToOriginal(offset: Int): Int {
//        var totalOffset = 0
//        for (change in changes) {
//            // We need to find how many characters were removed
//            // BEFORE this visual position
//            if (offset + totalOffset >= change.originalIndex) {
//                totalOffset = change.cumulativeOffset
//            } else {
//                break
//            }
//        }
//        return (offset + totalOffset).coerceIn(0, originalLength)
//    }
//}