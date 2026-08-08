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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.notes.shared.coreUi.SecureBasicTextField
import com.notes.shared.coreUi.secureKeyboard.KeyboardType
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
            SecureBasicTextField(
                value = state.enteredPassword,
                onValueChange = {
                    event(SecureLockScreenContract.Event.OnEnterPassword(it))
                },
                onPressKeyBoardAction = {
                    event(SecureLockScreenContract.Event.OnClickAction(state.enteredPassword))
                },
                modifier = Modifier.fillMaxWidth().padding(top = 12.dp).focusable(true),
                textStyle = TextStyle(fontSize = 18.sp, textAlign = TextAlign.Center),
                keyboardType = KeyboardType.NumberOnly,
                canHideKeyboard = false,
                visualTransformation = PasswordVisualTransformation(mask = '*')
            )
            Spacer(
                modifier = Modifier.fillMaxWidth().height(2.dp)
                    .background(color = Color.Black.copy(0.5f))
            )
        }
    }
}