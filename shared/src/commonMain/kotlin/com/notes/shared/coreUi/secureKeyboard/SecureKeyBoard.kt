package com.notes.shared.coreUi.secureKeyboard

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.notes.shared.NotesDependencies
import com.notes.shared.coreUi.nonScaledSp
import com.notes.shared.getScreenWidth
import com.notes.shared.painterResource
import com.notes.shared.utils.colorResource
import notes2.shared.generated.resources.Res
import notes2.shared.generated.resources.backspace_icon
import notes2.shared.generated.resources.colorActionButton
import notes2.shared.generated.resources.colorPrimaryDark
import notes2.shared.generated.resources.ic_arrow_back_black_24dp
import notes2.shared.generated.resources.keyboard_allcap_1
import notes2.shared.generated.resources.keyboard_allcap_2
import notes2.shared.generated.resources.keyboard_allcap_3
import notes2.shared.generated.resources.keyboard_arrow_down
import notes2.shared.generated.resources.keyboard_button_bg
import notes2.shared.generated.resources.keyboard_newline
import org.jetbrains.compose.resources.DrawableResource

private val secureKeyBoardPaddingValue = mutableStateOf(0.dp)

fun Modifier.secureKeyBoardPadding() = this.padding(bottom = secureKeyBoardPaddingValue.value)

@Composable
fun SecureFloatingKeyBoard(
    showKeyBoard : Boolean = true,
    keyboardType: KeyboardType,
    onPressKey: (KeyBoardButton) -> Unit
) {
    val visibleState = remember { MutableTransitionState(false) }
    visibleState.targetState = showKeyBoard
    if (visibleState.currentState || visibleState.targetState){
        Popup(
            properties = PopupProperties(focusable = false),
            popupPositionProvider = object : androidx.compose.ui.window.PopupPositionProvider {
                override fun calculatePosition(
                    anchorBounds: androidx.compose.ui.unit.IntRect,
                    windowSize: androidx.compose.ui.unit.IntSize,
                    layoutDirection: androidx.compose.ui.unit.LayoutDirection,
                    popupContentSize: androidx.compose.ui.unit.IntSize
                ): IntOffset {
                    val x = (windowSize.width - popupContentSize.width) / 2
                    val y = windowSize.height
                    return IntOffset(x, y)
                }
            },
            onDismissRequest = {
                // onPressKey(KeyBoardButton.HideKeyboard)
            }
        ) {
            DisposableEffect(Unit) {
                onDispose {
                    secureKeyBoardPaddingValue.value = 0.dp
                }
            }
            val density = LocalDensity.current
            AnimatedVisibility(
                modifier = Modifier,
                visibleState = visibleState,
                enter = slideInVertically(
                    // Sync this duration with your modifier's padding animation!
                    animationSpec = tween(250, easing = LinearEasing),
                    // Start exactly 1 full height below the screen
                    initialOffsetY = { fullHeight -> fullHeight }
                ),
                exit = slideOutVertically(
                    animationSpec = tween(250, easing = LinearEasing),
                    // Slide down exactly 1 full height
                    targetOffsetY = { fullHeight -> fullHeight }
                )
            ) {
                SecureKeyBoard(
                    modifier = Modifier.fillMaxWidth().onGloballyPositioned {
                        val rect = it.boundsInWindow()
                        val paddingInt = (rect.bottom - rect.top).toInt().coerceIn(0, it.size.height)
                        secureKeyBoardPaddingValue.value = with(density) { paddingInt.toDp() }
                    },
                    keyboardType = keyboardType,
                    onPressKey = onPressKey
                )
            }
        }
    }
}

@Composable
fun SecureKeyBoard(
    modifier: Modifier = Modifier,
    keyboardType: KeyboardType,
    onPressKey: (KeyBoardButton) -> Unit
) {
    CompositionLocalProvider(
        LocalKeyBoardBgColor provides colorResource(Res.string.keyboard_button_bg),
        LocalKeyBoardButtonBgColor provides Color.White,
        LocalKeyBoardButtonColor provides colorResource(Res.string.colorPrimaryDark),
        LocalKeyBoardActionButtonColor provides colorResource(Res.string.colorActionButton)
    ){
        when (keyboardType) {
            KeyboardType.NumberOnly -> {
                SecureNumberTypeKeyboard(modifier = modifier.background(color = LocalKeyBoardBgColor.current), onPressKey = onPressKey)
            }

            KeyboardType.AlphaNumeric -> {
                SecureAlphaNumericTypeKeyboard(modifier = modifier.background(color = LocalKeyBoardBgColor.current), onPressKey = onPressKey)
            }
        }
    }
}


