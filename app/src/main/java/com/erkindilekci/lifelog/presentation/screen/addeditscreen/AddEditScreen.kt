package com.erkindilekci.lifelog.presentation.screen.addeditscreen

import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.pager.PagerState
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.erkindilekci.lifelog.data.model.Diary
import com.erkindilekci.lifelog.data.model.Mood

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AddEditScreen(
    uiState: AddEditUiState,
    pagerState: PagerState,
    onBackClicked: () -> Unit,
    onDeleteConfirmed: () -> Unit,
    onTitleChanged: (String) -> Unit,
    onDescriptionChanged: (String) -> Unit,
    moodName: () -> String,
    onSaveClicked: (Diary) -> Unit
) {
    LaunchedEffect(key1 = uiState.mood) {
        val targetPage = Mood.valueOf(uiState.mood.name).ordinal
        pagerState.animateScrollToPage(
            page = targetPage,
            animationSpec = tween(durationMillis = 500, easing = FastOutLinearInEasing)
        )
    }

    Scaffold(
        topBar = {
            AddEditTopBar(
                selectedDiary = uiState.selectedDiary,
                onBackClicked = onBackClicked,
                onDeleteConfirmed = onDeleteConfirmed,
                moodName = moodName
            )
        },
        content = { paddingValues ->
            AddEditContent(
                paddingValues = paddingValues,
                pagerState = pagerState,
                title = uiState.title,
                onTitleChanged = onTitleChanged,
                description = uiState.description,
                onDescriptionChanged = onDescriptionChanged,
                onSaveClicked = onSaveClicked
            )
        }
    )
}
