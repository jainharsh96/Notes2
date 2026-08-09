package com.notes.shared.coreUi

import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.InterceptPlatformTextInput
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.notes.shared.coreUi.secureKeyboard.KeyBoardButton
import com.notes.shared.coreUi.secureKeyboard.KeyboardType
import com.notes.shared.coreUi.secureKeyboard.SecureFloatingKeyBoard
import kotlinx.coroutines.awaitCancellation


@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun SecureBasicTextField(
    modifier: Modifier = Modifier,
    useSecureKeyBoard: Boolean = true,
    keyboardType: KeyboardType,
    canHideKeyboard: Boolean = true,
    value: TextFieldValue,
    onValueChange: (TextFieldValue) -> Unit,  // returns the updated value of the text field (not a single key press)
    onPressKeyBoardAction: (KeyBoardButton.Action) -> Unit = {},
    textStyle: TextStyle = TextStyle.Default,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    decorationBox: @Composable (innerTextField: @Composable () -> Unit) -> Unit =
        @Composable { innerTextField -> innerTextField() },
) {
    val showSecureKeyBoard = remember(useSecureKeyBoard) { mutableStateOf(false) }
    val updatedValue by rememberUpdatedState(value)

    LaunchedEffect(Unit){
        if (useSecureKeyBoard) {
            showSecureKeyBoard.value = true
        }
    }

    InterceptPlatformTextInput(
        interceptor = { request, nextHandler ->
            if (useSecureKeyBoard) {
                awaitCancellation()
            } else {
                nextHandler.startInputMethod(request)
            }
        }
    ) {
        BasicTextField(
            modifier = modifier
                .then(if (useSecureKeyBoard) Modifier.pointerInput(useSecureKeyBoard) {
                    awaitEachGesture {
                        awaitFirstDown(pass = PointerEventPass.Initial)
                        if (showSecureKeyBoard.value.not()) {
                            showSecureKeyBoard.value = true
                        }
                    }
                } else Modifier),
            value = updatedValue,
            onValueChange = { newValue ->
                onValueChange(newValue)
            },
            textStyle = textStyle,
            decorationBox = decorationBox,
            visualTransformation = visualTransformation
        )
    }

    SecureFloatingKeyBoard(
        showKeyBoard = showSecureKeyBoard.value,
        keyboardType = keyboardType,
        onPressKey = { key ->
            when (key) {
                is KeyBoardButton.Action -> onPressKeyBoardAction(key)
                is KeyBoardButton.AlphaNumeric -> onValueChange(
                    SecureTextFieldUtil.insertChar(
                        key.char.toString(),
                        updatedValue
                    )
                )

                is KeyBoardButton.Back -> onValueChange(
                    SecureTextFieldUtil.handleBackspace(
                        updatedValue
                    )
                )

                is KeyBoardButton.ClipboardPaste -> onValueChange(
                    SecureTextFieldUtil.insertChar(
                        key.msg,
                        updatedValue
                    )
                )

                KeyBoardButton.HideKeyboard -> if (canHideKeyboard) showSecureKeyBoard.value = false
                is KeyBoardButton.Number -> onValueChange(
                    SecureTextFieldUtil.insertChar(
                        key.digit.toString(),
                        updatedValue
                    )
                )

                KeyBoardButton.Space -> onValueChange(
                    SecureTextFieldUtil.insertChar(
                        " ",
                        updatedValue
                    )
                )
            }
        }
    )
}


private object SecureTextFieldUtil {
    // --- Helper Functions for Custom Keyboard Logic ---

    /**
     * Inserts a character at the current cursor position or replaces selected text.
     */
    fun insertChar(char: String, currentValue: TextFieldValue): TextFieldValue {
        val text = currentValue.text
        val selection = currentValue.selection

        // Replace the highlighted text, or insert at the blinking cursor
        val newText = text.replaceRange(selection.min, selection.max, char)

        // Move the cursor to right after the inserted character
        val newCursorPosition = selection.min + char.length

        return TextFieldValue(
            text = newText,
            selection = TextRange(newCursorPosition)
        )
    }

    /**
     * Deletes the character right behind the cursor, or deletes highlighted text.
     */
    fun handleBackspace(currentValue: TextFieldValue): TextFieldValue {
        val text = currentValue.text
        val selection = currentValue.selection

        // Case 1: Text is highlighted. Delete just the highlighted portion.
        if (selection.min != selection.max) {
            val newText = text.removeRange(selection.min, selection.max)
            return TextFieldValue(text = newText, selection = TextRange(selection.min))
        }

        // Case 2: No text highlighted, but cursor is not at the very beginning. Delete 1 char back.
        if (selection.min > 0) {
            val newText = text.removeRange(selection.min - 1, selection.min)
            return TextFieldValue(text = newText, selection = TextRange(selection.min - 1))
        }

        // Case 3: Cursor is at the very beginning. Do nothing.
        return currentValue
    }
}

@Composable
@Preview
fun PreviewSecureBasicTextField() {
    var text by remember { mutableStateOf(TextFieldValue(text = "Enter text here")) }
    Column(modifier = Modifier.fillMaxSize()) {
        SecureBasicTextField(
            modifier = Modifier.padding(top = 12.dp),
            useSecureKeyBoard = true,
            keyboardType = KeyboardType.AlphaNumeric,
            canHideKeyboard = true,
            value = text,
            onValueChange = { text = it },
            textStyle = TextStyle(fontSize = 20.sp),
            onPressKeyBoardAction = {}
        )
    }
}