package com.erkindilekci.lifelog.presentation.screen.addeditscreen

import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.erkindilekci.lifelog.R
import com.erkindilekci.lifelog.data.model.Diary
import com.erkindilekci.lifelog.data.model.GalleryImage
import com.erkindilekci.lifelog.data.model.GalleryState
import com.erkindilekci.lifelog.data.model.Mood
import com.erkindilekci.lifelog.presentation.component.GalleryRow
import com.erkindilekci.lifelog.presentation.component.GalleryUploader
import com.erkindilekci.lifelog.presentation.ui.theme.Shapes
import io.realm.kotlin.ext.toRealmList
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AddEditContent(
    paddingValues: PaddingValues,
    pagerState: PagerState,
    uiState: AddEditUiState,
    galleryState: GalleryState,
    title: String,
    onTitleChanged: (String) -> Unit,
    description: String,
    onDescriptionChanged: (String) -> Unit,
    onSaveClicked: (Diary) -> Unit,
    onImageSelected: (Uri) -> Unit,
    onImageClicked: (GalleryImage) -> Unit
) {
    val scrollState = rememberScrollState()
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    val scope = rememberCoroutineScope()

    LaunchedEffect(key1 = scrollState.maxValue) {
        scrollState.scrollTo(scrollState.maxValue)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .imePadding()
            .navigationBarsPadding()
            .padding(top = paddingValues.calculateTopPadding())
            .padding(bottom = 24.dp)
            .padding(horizontal = 24.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            modifier = Modifier
                .verticalScroll(scrollState)
                .weight(1f)
        ) {
            Spacer(modifier = Modifier.height(30.dp))

            HorizontalPager(
                state = pagerState,
                pageCount = Mood.values().size
            ) { page ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                        .padding(end = 16.dp)
                        .align(Alignment.CenterHorizontally)
                ) {
                    AsyncImage(
                        modifier = Modifier
                            .size(120.dp)
                            .align(Alignment.Center),
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(Mood.values()[page].icon)
                            .crossfade(true)
                            .build(),
                        contentDescription = "Mood Icon"
                    )
                    if (page == 0 && uiState.selectedDiaryId == null) {
                        Icon(
                            painter = painterResource(id = R.drawable.swipe_left),
                            contentDescription = "Swipe Left Icon",
                            modifier = Modifier
                                .size(24.dp)
                                .align(Alignment.CenterEnd)
                        )
                    }
                    if (page == Mood.values().size - 1 && uiState.selectedDiaryId == null) {
                        Icon(
                            painter = painterResource(id = R.drawable.swipe_right),
                            contentDescription = "Swipe Left Icon",
                            modifier = Modifier
                                .size(24.dp)
                                .align(Alignment.CenterStart)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(30.dp))

            TextField(
                value = title,
                onValueChange = onTitleChanged,
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text(text = "Title") },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Unspecified,
                    unfocusedIndicatorColor = Color.Unspecified,
                    disabledIndicatorColor = Color.Unspecified,
                    focusedPlaceholderColor = MaterialTheme.colorScheme.onSurface.copy(0.38f),
                    unfocusedPlaceholderColor = MaterialTheme.colorScheme.onSurface.copy(0.38f),
                ),
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(
                    onNext = {
                        scope.launch {
                            scrollState.animateScrollTo(Int.MAX_VALUE)
                            focusManager.moveFocus(FocusDirection.Down)
                        }
                    }
                ),
                maxLines = 1,
                singleLine = true
            )

            TextField(
                value = description,
                onValueChange = onDescriptionChanged,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                placeholder = { Text(text = "Enter your log") },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Unspecified,
                    unfocusedIndicatorColor = Color.Unspecified,
                    disabledIndicatorColor = Color.Unspecified,
                    focusedPlaceholderColor = MaterialTheme.colorScheme.onSurface.copy(0.38f),
                    unfocusedPlaceholderColor = MaterialTheme.colorScheme.onSurface.copy(0.38f),
                ),
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(
                    onNext = { focusManager.clearFocus() }
                )
            )
        }

        Column(verticalArrangement = Arrangement.Bottom) {
            Spacer(modifier = Modifier.height(12.dp))

            if (uiState.selectedDiaryId == null) {
                GalleryUploader(
                    galleryState = galleryState,
                    onImageSelected = onImageSelected,
                    onAddClicked = { focusManager.clearFocus() },
                    onImageClicked = onImageClicked
                )
            } else {
                GalleryRow(
                    galleryState = galleryState,
                    onImageSelected = onImageSelected,
                    onAddClicked = { focusManager.clearFocus() },
                    onImageClicked = onImageClicked
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                shape = Shapes.small,
                onClick = {
                    if (title.trim().isNotEmpty() && description.trim().isNotEmpty()) {
                        onSaveClicked(
                            Diary().apply {
                                this.title = title
                                this.description = description
                                this.images =
                                    galleryState.images.map { it.remoteImagePath }.toRealmList()
                            }
                        )
                    } else {
                        Toast.makeText(
                            context,
                            "Field(s) can't be empty!",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            ) {
                Text(text = "Save")
            }
        }
    }
}
