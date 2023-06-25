package com.erkindilekci.lifelog.presentation.screen.addeditscreen

import android.net.Uri
import android.widget.Toast
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.erkindilekci.lifelog.data.model.Diary
import com.erkindilekci.lifelog.data.model.GalleryImage
import com.erkindilekci.lifelog.data.model.GalleryState
import com.erkindilekci.lifelog.data.model.Mood
import com.erkindilekci.lifelog.data.repository.MongoDb
import com.erkindilekci.lifelog.util.Constants.WRITE_SCREEN_ARGUMENT_KEY
import com.erkindilekci.lifelog.util.RequestState
import com.erkindilekci.lifelog.util.fetchImagesFromFirebase
import com.erkindilekci.lifelog.util.toRealmInstant
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.mongodb.kbson.ObjectId
import java.time.ZonedDateTime

class AddEditViewModel(
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    val galleryState = GalleryState()

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
                MongoDb.getSelectedDiary(diaryId = ObjectId.invoke(diaryId))
                    .catch { emit(RequestState.Error(Exception("Diary is already deleted."))) }
                    .collect { diary ->
                        if (diary is RequestState.Success) {
                            updateTitle(title = diary.data.title)
                            updateDescription(description = diary.data.description)
                            updateMood(mood = Mood.valueOf(diary.data.mood))
                            updateSelectedDiary(diary = diary.data)

                            fetchImagesFromFirebase(
                                remoteImagePaths = diary.data.images,
                                onImageDownload = { downloadedImage ->
                                    galleryState.addImage(
                                        GalleryImage(
                                            image = downloadedImage,
                                            remoteImagePath = extractImagePath(
                                                fullImageUrl = downloadedImage.toString()
                                            )
                                        )
                                    )
                                }
                            )
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

    fun updateDateTime(zonedDateTime: ZonedDateTime) {
        _uiState.update {
            it.copy(updatedDateTime = zonedDateTime.toInstant().toRealmInstant())
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
        val result = MongoDb.insertDiary(diary = diary.apply {
            if (uiState.value.updatedDateTime != null) {
                date = uiState.value.updatedDateTime!!
            }
        })
        if (result is RequestState.Success) {
            uploadImagesToFirebase()
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
                date = if (uiState.value.updatedDateTime != null) {
                    uiState.value.updatedDateTime!!
                } else uiState.value.selectedDiary!!.date
            }
        )
        if (result is RequestState.Success) {
            uploadImagesToFirebase()
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

    fun deleteDiary(
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            if (uiState.value.selectedDiaryId != null) {
                val result = MongoDb.deleteDiary(ObjectId.invoke(uiState.value.selectedDiaryId!!))

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
        }
    }

    fun addImage(image: Uri, imageType: String) {
        val remoteImagePath = "images/${FirebaseAuth.getInstance().currentUser?.uid}/" +
                "${image.lastPathSegment}-${System.currentTimeMillis()}.$imageType"
        galleryState.addImage(
            GalleryImage(
                image = image,
                remoteImagePath = remoteImagePath
            )
        )
    }

    private fun uploadImagesToFirebase() {
        val storage = FirebaseStorage.getInstance().reference
        galleryState.images.forEach { galleryImage ->
            val imagePath = storage.child(galleryImage.remoteImagePath)
            imagePath.putFile(galleryImage.image)
        }
    }

    private fun extractImagePath(fullImageUrl: String): String {
        val chunks = fullImageUrl.split("%2F")
        val imageName = chunks[2].split("?").first()
        return "images/${Firebase.auth.currentUser?.uid}/$imageName"
    }
}
