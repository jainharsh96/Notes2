package com.notes.shared.coreUi

import platform.UIKit.UIPasteboard

class ClipboardManagerIOSImpl : ClipboardManager {

    override fun getClipboardText(): String? {
        return UIPasteboard.generalPasteboard.string
    }

    override fun setClipboardText(text: String) {
        UIPasteboard.generalPasteboard.string = text
    }
}