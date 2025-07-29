package com.notes.shared.ui.secureKeyboard

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.notes.shared.getScreenWidth
import notes2.shared.generated.resources.Res
import notes2.shared.generated.resources.backspace_icon
import notes2.shared.generated.resources.keyboard_allcap_1
import notes2.shared.generated.resources.keyboard_allcap_2
import notes2.shared.generated.resources.keyboard_allcap_3
import notes2.shared.generated.resources.keyboard_arrow_down
import notes2.shared.generated.resources.keyboard_newline
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource

@Composable
fun SecureAlphaNumericTypeKeyboard(
    modifier: Modifier = Modifier,
    onClickButton: (KeyBoardButton) -> Unit
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

    val onClickButtonInternal = remember(allCapButtonState) {
        { keyboard: KeyBoardButton ->
            onClickButton(keyboard)
            if (allCapButtonState == 1) {
                row2Buttons =
                    allAlphaNumerics[0].map { it.copy(char = it.char.lowercaseChar()) }
                row3Buttons =
                    allAlphaNumerics[1].map { it.copy(char = it.char.lowercaseChar()) }
                row4Buttons =
                    allAlphaNumerics[2].map { it.copy(char = it.char.lowercaseChar()) }
                allCapButtonState = 0
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AlphaNumericButtonsRow(
            modifier = Modifier,
            buttons = numericButtons
        ) {
            onClickButtonInternal(it)
        }

        AlphaNumericButtonsRow(
            modifier = Modifier,
            buttons = row2Buttons
        ) {
            onClickButtonInternal(it)
        }

        AlphaNumericButtonsRow(
            modifier = Modifier,
            buttons = row3Buttons
        ) {
            onClickButtonInternal(it)
        }

        Row(
            modifier = Modifier.widthIn(max = 480.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            if (showSpecialChar.not()) {
                IconButton(
                    iconDrawable = getAllCapButtonRes(allCapButtonState),
                    modifier = Modifier
                        .padding(4.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .clickable {
                            allCapButtonState =
                                if (allCapButtonState >= 2) 0 else allCapButtonState + 1
                            if (allCapButtonState == 0) {
                                row2Buttons =
                                    allAlphaNumerics[0].map { it.copy(char = it.char.lowercaseChar()) }
                                row3Buttons =
                                    allAlphaNumerics[1].map { it.copy(char = it.char.lowercaseChar()) }
                                row4Buttons =
                                    allAlphaNumerics[2].map { it.copy(char = it.char.lowercaseChar()) }
                            } else {
                                row2Buttons =
                                    allAlphaNumerics[0].map { it.copy(char = it.char.uppercaseChar()) }
                                row3Buttons =
                                    allAlphaNumerics[1].map { it.copy(char = it.char.uppercaseChar()) }
                                row4Buttons =
                                    allAlphaNumerics[2].map { it.copy(char = it.char.uppercaseChar()) }
                            }
                        }
                        .background(
                            color = Color.Black.copy(alpha = 0.1f),
                            shape = RoundedCornerShape(4.dp)
                        )
                        .width(40.dp)
                        .padding(vertical = 12.dp)
                )
            } else {
                Spacer(modifier = Modifier.width(20.dp))
            }
            AlphaNumericButtonsRow(
                modifier = Modifier,
                buttons = row4Buttons
            ) {
                onClickButtonInternal(it)
            }

            AlphaNumericButton(
                button = backButton,
                modifier = Modifier
                    .padding(4.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .clickable { onClickButtonInternal(backButton) }
                    .background(
                        color = Color.Black.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(4.dp)
                    )
                    .width(40.dp)
                    .padding(vertical = 12.dp)
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
                modifier = Modifier
                    .padding(4.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .clickable {
                        showSpecialChar = showSpecialChar.not()
                        if (showSpecialChar) {
                            row2Buttons = allSpecialChars[0]
                            row3Buttons = allSpecialChars[1]
                            row4Buttons = allSpecialChars[2]
                        } else {
                            if (allCapButtonState == 0) {
                                row2Buttons =
                                    allAlphaNumerics[0].map { it.copy(char = it.char.lowercaseChar()) }
                                row3Buttons =
                                    allAlphaNumerics[1].map { it.copy(char = it.char.lowercaseChar()) }
                                row4Buttons =
                                    allAlphaNumerics[2].map { it.copy(char = it.char.lowercaseChar()) }
                            } else {
                                row2Buttons =
                                    allAlphaNumerics[0].map { it.copy(char = it.char.uppercaseChar()) }
                                row3Buttons =
                                    allAlphaNumerics[1].map { it.copy(char = it.char.uppercaseChar()) }
                                row4Buttons =
                                    allAlphaNumerics[2].map { it.copy(char = it.char.uppercaseChar()) }
                            }
                        }
                    }
                    .background(
                        color = Color.Black.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(4.dp)
                    )
                    .width(60.dp)
                    .padding(vertical = 12.dp)
            )
            AlphaNumericButton(
                button = KeyBoardButton.AlphaNumeric(','),
                modifier = Modifier
                    .padding(4.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .clickable {
                        onClickButtonInternal(KeyBoardButton.AlphaNumeric(','))
                    }
                    .background(
                        color = Color.Black.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(4.dp)
                    )
                    .width(30.dp)
                    .padding(vertical = 12.dp)
            )
            AlphaNumericButton(
                button = KeyBoardButton.Space,
                modifier = Modifier
                    .padding(4.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .clickable { onClickButtonInternal(KeyBoardButton.Space) }
                    .background(
                        color = Color.Black.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(4.dp)
                    )
                    .weight(1f)
                    .padding(vertical = 12.dp)
            )
            AlphaNumericButton(
                button = KeyBoardButton.AlphaNumeric('.'),
                modifier = Modifier
                    .padding(4.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .clickable {
                        onClickButtonInternal(KeyBoardButton.AlphaNumeric('.'))
                    }
                    .background(
                        color = Color.Black.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(4.dp)
                    )
                    .width(30.dp)
                    .padding(vertical = 12.dp)
            )
            IconButton(
                iconDrawable = Res.drawable.keyboard_newline,
                modifier = Modifier
                    .padding(4.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .clickable { onClickButtonInternal(newLineCharButton) }
                    .background(
                        color = Color.Black.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(4.dp)
                    )
                    .width(60.dp)
                    .padding(vertical = 12.dp)
            )
        }

        Row(
            modifier = Modifier.widthIn(max = 480.dp).fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            AlphaNumericButton(
                button = KeyBoardButton.HideKeyboard,
                modifier = Modifier.padding(end = 24.dp, top = 24.dp, bottom = 12.dp)
                    .clip(CircleShape)
                    .clickable { onClickButtonInternal(KeyBoardButton.HideKeyboard) }
                    .padding(8.dp)
            )
        }
    }
}

@Composable
private fun AlphaNumericButtonsRow(
    modifier: Modifier = Modifier,
    buttons: List<KeyBoardButton>,
    onClickButton: (KeyBoardButton) -> Unit,
) {
    val screenWidth = getScreenWidth()
    val buttonSize = screenWidth / 10
    val buttonWidth = with(LocalDensity.current) { buttonSize.toDp() } - 8.dp
    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        buttons.forEach { button ->
            AlphaNumericButton(
                button = button,
                modifier = Modifier
                    .padding(4.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .clickable { onClickButton(button) }
                    .background(
                        color = Color.Black.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(4.dp)
                    )
                    .widthIn(max = 40.dp)
                    .width(buttonWidth)
                    .padding(vertical = 12.dp)
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
private fun AlphaNumericButton(
    button: KeyBoardButton,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        when (button) {
            is KeyBoardButton.Action -> {
                Text(
                    text = button.txt,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.Blue.copy(0.8f),
                    textAlign = TextAlign.Center
                )
            }

            is KeyBoardButton.Back -> {
                Image(
                    painter = painterResource(button.icon),
                    contentDescription = "",
                    modifier = Modifier
                        .size(24.dp),
                )
            }

            is KeyBoardButton.Number -> {
                Text(
                    text = button.digit.toString(),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.Black.copy(0.8f),
                    textAlign = TextAlign.Center
                )
            }

            is KeyBoardButton.AlphaNumeric -> {
                Text(
                    text = button.char.toString(),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.Black.copy(0.8f),
                    textAlign = TextAlign.Center
                )
            }

            KeyBoardButton.Space -> {
                Text(
                    text = "Space",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.Black.copy(0.8f),
                    textAlign = TextAlign.Center
                )
            }

            KeyBoardButton.HideKeyboard -> {
                Image(
                    painter = painterResource(Res.drawable.keyboard_arrow_down),
                    contentDescription = "",
                    contentScale = ContentScale.FillBounds,
                    modifier = Modifier.size(24.dp),
                )
            }
        }
    }
}

@Composable
private fun IconButton(
    iconDrawable: DrawableResource,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(iconDrawable),
            contentDescription = "",
            contentScale = ContentScale.FillBounds,
            modifier = Modifier
                .size(24.dp),
        )
    }
}