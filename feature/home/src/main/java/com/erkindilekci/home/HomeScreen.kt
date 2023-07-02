package com.erkindilekci.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DrawerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.LayoutDirection
import com.erkindilekci.mongo.repository.Diaries
import com.erkindilekci.util.model.RequestState
import java.time.ZonedDateTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun HomeScreen(
    diaries: Diaries,
    drawerState: DrawerState,
    onSignOutClicked: () -> Unit,
    onDeleteAllClicked: () -> Unit,
    isDateSelected: Boolean,
    onDateSelected: (ZonedDateTime) -> Unit,
    onDateReset: () -> Unit,
    onMenuClicked: () -> Unit,
    navigateToWrite: () -> Unit,
    navigateToWriteWithArgs: (String) -> Unit,
) {
    var padding by remember { mutableStateOf(PaddingValues()) }
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(
        snapAnimationSpec = null,
        flingAnimationSpec = null
    )

    HomeNavigationDrawer(
        drawerState = drawerState,
        onSignOutClicked = onSignOutClicked,
        onDeleteAllClicked = onDeleteAllClicked
    ) {
        Scaffold(
            modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
            topBar = {
                HomeTopBar(
                    onMenuClicked = onMenuClicked,
                    scrollBehavior = scrollBehavior,
                    dateIsSelected = isDateSelected,
                    onDateReset = onDateReset,
                    onDateSelected = onDateSelected
                )
            },
            content = { paddingValues ->
                padding = paddingValues
                when (diaries) {
                    is RequestState.Error -> {
                        EmptyScreen(
                            title = "Error",
                            subtitle = diaries.error.localizedMessage ?: "Unknown error occurred!"
                        )
                    }

                    RequestState.Loading -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }

                    is RequestState.Success -> {
                        HomeContent(
                            paddingValues = paddingValues,
                            diaries = diaries.data,
                            onClick = navigateToWriteWithArgs
                        )
                    }

                    else -> {}
                }
            },
            floatingActionButton = {
                ExtendedFloatingActionButton(
                    modifier = Modifier.padding(end = padding.calculateEndPadding(LayoutDirection.Ltr)),
                    onClick = navigateToWrite,
                    text = { Text(text = "Add") },
                    icon = { Icon(Icons.Filled.Edit, "Add Log Icon") },
                )
            }
        )
    }
}
