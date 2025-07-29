package com.notes.shared.coreUi

interface ClipboardManager {
    fun getClipboardText(): String?

    fun setClipboardText(text: String)
}