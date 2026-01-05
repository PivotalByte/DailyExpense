package com.dailyexpense.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import com.dailyexpense.data.models.MainTab
import com.dailyexpense.navigation.Route
import com.dailyexpense.ui.components.CurvyBottomBar
import com.dailyexpense.ui.theme.LocalCustomColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(navController: NavHostController) {
    var selectedTab by rememberSaveable(
        stateSaver = androidx.compose.runtime.saveable.Saver(
            save = { tab ->
                when (tab) {
                    is MainTab.Dashboard -> 0
                    is MainTab.Transactions -> 1
                    is MainTab.Analytics -> 2
                    is MainTab.Account -> 3
                }
            },
            restore = { index ->
                when (index) {
                    0 -> MainTab.Dashboard
                    1 -> MainTab.Transactions
                    2 -> MainTab.Analytics
                    3 -> MainTab.Account
                    else -> MainTab.Dashboard
                }
            }
        )
    ) { mutableStateOf<MainTab>(value = MainTab.Dashboard) }
    Scaffold(
        floatingActionButton = {
            AnimatedVisibility(
                visible = selectedTab == MainTab.Dashboard || selectedTab == MainTab.Transactions,
                enter = scaleIn(
                    initialScale = 0f,
                    animationSpec = tween(durationMillis = 150)
                ),
                exit = scaleOut(
                    targetScale = 0f,
                    animationSpec = tween(durationMillis = 150)
                )
            ) {
                FloatingActionButton(
                    onClick = {
                        navController.navigate(Route.NewTransaction.route)
                    },
                    containerColor = LocalCustomColors.current.primaryColor
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add",
                        tint = Color.White
                    )
                }
            }
        },
        bottomBar = {
            CurvyBottomBar(
                selectedIndex = selectedTab,
                onItemSelected = { selectedTab = it }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues = padding),
            contentAlignment = Alignment.TopStart
        ) {
            when (selectedTab) {
                MainTab.Dashboard -> DashboardScreen()
                MainTab.Transactions -> TransactionListScreen()
                MainTab.Analytics -> AnalyticsScreen()
                MainTab.Account -> Text(text = "Current tab: ${stringResource(id = selectedTab.labelRes)}")
            }
        }
    }
}
