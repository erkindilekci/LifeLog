package com.erkindilekci.lifelog.presentation.util.navigation

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.erkindilekci.lifelog.data.model.Mood
import com.erkindilekci.lifelog.presentation.screen.addeditscreen.AddEditScreen
import com.erkindilekci.lifelog.presentation.screen.addeditscreen.AddEditViewModel
import com.erkindilekci.lifelog.util.Constants

@OptIn(ExperimentalFoundationApi::class)
fun NavGraphBuilder.writeRoute(
    onBackClicked: () -> Unit
) {
    composable(
        route = Screen.Write.route,
        arguments = listOf(navArgument(Constants.WRITE_SCREEN_ARGUMENT_KEY) {
            type = NavType.StringType
            nullable = true
            defaultValue = null
        })
    ) {
        val viewModel: AddEditViewModel = viewModel()
        val uiState by viewModel.uiState.collectAsState()
        val pagerState = rememberPagerState()
        val pageNumber by remember {
            derivedStateOf { pagerState.currentPage }
        }

        LaunchedEffect(key1 = uiState) {
            println(uiState.selectedDiaryId)
        }

        AddEditScreen(
            uiState = uiState,
            pagerState = pagerState,
            moodName = { Mood.values()[pageNumber].name },
            onBackClicked = onBackClicked,
            onTitleChanged = { viewModel.updateTitle(it) },
            onDescriptionChanged = { viewModel.updateDescription(it) },
            onDeleteConfirmed = {},
            onSaveClicked = {
                viewModel.upsertDiary(
                    diary = it.apply { mood = Mood.values()[pageNumber].name },
                    onSuccess = onBackClicked,
                    onError = {}
                )
            }
        )
    }
}
