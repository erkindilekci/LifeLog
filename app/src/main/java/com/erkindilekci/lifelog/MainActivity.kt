package com.erkindilekci.lifelog

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import androidx.navigation.compose.rememberNavController
import com.erkindilekci.lifelog.presentation.util.Navigation
import com.erkindilekci.lifelog.presentation.util.Screen
import com.erkindilekci.lifelog.presentation.util.theme.LifeLogTheme
import com.erkindilekci.lifelog.util.Constants.APP_ID
import io.realm.kotlin.mongodb.App

class MainActivity : ComponentActivity() {

    var keepSplashOpened = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen().setKeepOnScreenCondition {
            keepSplashOpened
        }
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
            LifeLogTheme {
                val navController = rememberNavController()
                Navigation(
                    startDestination = getStartDestination(),
                    navController = navController,
                    onDataLoaded = { keepSplashOpened = false }
                )
            }
        }
    }
}

private fun getStartDestination(): String {
    val user = App.create(APP_ID).currentUser
    return if (user != null && user.loggedIn) Screen.Home.route else Screen.Authentication.route
}
