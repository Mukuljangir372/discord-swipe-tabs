package com.mukul.discord.swipe.tabs

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun TwoPanelsScreen() {
    Surface {
        val density = LocalDensity.current
        val screenWidth = LocalConfiguration.current.screenWidthDp.dp

        val screenCenterDp by remember(screenWidth) {
            mutableStateOf(
                screenWidth.times(
                    0.5f
                )
            )
        }

        val rightEndpointDp by remember(screenWidth) {
            mutableStateOf(
                screenWidth.times(
                    1.4f
                )
            )
        }

        val screenCenterPx by remember(screenCenterDp) {
            mutableStateOf(with(density) { screenCenterDp.toPx() })
        }

        val rightEndpointPx by remember(rightEndpointDp) {
            mutableStateOf(with(density) { rightEndpointDp.toPx() })
        }

        val swipeableState =
            rememberSwipeableState(initialValue = CenterScreenState.RIGHT_ANCHORED)

        val anchors = mapOf(
            screenCenterPx to CenterScreenState.CENTER,
            rightEndpointPx to CenterScreenState.RIGHT_ANCHORED
        )

        val drawerOnTop by remember {
            derivedStateOf {
                when (swipeableState.currentValue) {
                    CenterScreenState.LEFT_ANCHORED -> DrawerTypes.RIGHT
                    CenterScreenState.CENTER -> {
                        if (swipeableState.direction < 0) DrawerTypes.RIGHT
                        else DrawerTypes.LEFT
                    }
                    CenterScreenState.RIGHT_ANCHORED -> DrawerTypes.LEFT
                }
            }
        }

        val isAnyItemSelectedInServers: Boolean by remember { mutableStateOf(true) }

        val swipeableModifier by remember(isAnyItemSelectedInServers) {
            mutableStateOf(
                if (isAnyItemSelectedInServers) {
                    Modifier.swipeable(
                        state = swipeableState,
                        anchors = anchors,
                        thresholds = { _, _ -> FractionalThreshold(0.5f) },
                        orientation = Orientation.Horizontal
                    )
                } else {
                    Modifier
                }
            )
        }

        val leftDrawerModifier by remember(drawerOnTop, isAnyItemSelectedInServers) {
            mutableStateOf(
                swipeableModifier
                    .zIndex(0f)
                    .alpha(if (drawerOnTop == DrawerTypes.LEFT) 1f else 0f)
            )
        }

        val centerScreenOffset by remember {
            derivedStateOf {
                if (drawerOnTop == DrawerTypes.LEFT) {
                    (swipeableState.offset.value - screenCenterPx).roundToInt()
                } else {
                    0
                }
            }
        }


        Box(
            modifier = Modifier.fillMaxSize(),
        ) {
            // Left Panel ----------------------------------------------------
            Column(
                modifier = leftDrawerModifier
                    .align(Alignment.CenterStart)
                    .fillMaxHeight()
                    .fillMaxWidth(0.85f)
                    .background(Color.Black)
            ) {
                Text(text = "left panel screen")
            }

            // Center Panel ----------------------------------------------------
            val centerScreenZIndex by remember {
                derivedStateOf {
                    if (swipeableState.isAnimationRunning || swipeableState.currentValue == CenterScreenState.CENTER || swipeableState.progress.fraction in 0.05f..0.95f) 1f
                    else 0.5f
                }
            }

            AnimatedVisibility(
                modifier = swipeableModifier
                    .zIndex(centerScreenZIndex)
                    .offset { IntOffset(x = centerScreenOffset, y = 0) },
                visible = isAnyItemSelectedInServers,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Magenta)
                ) {
                    Text(text = "center panel screen")
                }
            }
        }
    }
}