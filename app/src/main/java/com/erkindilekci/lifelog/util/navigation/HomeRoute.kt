package com.erkindilekci.lifelog.util.navigation

import android.widget.Toast
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.erkindilekci.lifelog.BuildConfig
import com.erkindilekci.lifelog.presentation.component.DisplayAlertDialog
import com.erkindilekci.lifelog.presentation.screen.homescreen.HomeScreen
import com.erkindilekci.lifelog.presentation.screen.homescreen.HomeViewModel
import com.erkindilekci.lifelog.util.RequestState
import io.realm.kotlin.mongodb.App
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

fun NavGraphBuilder.homeRoute(
    navigateToWrite: () -> Unit,
    navigateToWriteWithArgs: (String) -> Unit,
    navigateToAuth: () -> Unit,
    onDataLoaded: () -> Unit
) {
    composable(Screen.Home.route) {
        val context = LocalContext.current
        val viewModel: HomeViewModel = hiltViewModel()
        val diaries by viewModel.diaries
        val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
        var signInDialogOpened by rememberSaveable { mutableStateOf(false) }
        var deleteAllDialogOpened by rememberSaveable { mutableStateOf(false) }
        val scope = rememberCoroutineScope()

        LaunchedEffect(key1 = diaries) {
            if (diaries !is RequestState.Loading) {
                onDataLoaded()
            }
        }

        HomeScreen(
            diaries = diaries,
            drawerState = drawerState,
            onSignOutClicked = { signInDialogOpened = true },
            onDeleteAllClicked = { deleteAllDialogOpened = true },
            navigateToWrite = navigateToWrite,
            navigateToWriteWithArgs = navigateToWriteWithArgs,
            onMenuClicked = {
                scope.launch {
                    drawerState.open()
                }
            }
        )

        DisplayAlertDialog(
            title = "Sign Out",
            message = "Are you sure you want to sign out?",
            dialogOpened = signInDialogOpened,
            onDialogClosed = { signInDialogOpened = false },
            onYesClicked = {
                scope.launch(Dispatchers.IO) {
                    val user = App.create(BuildConfig.APP_ID).currentUser
                    user?.logOut()
                    withContext(Dispatchers.Main) {
                        navigateToAuth()
                    }
                }
            }
        )

        DisplayAlertDialog(
            title = "Delete All",
            message = "Are you sure you want to permanently delete all logs?",
            dialogOpened = deleteAllDialogOpened,
            onDialogClosed = { deleteAllDialogOpened = false },
            onYesClicked = {
                viewModel.deleteAllDiaries(
                    onSuccess = {
                        Toast.makeText(
                            context,
                            "All logs have been deleted.",
                            Toast.LENGTH_SHORT
                        ).show()
                        scope.launch {
                            drawerState.close()
                        }
                    },
                    onError = {
                        Toast.makeText(
                            context,
                            if (it.message == "No Internet Connection.")
                                "Internet connection is needed for deleting all logs."
                            else it.localizedMessage,
                            Toast.LENGTH_SHORT
                        ).show()
                        scope.launch {
                            drawerState.close()
                        }
                    }
                )
            }
        )
    }
}
