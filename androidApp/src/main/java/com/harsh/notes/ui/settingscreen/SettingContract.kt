package com.harsh.notes.ui.settingscreen

interface SettingContract {
    sealed class Effect {
        object ClickBack : Effect()
        object RestoreData : Effect()
        object OpenDraftNote : Effect()
    }
}