@Composable
private fun SecureNumberTypeKeyboard(
    modifier: Modifier = Modifier,
    onPressKey: (KeyBoardButton) -> Unit
) {
    val keyBoardButtons = remember {
        KeyBoardButton.numberTypeKeyboardAllButton()
    }
    val hapticFeedback = LocalHapticFeedback.current
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Bottom
    ) {
        LazyVerticalGrid(
            modifier = Modifier.widthIn(max = 400.dp),
            columns = GridCells.Fixed(3),
            contentPadding = PaddingValues(8.dp)
        ) {
            items(items = keyBoardButtons) {
                NumberKeyboardButton(
                    button = it,
                    onPressKey = {
                        hapticFeedback.performHapticFeedback(HapticFeedbackType.KeyboardTap)
                        onPressKey(it)
                    }
                )
            }
        }
    }
}

@Composable
private fun NumberKeyboardButton(
    button: KeyBoardButton,
    onPressKey: (KeyBoardButton) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .padding(4.dp)
            .aspectRatio(2f)
            .clip(RoundedCornerShape(8.dp))
            .clickable { onPressKey(button) }
            .background(color = LocalKeyBoardButtonBgColor.current),
        contentAlignment = Alignment.Center
    ) {
        when (button) {
            is KeyBoardButton.Action -> {
                Text(
                    text = button.txt,
                    fontSize = 18.nonScaledSp,
                    fontWeight = FontWeight.SemiBold,
                    color = LocalKeyBoardActionButtonColor.current,
                    textAlign = TextAlign.Center
                )
            }

            is KeyBoardButton.Back -> {
                Icon(
                    painter = painterResource(Res.drawable.ic_arrow_back_black_24dp),
                    contentDescription = "",
                    modifier = Modifier
                        .size(24.dp),
                    tint = LocalKeyBoardButtonColor.current
                )
            }

            is KeyBoardButton.Number -> {
                Text(
                    text = button.digit.toString(),
                    fontSize = 24.nonScaledSp,
                    fontWeight = FontWeight.SemiBold,
                    color = LocalKeyBoardButtonColor.current,
                    textAlign = TextAlign.Center
                )
            }

            else -> Unit
        }
    }
}

