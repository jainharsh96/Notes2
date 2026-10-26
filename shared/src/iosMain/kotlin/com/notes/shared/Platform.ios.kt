package com.notes.shared

class IOSPlatform : Platform {
    override val name : Platform.PlatFormName = Platform.PlatFormName.IOS
    override fun allowShowingDebugWindow(): Boolean {
        return true
    }
}

actual fun getPlatform(): Platform = IOSPlatform()