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
fun LeftTwoPanelsScreen() {
    Surface {
        val density = LocalDensity.current
        val screenWidth = LocalConfiguration.current.screenWidthDp.dp

        val screenCenterDp by remember(screenWidth) {
            mutableStateOf(screenWidth.times(0.5f))
        }

        val leftEndpointDp by remember(screenWidth) {
            mutableStateOf(screenWidth.times(-1.4f))
        }

        val screenCenterPx by remember(screenCenterDp) {
            mutableStateOf(with(density) { screenCenterDp.toPx() })
        }

        val leftEndpointPx by remember(leftEndpointDp) {
            mutableStateOf(with(density) { leftEndpointDp.toPx() })
        }

        val leftSwipeableState =
            rememberSwipeableState(initialValue = CenterScreenState.LEFT_ANCHORED)

        val leftAnchors = mapOf(
            screenCenterPx to CenterScreenState.CENTER,
            leftEndpointPx to CenterScreenState.LEFT_ANCHORED
        )

        val leftDrawerOnTop by remember {
            derivedStateOf {
                when (leftSwipeableState.currentValue) {
                    CenterScreenState.LEFT_ANCHORED -> DrawerTypes.RIGHT
                    CenterScreenState.CENTER -> {
                        if (leftSwipeableState.direction < 0) DrawerTypes.RIGHT
                        else DrawerTypes.LEFT
                    }
                    CenterScreenState.RIGHT_ANCHORED -> DrawerTypes.LEFT
                }
            }
        }

        val isAnyItemSelectedInServers: Boolean by remember { mutableStateOf(true) }

        val leftSwipeableModifier by remember(isAnyItemSelectedInServers) {
            mutableStateOf(
                if (isAnyItemSelectedInServers) {
                    Modifier.swipeable(
                        state = leftSwipeableState,
                        anchors = leftAnchors,
                        thresholds = { _, _ -> FractionalThreshold(0.5f) },
                        orientation = Orientation.Horizontal
                    )
                } else {
                    Modifier
                }
            )
        }

        val leftDrawerModifier by remember(leftDrawerOnTop, isAnyItemSelectedInServers) {
            mutableStateOf(
                leftSwipeableModifier
                    .zIndex(0f)
                    .alpha(1f)
                    .offset {
                        if (leftDrawerOnTop == DrawerTypes.LEFT) {
                            IntOffset(leftSwipeableState.offset.value.roundToInt(), 0)
                        } else {
                            IntOffset.Zero
                        }
                    }
            )
        }

        val centerScreenOffset by remember {
            derivedStateOf {
                if (leftDrawerOnTop == DrawerTypes.RIGHT) {
                    (leftSwipeableState.offset.value - screenCenterPx).roundToInt()
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
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.5f))
            ) {
                Text(text = "left panel screen")
            }

            // Center Panel ----------------------------------------------------
            val centerScreenZIndex by remember {
                derivedStateOf {
                    if (leftSwipeableState.isAnimationRunning || leftSwipeableState.currentValue == CenterScreenState.CENTER || leftSwipeableState.progress.fraction in 0.05f..0.95f) 1f
                    else 0.5f
                }
            }

            AnimatedVisibility(
                modifier = leftSwipeableModifier
                    .zIndex(centerScreenZIndex)
                    .offset { IntOffset(x = centerScreenOffset, y = 0) },
                visible = isAnyItemSelectedInServers,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(0.85f)
                        .background(Color.Magenta)
                ) {
                    Text(text = "center panel screen")
                }
            }
        }


    }
}