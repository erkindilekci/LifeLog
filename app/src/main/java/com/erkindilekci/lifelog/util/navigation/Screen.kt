package com.erkindilekci.lifelog.util.navigation

import com.erkindilekci.lifelog.util.Constants.WRITE_SCREEN_ARGUMENT_KEY

sealed class Screen(val route: String) {
    object Authentication : Screen("auth_screen")
    object Home : Screen("home_screen")
    object Write : Screen(
        "write_screen?$WRITE_SCREEN_ARGUMENT_KEY=" +
                "{$WRITE_SCREEN_ARGUMENT_KEY}"
    ) {
        fun passDiaryId(diaryId: String) = "write_screen?$WRITE_SCREEN_ARGUMENT_KEY=$diaryId"
    }
}
