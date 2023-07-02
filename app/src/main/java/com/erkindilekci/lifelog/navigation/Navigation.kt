package com.erkindilekci.lifelog.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.erkindilekci.addedit.navigation.addEditRoute
import com.erkindilekci.auth.navigation.authenticationRoute
import com.erkindilekci.home.navigation.homeRoute
import com.erkindilekci.util.Screen

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
            },
            navigateToWriteWithArgs = { diaryId ->
                navController.navigate(Screen.Write.passDiaryId(diaryId))
            }
        )
        addEditRoute(
            onBackClicked = { navController.popBackStack() }
        )
    }
}
