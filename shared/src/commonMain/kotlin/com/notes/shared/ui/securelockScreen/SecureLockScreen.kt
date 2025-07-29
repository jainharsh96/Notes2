package com.notes.shared.ui.securelockScreen

import androidx.compose.foundation.background
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.notes.shared.ui.secureKeyboard.SecureNumberTypeKeyboard
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.collectLatest

@Composable
fun SecureLockScreen(
    state: SecureLockScreenContract.State,
    effect: SharedFlow<SecureLockScreenContract.SideEffect>,
    event: (SecureLockScreenContract.Event) -> Unit,
    onGoBack: () -> Unit,
    onGoForward: () -> Unit
) {
    LaunchedEffect(Unit) {
        effect.collectLatest { sideEffect ->
            when (sideEffect) {
                SecureLockScreenContract.SideEffect.GoBack -> {
                    onGoBack()
                }

                SecureLockScreenContract.SideEffect.GoForward -> {
                    onGoForward()
                }
            }
        }
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(top = 60.dp)
    ) {
        Text(
            modifier = Modifier.align(Alignment.CenterHorizontally),
            text = "Unlock Notes",
            fontSize = 24.sp,
            fontWeight = FontWeight.Medium,
            color = Color.Blue.copy(0.8f),
            textAlign = TextAlign.Center
        )
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 32.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                modifier = Modifier.align(Alignment.CenterHorizontally),
                text = state.getPasswordHeader(),
                fontSize = 18.sp,
                fontWeight = FontWeight.Normal,
                color = Color.Blue.copy(0.8f),
                textAlign = TextAlign.Center
            )
            BasicTextField(
                value = state.representedPassword,
                onValueChange = {},
                modifier = Modifier.fillMaxWidth().padding(top = 12.dp).focusable(true),
                textStyle = TextStyle(fontSize = 18.sp, textAlign = TextAlign.Center),
                enabled = false
            )
            Spacer(
                modifier = Modifier.fillMaxWidth().height(2.dp)
                    .background(color = Color.Black.copy(0.5f))
            )
        }
        SecureNumberTypeKeyboard(
            modifier = Modifier,
            enteredNumber = state.enteredPassword,
            onEnterNumber = {
                event(SecureLockScreenContract.Event.OnEnterPassword(it))
            },
            onClickAction = {
                event(SecureLockScreenContract.Event.OnClickAction(it))
            }
        )
    }
}

//@Composable
//@Preview
//fun PreviewSecureLockScreen() {
//    var mockState by remember { mutableStateOf(SecureLockScreenContract.State.initialState()) }
//    Box(modifier = Modifier.fillMaxSize()) {
//        SecureLockScreen(
//            state = mockState,
//            event = {
//                when(it){
//                    SecureLockScreenContract.Event.ClickBack -> {}
//                    is SecureLockScreenContract.Event.OnClickAction -> {
//                        mockState = mockState.copy(representedPassword = "*".repeat(it.password.length))
//                    }
//                    is SecureLockScreenContract.Event.OnEnterPassword -> {
//                        mockState = mockState.copy(representedPassword = "*".repeat(it.password.length))
//                    }
//                }
//            },
//            effect = MutableSharedFlow()
//        )
//    }
//}