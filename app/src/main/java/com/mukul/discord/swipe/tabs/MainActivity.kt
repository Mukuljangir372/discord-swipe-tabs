package com.mukul.discord.swipe.tabs

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.mukul.discord.swipe.tabs.ui.theme.DiscordswipetabsTheme

enum class DrawerTypes {
    LEFT, RIGHT
}

enum class CenterScreenState {
    LEFT_ANCHORED, CENTER, RIGHT_ANCHORED
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DiscordswipetabsTheme {
                LeftTwoPanelsScreen()
//                RightTwoPanelsScreen()
//                ThreePanelsScreen()
            }
        }
    }
}
