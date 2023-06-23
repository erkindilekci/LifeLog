package com.erkindilekci.lifelog.presentation.util

import androidx.compose.material3.DrawerValue
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.erkindilekci.lifelog.presentation.component.DisplayAlertDialog
import com.erkindilekci.lifelog.presentation.screen.authscreen.AuthenticationScreen
import com.erkindilekci.lifelog.presentation.screen.authscreen.AuthenticationViewModel
import com.erkindilekci.lifelog.presentation.screen.homescreen.HomeScreen
import com.erkindilekci.lifelog.presentation.screen.homescreen.HomeViewModel
import com.erkindilekci.lifelog.util.Constants.APP_ID
import com.erkindilekci.lifelog.util.Constants.WRITE_SCREEN_ARGUMENT_KEY
import com.erkindilekci.lifelog.util.RequestState
import com.stevdzasan.messagebar.rememberMessageBarState
import com.stevdzasan.onetap.rememberOneTapSignInState
import io.realm.kotlin.mongodb.App
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun Navigation(
    startDestination: String,
    navController: NavHostController,
    onDataLoaded: () -> Unit
) {
    NavHost(navController = navController, startDestination = startDestination) {
        authenticationRoute(
            onDataLoaded = onDataLoaded,
            navigateToHome = {
                navController.popBackStack()
                navController.navigate(Screen.Home.route)
            }
        )
        homeRoute(
            navigateToWrite = { navController.navigate(Screen.Write.route) },
            onDataLoaded = onDataLoaded,
            navigateToAuth = {
                navController.popBackStack()
                navController.navigate(Screen.Authentication.route)
            }
        )
        writeRoute()
    }
}

fun NavGraphBuilder.authenticationRoute(
    navigateToHome: () -> Unit,
    onDataLoaded: () -> Unit
) {
    composable(Screen.Authentication.route) {
        val viewModel: AuthenticationViewModel = viewModel()
        val authenticated by viewModel.authenticated
        val loadingState by viewModel.loadingState

        val oneTapState = rememberOneTapSignInState()
        val messageBarState = rememberMessageBarState()

        LaunchedEffect(key1 = Unit) {
            onDataLoaded()
        }

        AuthenticationScreen(
            oneTapSignInState = oneTapState,
            messageBarState = messageBarState,
            authenticated = authenticated,
            loadingState = loadingState,
            navigateToHome = navigateToHome,
            onDialogDismissed = { message ->
                messageBarState.addError(Exception(message))
                viewModel.updateLoadingState(false)
            },
            onTokenIdReceived = { tokenId ->
                viewModel.signInWithMongoAtlas(
                    tokenId = tokenId,
                    onError = { message ->
                        messageBarState.addError(Exception(message))
                        viewModel.updateLoadingState(false)
                    },
                    onSuccess = {
                        messageBarState.addSuccess("Authenticated")
                        viewModel.updateLoadingState(false)
                    }
                )
            },
            onButtonClicked = {
                oneTapState.open()
                viewModel.updateLoadingState(true)
            }
        )
    }
}

fun NavGraphBuilder.homeRoute(
    navigateToWrite: () -> Unit,
    navigateToAuth: () -> Unit,
    onDataLoaded: () -> Unit
) {
    composable(Screen.Home.route) {
        val viewModel: HomeViewModel = viewModel()
        val diaries by viewModel.diaries
        val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
        var dialogOpened by rememberSaveable { mutableStateOf(false) }
        val scope = rememberCoroutineScope()

        LaunchedEffect(key1 = diaries) {
            if (diaries !is RequestState.Loading) {
                onDataLoaded()
            }
        }

        HomeScreen(
            diaries = diaries,
            drawerState = drawerState,
            onSignOutClicked = { dialogOpened = true },
            navigateToWrite = navigateToWrite,
            onMenuClicked = {
                scope.launch {
                    drawerState.open()
                }
            }
        )

        DisplayAlertDialog(
            title = "Sign Out",
            message = "Are you sure you want to sign out?",
            dialogOpened = dialogOpened,
            onDialogClosed = { dialogOpened = false },
            onYesClicked = {
                scope.launch(Dispatchers.IO) {
                    val user = App.create(APP_ID).currentUser
                    user?.logOut()
                    withContext(Dispatchers.Main) {
                        navigateToAuth()
                    }
                }
            }
        )
    }
}

fun NavGraphBuilder.writeRoute() {
    composable(
        route = Screen.Write.route,
        arguments = listOf(navArgument(WRITE_SCREEN_ARGUMENT_KEY) {
            type = NavType.StringType
            nullable = true
            defaultValue = null
        })
    ) {

    }
}
