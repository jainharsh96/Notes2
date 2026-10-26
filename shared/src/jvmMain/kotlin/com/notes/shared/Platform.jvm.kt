package com.notes.shared


class JvmPlatform : Platform {
    override val name: Platform.PlatFormName = Platform.PlatFormName.JVM_DESKTOP
    override fun allowShowingDebugWindow(): Boolean {
        return true
    }
}

actual fun getPlatform(): Platform {
    return JvmPlatform()
}