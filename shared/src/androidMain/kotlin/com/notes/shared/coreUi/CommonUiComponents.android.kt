package com.notes.shared.coreUi

import android.widget.Toast
import com.notes.shared.AndroidApplication

actual fun showToast(message: String) {
    Toast.makeText(AndroidApplication.context, message, Toast.LENGTH_SHORT).show()
}