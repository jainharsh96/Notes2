package com.notes.shared

interface NotesSyncManager {

    fun hasSupportSync() : Boolean

    suspend fun syncDataToCloud(bgSync : Boolean) : Result<String>

    suspend fun restoreDataFromCloud() : Result<String>
}

sealed class Result<out T> {
    data class Success<T>(val data : T) : Result<T>()
    data class Error(val msg : String = "") : Result<String>()
    data class Exception<T>(val exception: kotlin.Exception) : Result<T>()

    fun getResultMsg() : String {
        return when(this) {
            is Success -> data.toString()
            is Error -> msg
            is Exception<*> -> when(exception){
                is UserNotLoggedInException -> "User not logged in"
                else -> "Something went wrong"
            }
        }
    }
}

object UserNotLoggedInException : Exception()