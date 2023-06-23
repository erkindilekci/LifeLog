package com.erkindilekci.lifelog.presentation.screen.addeditscreen

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.erkindilekci.lifelog.data.model.Diary
import com.erkindilekci.lifelog.data.model.Mood
import com.erkindilekci.lifelog.data.repository.MongoDb
import com.erkindilekci.lifelog.util.Constants.WRITE_SCREEN_ARGUMENT_KEY
import com.erkindilekci.lifelog.util.RequestState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.mongodb.kbson.ObjectId

class AddEditViewModel(
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddEditUiState())
    val uiState: StateFlow<AddEditUiState> = _uiState.asStateFlow()

    init {
        getDiaryId()
        fetchSelectedDiary()
    }

    private fun getDiaryId() {
        _uiState.update {
            it.copy(selectedDiaryId = savedStateHandle.get<String>(WRITE_SCREEN_ARGUMENT_KEY))
        }
    }

    private fun fetchSelectedDiary() {
        uiState.value.selectedDiaryId?.let { diaryId ->
            viewModelScope.launch(Dispatchers.Main) {
                MongoDb.getSelectedDiary(diaryId = ObjectId.invoke(diaryId)).collect { diary ->
                    if (diary is RequestState.Success) {
                        updateTitle(title = diary.data.title)
                        updateDescription(description = diary.data.description)
                        updateMood(mood = Mood.valueOf(diary.data.mood))
                        updateSelectedDiary(diary = diary.data)
                    }
                }
            }
        }
    }

    fun updateTitle(title: String) {
        _uiState.update {
            it.copy(title = title)
        }
    }

    fun updateDescription(description: String) {
        _uiState.update {
            it.copy(description = description)
        }
    }

    private fun updateMood(mood: Mood) {
        _uiState.update {
            it.copy(mood = mood)
        }
    }

    private fun updateSelectedDiary(diary: Diary) {
        _uiState.update {
            it.copy(selectedDiary = diary)
        }
    }

    private suspend fun insertDiary(
        diary: Diary,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val result = MongoDb.insertDiary(diary = diary)
        if (result is RequestState.Success) {
            withContext(Dispatchers.Main) {
                onSuccess()
            }
        } else if (result is RequestState.Error) {
            withContext(Dispatchers.Main) {
                onError(result.error.localizedMessage ?: "Unknown error occurred!")
            }
        }
    }

    private suspend fun updateDiary(
        diary: Diary,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val result = MongoDb.updateDiary(
            diary.apply {
                _id = ObjectId.invoke(uiState.value.selectedDiaryId!!)
                date = uiState.value.selectedDiary!!.date
            }
        )
        if (result is RequestState.Success) {
            withContext(Dispatchers.Main) {
                onSuccess()
            }
        } else if (result is RequestState.Error) {
            withContext(Dispatchers.Main) {
                onError(result.error.localizedMessage ?: "Unknown error occurred!")
            }
        }
    }

    fun upsertDiary(
        diary: Diary,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            if (uiState.value.selectedDiary != null) {
                updateDiary(diary, onSuccess, onError)
            } else {
                insertDiary(diary, onSuccess, onError)
            }
        }
    }
}