@Composable
private fun SecureAlphaNumericTypeKeyboard(
    modifier: Modifier = Modifier,
    onPressKey: (KeyBoardButton) -> Unit
) {

    val numericButtons = remember {
        KeyBoardButton.getAllNumbers()
    }

    val allAlphaNumerics = remember {
        KeyBoardButton.getAllAlphabetized()
    }

    val allSpecialChars = remember {
        KeyBoardButton.getAllSpecialChars()
    }

    var allCapButtonState by remember {
        mutableStateOf(0)
    }

    var showSpecialChar by remember {
        mutableStateOf(false)
    }

    val backButton by remember {
        mutableStateOf(KeyBoardButton.Back(Res.drawable.backspace_icon))
    }

    val newLineCharButton by remember {
        mutableStateOf(KeyBoardButton.AlphaNumeric('\n'))
    }

    var row2Buttons by remember { mutableStateOf(allAlphaNumerics[0]) }
    var row3Buttons by remember { mutableStateOf(allAlphaNumerics[1]) }
    var row4Buttons by remember { mutableStateOf(allAlphaNumerics[2]) }
    val hapticFeedback = LocalHapticFeedback.current

    val onPressKeyInternal = remember(allCapButtonState) {
        { keyboard: KeyBoardButton ->
            hapticFeedback.performHapticFeedback(HapticFeedbackType.KeyboardTap)
            onPressKey(keyboard)
            if (allCapButtonState == 1) {
                allCapButtonState = 0
            }
        }
    }

    LaunchedEffect(key1 = allCapButtonState, key2 = showSpecialChar) {
        if (showSpecialChar) {
            row2Buttons = allSpecialChars[0]
            row3Buttons = allSpecialChars[1]
            row4Buttons = allSpecialChars[2]
        } else {
            row2Buttons =
                allAlphaNumerics[0].map { it.copy(char = if (allCapButtonState == 0) it.char.lowercaseChar() else it.char.uppercaseChar()) }
            row3Buttons =
                allAlphaNumerics[1].map { it.copy(char = if (allCapButtonState == 0) it.char.lowercaseChar() else it.char.uppercaseChar()) }
            row4Buttons =
                allAlphaNumerics[2].map { it.copy(char = if (allCapButtonState == 0) it.char.lowercaseChar() else it.char.uppercaseChar()) }
        }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(top = 12.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        ShowClipboardData(
            modifier = Modifier.padding(bottom = 8.dp).fillMaxWidth(),
            onSelectData = { onPressKeyInternal(KeyBoardButton.ClipboardPaste(it)) }
        )

        AlphaNumericButtonsRow(
            modifier = Modifier,
            buttons = numericButtons
        ) {
            onPressKeyInternal(it)
        }

        AlphaNumericButtonsRow(
            modifier = Modifier,
            buttons = row2Buttons
        ) {
            onPressKeyInternal(it)
        }

        AlphaNumericButtonsRow(
            modifier = Modifier,
            buttons = row3Buttons
        ) {
            onPressKeyInternal(it)
        }

        Row(
            modifier = Modifier.widthIn(max = 480.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            if (showSpecialChar.not()) {
                IconButton(
                    iconDrawable = getAllCapButtonRes(allCapButtonState),
                    tint = getAllCapButtonTintColor(allCapButtonState),
                    onClick = {
                        hapticFeedback.performHapticFeedback(HapticFeedbackType.KeyboardTap)
                        allCapButtonState =
                            if (allCapButtonState >= 2) 0 else allCapButtonState + 1
                    },
                    modifier = Modifier
                        .padding(4.dp)
                        .width(40.dp),
                    paddingModifier = Modifier.padding(vertical = 12.dp, horizontal = 4.dp)
                )
            } else {
                Spacer(modifier = Modifier.width(20.dp))
            }
            AlphaNumericButtonsRow(
                modifier = Modifier,
                buttons = row4Buttons
            ) {
                onPressKeyInternal(it)
            }

            IconButton(
                iconDrawable = backButton.icon,
                onClick = { onPressKeyInternal(backButton) },
                paddingModifier = Modifier.padding(vertical = 12.dp, horizontal = 12.dp),
                modifier = Modifier
                    .padding(4.dp)
            )
        }

        Row(
            modifier = Modifier.widthIn(max = 480.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AlphaNumericButton(
                button = if (showSpecialChar) KeyBoardButton.Action("ABC") else KeyBoardButton.Action(
                    "!#1"
                ),
                onPressKey = {
                    hapticFeedback.performHapticFeedback(HapticFeedbackType.KeyboardTap)
                    showSpecialChar = showSpecialChar.not()
                             },
                modifier = Modifier
                    .padding(4.dp)
                    .width(60.dp)
            )
            AlphaNumericButton(
                button = KeyBoardButton.AlphaNumeric(','),
                onPressKey = { onPressKeyInternal(KeyBoardButton.AlphaNumeric(',')) },
                modifier = Modifier
                    .padding(4.dp)
                    .width(30.dp)
            )
            AlphaNumericButton(
                button = KeyBoardButton.Space,
                onPressKey = { onPressKeyInternal(KeyBoardButton.Space) },
                modifier = Modifier
                    .padding(4.dp)
                    .weight(1f)
            )
            AlphaNumericButton(
                button = KeyBoardButton.AlphaNumeric('.'),
                onPressKey = { onPressKeyInternal(KeyBoardButton.AlphaNumeric('.')) },
                modifier = Modifier
                    .padding(4.dp)
                    .width(30.dp)
            )
            IconButton(
                iconDrawable = Res.drawable.keyboard_newline,
                onClick = { onPressKeyInternal(newLineCharButton) },
                modifier = Modifier
                    .padding(4.dp)
                    .width(60.dp),
                paddingModifier = Modifier.padding(vertical = 12.dp),
            )
        }

        Row(
            modifier = Modifier.widthIn(max = 480.dp).fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            Icon(
                painter = org.jetbrains.compose.resources.painterResource(Res.drawable.keyboard_arrow_down),
                contentDescription = "",
                modifier = Modifier.padding(end = 20.dp, top = 4.dp, bottom = 4.dp)
                    .clip(CircleShape)
                    .clickable { onPressKeyInternal(KeyBoardButton.HideKeyboard) }
                    .padding(8.dp)
                    .size(24.dp),
                tint = LocalKeyBoardButtonColor.current
            )
        }
    }
}

@Composable
private fun ShowClipboardData(modifier: Modifier, onSelectData: (String) -> Unit) {
    var data by remember { mutableStateOf("") }
    val lifecycleOwner = LocalLifecycleOwner.current
    val clipboardManager = remember { NotesDependencies.clipboardManager }
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME || event == Lifecycle.Event.ON_START) {
                val clipboardText = clipboardManager?.getClipboardText().orEmpty()
                if (clipboardText.isNotEmpty() && clipboardText != data) {
                    data = clipboardManager?.getClipboardText().orEmpty()
                }
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }
    if (data.isNotEmpty()) {
        Row(
            modifier = modifier,
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .background(color = LocalKeyBoardButtonBgColor.current)
                    .clickable { onSelectData(data) }
                    .padding(vertical = 8.dp, horizontal = 16.dp),
                text = data,
                fontSize = 12.nonScaledSp,
                fontWeight = FontWeight.Medium,
                color = LocalKeyBoardButtonColor.current,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun AlphaNumericButtonsRow(
    modifier: Modifier = Modifier,
    buttons: List<KeyBoardButton>,
    onPressKey: (KeyBoardButton) -> Unit,
) {
    val screenWidth = getScreenWidth()
    val buttonSize = screenWidth / 10
    val buttonWidth = with(LocalDensity.current) { buttonSize.toDp() } - 8.dp
    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        buttons.forEach { button ->
            AlphaNumericButton(
                button = button,
                onPressKey = { onPressKey(button) },
                modifier = Modifier
                    .padding(4.dp)
                    .widthIn(max = 40.dp)
                    .width(buttonWidth)
            )
        }
    }
}

private fun getAllCapButtonRes(allCapState: Int) = when (allCapState) {
    0 -> Res.drawable.keyboard_allcap_1
    1 -> Res.drawable.keyboard_allcap_2
    else -> Res.drawable.keyboard_allcap_3
}

@Composable
private fun getAllCapButtonTintColor(allCapState: Int) = when (allCapState) {
    0 -> LocalKeyBoardButtonColor.current
    1 -> LocalKeyBoardActionButtonColor.current
    else -> LocalKeyBoardButtonColor.current
}


@Composable
private fun AlphaNumericButton(
    modifier: Modifier = Modifier,
    button: KeyBoardButton,
    onPressKey: (KeyBoardButton) -> Unit
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(4.dp))
            .clickable {
                onPressKey(button)
            }
            .background(color = LocalKeyBoardButtonBgColor.current)
            .padding(vertical = 10.dp, horizontal = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        when (button) {
            is KeyBoardButton.Action -> {
                Text(
                    text = button.txt,
                    fontSize = 18.nonScaledSp,
                    fontWeight = FontWeight.SemiBold,
                    color = LocalKeyBoardActionButtonColor.current,
                    textAlign = TextAlign.Center
                )
            }

            is KeyBoardButton.Back -> {
                Icon(
                    painter = org.jetbrains.compose.resources.painterResource(button.icon),
                    contentDescription = "",
                    modifier = Modifier.padding(horizontal = 8.dp)
                        .size(24.dp),
                    tint = LocalKeyBoardButtonColor.current
                )
            }

            is KeyBoardButton.Number -> {
                Text(
                    text = button.digit.toString(),
                    fontSize = 22.nonScaledSp,
                    fontWeight = FontWeight.SemiBold,
                    color = LocalKeyBoardButtonColor.current,
                    textAlign = TextAlign.Center
                )
            }

            is KeyBoardButton.AlphaNumeric -> {
                Text(
                    text = button.char.toString(),
                    fontSize = 22.nonScaledSp,
                    fontWeight = FontWeight.SemiBold,
                    color = LocalKeyBoardButtonColor.current,
                    textAlign = TextAlign.Center
                )
            }

            KeyBoardButton.Space -> {
                Text(
                    text = "Space",
                    fontSize = 18.nonScaledSp,
                    fontWeight = FontWeight.Normal,
                    color = LocalKeyBoardButtonColor.current,
                    textAlign = TextAlign.Center
                )
            }

            KeyBoardButton.HideKeyboard -> Unit
            is KeyBoardButton.ClipboardPaste -> Unit
        }
    }
}

@Composable
private fun IconButton(
    iconDrawable: DrawableResource,
    modifier: Modifier = Modifier,
    paddingModifier: Modifier,
    onClick: () -> Unit,
    tint : Color = LocalKeyBoardButtonColor.current
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(4.dp))
            .clickable(onClick = onClick)
            .background(color = LocalKeyBoardButtonBgColor.current)
            .then(paddingModifier),
        contentAlignment = Alignment.Center
    )
    {
        Icon(
            painter = org.jetbrains.compose.resources.painterResource(iconDrawable),
            contentDescription = "",
            modifier = Modifier
                .size(24.dp),
            tint = tint
        )
    }
}

private val LocalKeyBoardBgColor = compositionLocalOf { Color.White }
private val LocalKeyBoardButtonBgColor = compositionLocalOf { Color.Black.copy(alpha = 0.1f) }
private val LocalKeyBoardButtonColor = compositionLocalOf { Color.Black.copy(0.8f) }
private val LocalKeyBoardActionButtonColor = compositionLocalOf { Color.Blue.copy(0.8f) }


@Composable
@Preview
private fun PreviewSecureNumberTypeKeyboard() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.BottomCenter) {
        SecureKeyBoard(
            modifier = Modifier,
            keyboardType = KeyboardType.NumberOnly,
        ) {
            // Handle button click for preview
        }
    }
}

@Composable
@PreviewScreenSizes
private fun PreviewSecureAlphaNumericKeyboard() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.BottomCenter) {
        SecureKeyBoard(
            modifier = Modifier,
            keyboardType = KeyboardType.AlphaNumeric,
        ) {
            // Handle button click for preview
        }
    }
}