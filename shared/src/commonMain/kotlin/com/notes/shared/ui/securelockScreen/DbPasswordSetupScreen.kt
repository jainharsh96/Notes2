package com.notes.shared.ui.securelockScreen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.notes.shared.coreUi.secureKeyboard.KeyBoardButton
import com.notes.shared.coreUi.secureKeyboard.KeyboardType
import com.notes.shared.coreUi.secureKeyboard.SecureKeyBoard
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.seconds

@Composable
fun DbPasswordSetupScreen(
    modifier: Modifier = Modifier,
    setupNotesState: SetupNotesState,
    event: (SecureLockScreenContract.Event) -> Unit
) {
    Column(modifier = modifier.padding(horizontal = 12.dp)) {
        Text(
            modifier = Modifier.align(Alignment.CenterHorizontally),
            text = "Setup Notes",
            fontSize = 24.sp
        )

        if (setupNotesState.setupNotesFlow == null){
            Spacer(modifier = Modifier.height(48.dp))

            // setup local backup option
            if (setupNotesState.isLocalBackupPresent) {
                Row(modifier = Modifier.fillMaxWidth().clickable {
                    event(SecureLockScreenContract.Event.OnSelectedSetupNotesFlow(SetupNotesFlow.RESTORE_FROM_LOCAL))
                }, verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(
                        selected = false,
                        onClick = {

                        }
                    )
                    Text(
                        modifier = Modifier,
                        text = "Restore Notes from Phone Storage",
                        fontSize = 16.sp
                    )
                }
            }

            // setup cloud backup option
            if (setupNotesState.isCloudBackUpEnabled) {
                Row(modifier = Modifier.fillMaxWidth().clickable {
                    event(SecureLockScreenContract.Event.OnSelectedSetupNotesFlow(SetupNotesFlow.RESTORE_FROM_CLOUD))
                }, verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(
                        selected = false,
                        onClick = {

                        }
                    )
                    Text(
                        modifier = Modifier,
                        text = "Restore Notes from Google Drive",
                        fontSize = 16.sp
                    )
                }
            }

            // setup fresh notes option
            Row(modifier = Modifier.fillMaxWidth().clickable {
                event(SecureLockScreenContract.Event.OnSelectedSetupNotesFlow(SetupNotesFlow.SETUP_FRESH_NOTES))
            }, verticalAlignment = Alignment.CenterVertically) {
                RadioButton(
                    selected = false,
                    onClick = {

                    }
                )
                Column {
                    Text(modifier = Modifier, text = "Setup Fresh notes", fontSize = 16.sp)
                    Text(
                        modifier = Modifier.alpha(0.6f),
                        text = "This will erase all local data and will create new notes",
                        fontSize = 14.sp,
                        fontStyle = FontStyle.Italic
                    )
                }
            }
        } else {
            SetupDbPassword(
                setupNotesFlow = setupNotesState.setupNotesFlow,
                errorMsg = setupNotesState.errorMsg,
                onConfirm = {
                    event(SecureLockScreenContract.Event.OnConfirmNotesPassword(it))
                }
            )
        }
    }

    if (setupNotesState.isLoadingNotesFromCloud){
        Dialog(
            onDismissRequest = {
                // Handle dialog dismissal if needed
            }
        ){
            Column(modifier = Modifier.background(color = Color.White).padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally){
                CircularProgressIndicator(
                    modifier = Modifier
                )
                Text(modifier = Modifier.padding(top = 12.dp), text = "Downloading Notes...\nDo not close screen", fontSize = 16.sp, textAlign = TextAlign.Center)

            }
        }
    }
}

@Composable
private fun SetupDbPassword(
    setupNotesFlow: SetupNotesFlow,
    errorMsg: String?,
    onConfirm: (String) -> Unit
) {
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize().background(color = Color.White)) {
        Column(
            Modifier.padding(top = 100.dp).fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = setupNotesFlow.getFlowDescription(), fontSize = 16.sp)
            TextField(
                modifier = Modifier.padding(vertical = 24.dp),
                value = password,
                readOnly = true,
                onValueChange = { password = it },
                label = { Text("Enter Password") },
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                singleLine = true,
                isError = errorMsg?.isNotEmpty() == true,
                trailingIcon = {
                    val image = if (passwordVisible)
                        Icons.Default.Visibility
                    else Icons.Default.VisibilityOff

                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(imageVector = image, contentDescription = null)
                    }
                }
            )
            Button(
                modifier = Modifier.padding(top = 24.dp),
                onClick = {
                    if (password.isNotEmpty()) {
                        onConfirm(password)
                    }
                },
                contentPadding = PaddingValues(horizontal = 24.dp, vertical = 12.dp)
            ) {
                Text("OK")
            }
        }
        SecureKeyBoard(
            modifier = Modifier.align(Alignment.BottomCenter),
            keyboardType = KeyboardType.AlphaNumeric
        ) {
            when (it) {
                is KeyBoardButton.Action -> Unit
                is KeyBoardButton.AlphaNumeric -> password = password + it.char
                is KeyBoardButton.Back -> password = runCatching {
                    password.substring(
                        0,
                        password.length - 1
                    )
                }.getOrElse { "" }

                is KeyBoardButton.Number -> password = password + it.digit
                KeyBoardButton.Space -> password = "$password "
                KeyBoardButton.HideKeyboard -> Unit
                is KeyBoardButton.ClipboardPaste -> password = password + it.msg
            }
        }
    }
}

@Composable
@Preview
private fun PreviewDbPasswordSetupScreen() {
    var mockSetupNotesState by remember {
        mutableStateOf(
            SetupNotesState(
                isLocalBackupPresent = true,
                isCloudBackUpEnabled = true,
                setupNotesFlow = null,
                isLoadingNotesFromCloud = false
            )
        )
    }
    val scope = rememberCoroutineScope()
    DbPasswordSetupScreen(
        modifier = Modifier.fillMaxSize().border(width = 1.dp, color = Color.Red).background(color = Color.White),
        setupNotesState = mockSetupNotesState,
        event = {}
    )
}