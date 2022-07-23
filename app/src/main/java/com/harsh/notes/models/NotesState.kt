package com.harsh.notes.models


sealed class NotesState {
    object NoData : NotesState()
    data class Notes(val notes: List<Note>, val confirmToDeleteNoteId : Int? = null) : NotesState()
}

sealed class NotesAction {
    object ClickBack : NotesAction()
    object AddNotes : NotesAction()
    object RecordNotes : NotesAction()
    object OpenSettings : NotesAction()
    object DismissConfirmToDeleteNote : NotesAction()
    data class OpenNote(val noteId: Int) : NotesAction()
    data class FetchNotes(val state: Int) : NotesAction()
    data class InsertNote(val note: Note) : NotesAction()
    data class ConfirmDeleteNote(val noteId: Int) : NotesAction()
    data class DeleteNote(val noteId: Int) : NotesAction()
    data class DraftNote(val noteId: Int) : NotesAction()
    data class RestoreNote(val noteId: Int) : NotesAction()
    // add load more notes action
}

sealed class CreateNoteAction {
    object ClickBack : CreateNoteAction()
    object ClickRecordNotes : CreateNoteAction()
    object ClickUndo : CreateNoteAction()
    object SaveNote : CreateNoteAction()
    object SavedNote : CreateNoteAction()
    object NoteRendered : CreateNoteAction()
    data class FetchNote(val noteId: Int?) : CreateNoteAction()
}

sealed class SettingAction {
    object ClickBack : SettingAction()
    object RestoreData : SettingAction()
    object OpenDraftNote : SettingAction()
}