package com.notes.shared.coreUi

import android.annotation.SuppressLint
import android.content.Context

class ClipboardManagerAndroidImpl(private val context: Context) : ClipboardManager {
    private val clipboardManager: android.content.ClipboardManager
        @SuppressLint("ServiceCast")
        get() = context.getSystemService(Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager

    override fun getClipboardText(): String? {
        return clipboardManager.primaryClip?.getItemAt(0)?.text?.toString()
    }

    override fun setClipboardText(text: String) {
        // todo
    }
}