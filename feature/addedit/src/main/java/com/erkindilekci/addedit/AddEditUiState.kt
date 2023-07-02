package com.erkindilekci.addedit

import com.erkindilekci.util.model.Diary
import com.erkindilekci.util.model.Mood
import io.realm.kotlin.types.RealmInstant

internal data class AddEditUiState(
    val selectedDiaryId: String? = null,
    val selectedDiary: Diary? = null,
    val title: String = "",
    val description: String = "",
    val mood: Mood = Mood.Neutral,
    val updatedDateTime: RealmInstant? = null
)
