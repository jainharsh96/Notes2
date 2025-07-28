package com.notes.shared.coreUi

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.notes.shared.painterResource
import notes2.shared.generated.resources.Res
import notes2.shared.generated.resources.ic_arrow_back_black_24dp

@Composable
fun SecureNumberTypeKeyboard(
    modifier: Modifier,
    enteredNumber : String,
    onEnterNumber: (String) -> Unit,
    onClickAction: (String) -> Unit
) {
    val keyBoardButtons = remember {
        KeyBoardButton.getButtonsSequenced()
    }

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Bottom
    ) {
        LazyVerticalGrid(
            modifier = Modifier,
            columns = GridCells.Fixed(3),
            contentPadding = PaddingValues(8.dp)
        ) {
            items(items = keyBoardButtons) {
                KeyboardButton(
                    button = it,
                    onClickButton = {
                        when (it) {
                            is KeyBoardButton.Action -> onClickAction(enteredNumber)
                            is KeyBoardButton.Back -> {
                                onEnterNumber(enteredNumber.dropLast(1))
                            }

                            is KeyBoardButton.Number -> {
                                onEnterNumber(enteredNumber + it.digit)
                            }
                        }
                    }
                )
            }
        }
    }
}

@Composable
private fun KeyboardButton(
    button: KeyBoardButton,
    onClickButton: (KeyBoardButton) -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        modifier = modifier
            .aspectRatio(2f)
            .padding(4.dp),
        shape = RoundedCornerShape(8.dp),
        colors = ButtonDefaults.buttonColors()
            .copy(containerColor = Color.Black.copy(alpha = 0.1f)),
        onClick = {
            onClickButton(button)
        }
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
                    painter = painterResource(Res.drawable.ic_arrow_back_black_24dp),
                    contentDescription = "",
                    modifier = Modifier
                        .size(24.dp),
                )
            }

            is KeyBoardButton.Number -> {
                Text(
                    text = button.digit.toString(),
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.Black.copy(0.8f),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

sealed class KeyBoardButton {
    data class Number(val digit: Int) : KeyBoardButton()
    data class Action(val txt: String) : KeyBoardButton()
    data class Back(val icon: Int = -1) : KeyBoardButton()

    companion object {
        fun getButtonsSequenced() = listOf(
            Number(1),
            Number(2),
            Number(3),
            Number(4),
            Number(5),
            Number(6),
            Number(7),
            Number(8),
            Number(9),
            Back(),
            Number(0),
            Action("Go")
        )
    }
}