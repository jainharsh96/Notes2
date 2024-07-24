package com.notes.shared

import android.content.Context
import android.graphics.drawable.Drawable
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.core.content.ContextCompat

private lateinit var context: Context

class AndroidPlatform : Platform {
    override val name: String = "Android ${android.os.Build.VERSION.SDK_INT}"
}

actual fun getPlatform(): Platform = AndroidPlatform()

actual fun showToast(msg: String) {
    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
}

fun setApplicationContext(appContext: Context){
    context = appContext
}

actual fun getDrawable(byName: String): Any? {
    val drawableResId = context.resources.getIdentifier(byName, "drawable", context.packageName)
    return ContextCompat.getDrawable(context, drawableResId)
}

actual fun getDrawableId(byName: String): Any? {
    return context.resources.getIdentifier(byName, "drawable", context.packageName)
}

actual fun getColor(name: String): Color {
    val colorResId = context.resources.getIdentifier(name, "color", context.packageName)
    val colorInt = ContextCompat.getColor(context, colorResId)
    return Color(colorInt)
}

@Composable
actual fun getPainter(resource: String): Painter {
    return painterResource(getDrawableId(resource) as Int)   // TODO painterResource supported in shared module
}