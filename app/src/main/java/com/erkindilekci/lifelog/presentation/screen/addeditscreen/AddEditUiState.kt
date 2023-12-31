package com.erkindilekci.lifelog.presentation.screen.addeditscreen

import com.erkindilekci.lifelog.data.model.Diary
import com.erkindilekci.lifelog.data.model.Mood
import io.realm.kotlin.types.RealmInstant

data class AddEditUiState(
    val selectedDiaryId: String? = null,
    val selectedDiary: Diary? = null,
    val title: String = "",
    val description: String = "",
    val mood: Mood = Mood.Neutral,
    val updatedDateTime: RealmInstant? = null
)
