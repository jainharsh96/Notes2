package com.notes.shared.coreUi.secureKeyboard

import notes2.shared.generated.resources.Res
import notes2.shared.generated.resources.ic_arrow_back_black_24dp
import org.jetbrains.compose.resources.DrawableResource

sealed class KeyboardType {
    object NumberOnly : KeyboardType()
    object AlphaNumeric : KeyboardType()
}

sealed class KeyBoardButton {
    data class Number(val digit: Int) : KeyBoardButton()
    data class AlphaNumeric(val char: Char) : KeyBoardButton()
    data class Action(val txt: String) : KeyBoardButton()  // todo impl action button type, like back, ok, paste, enter, etc. instead of using string
    data class Back(val icon: DrawableResource = Res.drawable.ic_arrow_back_black_24dp) : KeyBoardButton()
    object Space : KeyBoardButton()
    object HideKeyboard : KeyBoardButton()
    data class ClipboardPaste(val msg: String) : KeyBoardButton()

    companion object {
        fun numberTypeKeyboardAllButton() = listOf(
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
            Action("Ok")
        )

        fun getAllNumbers() = listOf(
            Number(1),
            Number(2),
            Number(3),
            Number(4),
            Number(5),
            Number(6),
            Number(7),
            Number(8),
            Number(9),
            Number(0),
        )

        fun getAllAlphabetized() = listOf(
            listOf(
                AlphaNumeric('q'),
                AlphaNumeric('w'),
                AlphaNumeric('e'),
                AlphaNumeric('r'),
                AlphaNumeric('t'),
                AlphaNumeric('y'),
                AlphaNumeric('u'),
                AlphaNumeric('i'),
                AlphaNumeric('o'),
                AlphaNumeric('p'),
            ),

            listOf(
                AlphaNumeric('a'),
                AlphaNumeric('s'),
                AlphaNumeric('d'),
                AlphaNumeric('f'),
                AlphaNumeric('g'),
                AlphaNumeric('h'),
                AlphaNumeric('j'),
                AlphaNumeric('k'),
                AlphaNumeric('l'),
            ),

            listOf(
                AlphaNumeric('z'),
                AlphaNumeric('x'),
                AlphaNumeric('c'),
                AlphaNumeric('v'),
                AlphaNumeric('b'),
                AlphaNumeric('n'),
                AlphaNumeric('m'),
            )
        )

        fun getAllSpecialChars() = listOf(
            listOf(
                AlphaNumeric('+'),
                AlphaNumeric('×'),
                AlphaNumeric('÷'),
                AlphaNumeric('='),
                AlphaNumeric('/'),
                AlphaNumeric('_'),
                AlphaNumeric('<'),
                AlphaNumeric('>'),
                AlphaNumeric('['),
                AlphaNumeric(']'),
            ),

            listOf(
                AlphaNumeric('!'),
                AlphaNumeric('@'),
                AlphaNumeric('#'),
                AlphaNumeric('$'),
                AlphaNumeric('%'),
                AlphaNumeric('^'),
                AlphaNumeric('&'),
                AlphaNumeric('*'),
                AlphaNumeric('('),
                AlphaNumeric(')'),
            ),

            listOf(
                AlphaNumeric('-'),
                AlphaNumeric('\''),
                AlphaNumeric('"'),
                AlphaNumeric(':'),
                AlphaNumeric(';'),
                AlphaNumeric(','),
                AlphaNumeric('?'),
            )
        )
    }
}

sealed class ActionButtonType {
    object Back : ActionButtonType()
    object Ok : ActionButtonType()
    object Paste : ActionButtonType()
    object Enter : ActionButtonType()
    data class Custom(val txt: String) : ActionButtonType()
}