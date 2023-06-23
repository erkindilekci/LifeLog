package com.erkindilekci.lifelog.presentation.screen.homescreen

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.erkindilekci.lifelog.data.repository.MongoDb
import com.erkindilekci.lifelog.domain.repository.Diaries
import com.erkindilekci.lifelog.util.RequestState
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {

    var diaries: MutableState<Diaries> = mutableStateOf(RequestState.Idle)

    init {
        observeAllDiaries()
    }

    private fun observeAllDiaries() {
        viewModelScope.launch {
            MongoDb.getAllDiaries().collect { result ->
                diaries.value = result
            }
        }
    }
}
