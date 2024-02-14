package com.notes.shared

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform