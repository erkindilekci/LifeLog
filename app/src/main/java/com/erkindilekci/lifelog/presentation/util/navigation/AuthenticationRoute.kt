package com.erkindilekci.lifelog.presentation.util.navigation

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.erkindilekci.lifelog.presentation.screen.authscreen.AuthenticationScreen
import com.erkindilekci.lifelog.presentation.screen.authscreen.AuthenticationViewModel
import com.stevdzasan.messagebar.rememberMessageBarState
import com.stevdzasan.onetap.rememberOneTapSignInState

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
