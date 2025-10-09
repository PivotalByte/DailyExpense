package com.dailyexpense.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.dailyexpense.data.models.MainTab
import com.dailyexpense.ui.theme.LocalCustomColors


@Composable
fun CurvyBottomBar(
    selectedIndex: MainTab,
    onItemSelected: (MainTab) -> Unit,
    modifier: Modifier = Modifier,
    showLabelsWhenSelected: Boolean = true,
) {

    val navItems = listOf(
        MainTab.Dashboard,
        MainTab.Transactions,
        MainTab.Analytics,
        MainTab.Account
    )

    NavigationBar(
        modifier = modifier
            .clip(shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
            .background(color = LocalCustomColors.current.bottomBarBg),
        containerColor = LocalCustomColors.current.bottomBarBg,
        tonalElevation = 0.dp,
    ) {
        navItems.forEachIndexed { index, item ->
            val selected = item == selectedIndex
            val tint by animateColorAsState(
                targetValue = if (selected) LocalCustomColors.current.primaryColor else Color.Gray,
                animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
                label = "iconTint"
            )
            NavigationBarItem(
                selected = selected,
                onClick = { onItemSelected(item) },
                icon = {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(size = 40.dp) // bigger touch target
                                .clip(shape = RoundedCornerShape(12.dp))
                                .clickable(
                                    indication = ripple(bounded = true, color = tint),
                                    interactionSource = remember { MutableInteractionSource() }
                                ) {
                                    onItemSelected(item)
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                painter = painterResource(id = item.icon),
                                contentDescription = stringResource(id = item.labelRes),
                                tint = tint,
                                modifier = Modifier.size(23.dp)
                            )
                        }
                        if (showLabelsWhenSelected) {
                            AnimatedContent(
                                targetState = selected,
                                transitionSpec = {
                                    fadeIn(animationSpec = spring(stiffness = Spring.StiffnessMediumLow))
                                        .togetherWith(exit = fadeOut(animationSpec = spring(stiffness = Spring.StiffnessMediumLow)))
                                },
                                label = "bottomBarLabel"
                            ) { isSelected ->
                                if (isSelected) {
                                    Text(
                                        text = stringResource(id = item.labelRes),
                                        style = MaterialTheme.typography.labelSmall,
                                    )
                                } else {
                                    Spacer(Modifier.height(height = 0.dp))
                                }
                            }
                        }
                    }
                },
                alwaysShowLabel = false,
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = tint,
                    unselectedIconColor = tint,
                    indicatorColor = Color.Transparent
                )
            )
        }
    }
}
