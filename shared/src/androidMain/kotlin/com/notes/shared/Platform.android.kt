package com.notes.shared

import android.app.Application
import android.content.Context
import android.os.Environment

object AndroidApplication {
    lateinit var context: Application
}

class AndroidPlatform : Platform {
    override val name: Platform.PlatFormName = Platform.PlatFormName.ANDROID
    override fun allowShowingDebugWindow(): Boolean {
        return isDebugBuild()
    }
}

actual fun getPlatform(): Platform = AndroidPlatform()

fun setApplicationContext(appContext: Context){
    AndroidApplication.context = appContext.applicationContext as Application
}

fun getDatabasePath(fileName : String) = Environment.getExternalStorageDirectory().absolutePath + "/Notes2/$fileName"