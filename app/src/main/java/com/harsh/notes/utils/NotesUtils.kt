package com.example.harsh.Notes.NoteUtils

import android.Manifest
import android.content.ContentResolver
import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

fun getNotesAllPermissions() = arrayOf(
    Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest
        .permission.READ_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO
)

fun dateInFormate(date: Date, formate: String): String {
    return SimpleDateFormat(formate, Locale.getDefault()).format(date)
}

fun Date.formated() = dateInFormate(this, NOTE_DATE_FORMAT)

fun getFilePathToMediaID(songPath: String, context: Context): Long {
    var id: Long = 0
    val cr: ContentResolver = context.contentResolver
    val uri: Uri = MediaStore.Files.getContentUri("external")
    val selection = MediaStore.Audio.Media.DATA
    val selectionArgs = arrayOf(songPath)
    val projection = arrayOf(MediaStore.Audio.Media._ID)
    val cursor: Cursor? = cr.query(uri, projection, "$selection=?", selectionArgs, null)
    if (cursor != null) {
        while (cursor.moveToNext()) {
            val idIndex: Int = cursor.getColumnIndex(MediaStore.Audio.Media._ID)
            id = cursor.getString(idIndex).toLong()
        }
        cursor.close()
    }
    return id
}

fun getDBMediaUri(
    filePath: String,
    context: Context
): Uri {   // "/storage/emulated/0/tempPic/export_image.jpg"
    val tempFile = File(filePath)
    val mediaID = getFilePathToMediaID(tempFile.absolutePath, context)
    return ContentUris.withAppendedId(MediaStore.Images.Media.getContentUri("external"), mediaID)
}