package com.erkindilekci.lifelog.util.navigation

import android.widget.Toast
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
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
        val viewModel: AddEditViewModel = hiltViewModel()
        val uiState by viewModel.uiState.collectAsState()
        val pagerState = rememberPagerState()
        val galleryState = viewModel.galleryState
        val context = LocalContext.current
        val pageNumber by remember {
            derivedStateOf { pagerState.currentPage }
        }

        LaunchedEffect(key1 = uiState) {
            println(uiState.selectedDiaryId)
        }

        AddEditScreen(
            uiState = uiState,
            pagerState = pagerState,
            galleryState = galleryState,
            moodName = { Mood.values()[pageNumber].name },
            onBackClicked = onBackClicked,
            onTitleChanged = { viewModel.updateTitle(it) },
            onDescriptionChanged = { viewModel.updateDescription(it) },
            onDateTimeUpdated = { viewModel.updateDateTime(it) },
            onDeleteConfirmed = {
                viewModel.deleteDiary(
                    onSuccess = {
                        Toast.makeText(
                            context,
                            "Deleted: ${uiState.title}",
                            Toast.LENGTH_SHORT
                        ).show()
                        onBackClicked()
                    },
                    onError = { errorMessage ->
                        Toast.makeText(
                            context,
                            errorMessage,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                )
            },
            onSaveClicked = {
                viewModel.upsertDiary(
                    diary = it.apply { mood = Mood.values()[pageNumber].name },
                    onSuccess = onBackClicked,
                    onError = { errorMessage ->
                        Toast.makeText(
                            context,
                            errorMessage,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                )
            },
            onImageSelected = { uri ->
                val type = context.contentResolver.getType(uri)?.split("/")?.last() ?: "jpg"
                viewModel.addImage(
                    image = uri,
                    imageType = type
                )
            },
            onImageDeleteClicked = { galleryState.removeImage(it) }
        )
    }
}
