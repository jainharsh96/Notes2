package com.example.harsh.Notes.NoteUtils

import android.Manifest
import android.content.ContentResolver
import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import com.harsh.notes.db.Note
import net.sqlcipher.database.SQLiteDatabase
import net.sqlcipher.database.SQLiteDatabaseHook
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


private val sSQLiteDatabaseHook: SQLiteDatabaseHook = object : SQLiteDatabaseHook {
    override fun preKey(database: SQLiteDatabase) {}
    override fun postKey(database: SQLiteDatabase) {
        // can remove this
        database.rawExecSQL("PRAGMA cipher_compatibility = 3;")
        database.rawExecSQL("PRAGMA cipher_page_size = 1024;")
        database.rawExecSQL("PRAGMA kdf_iter = 64000;")
        database.rawExecSQL("PRAGMA cipher_hmac_algorithm = HMAC_SHA1;")
        database.rawExecSQL("PRAGMA cipher_kdf_algorithm = PBKDF2_HMAC_SHA1;")
    }
}

private fun restoreData() {
    val list = mutableListOf<Note>()
    val FILE_PATH =
        Environment.getExternalStorageDirectory().absolutePath + "/Notes2/NotesDb.db"
    try {
        val db = SQLiteDatabase.openOrCreateDatabase(
            FILE_PATH,
            "thisispassword123!@#",
            null,
            sSQLiteDatabaseHook
        )
        val cursor = db.query("select * from Notes")
        while (cursor.moveToNext()) {
            val body = cursor.getString(1)
            val updated = cursor.getString(2)
            val status = cursor.getString(3)
            list.add(
                Note(
                    body = body,
                    createdDate = Date(updated.toLong()),
                    updatedDate = Date(updated.toLong()),
                    state = status.toIntOrNull() ?: 1
                )
            )
        }
        cursor.close()
    } catch (e: Exception) {
        Log.e("harshtag", e.toString())
    }
}