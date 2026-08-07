package com.finalplayer.app.ui.player.controls.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.finalplayer.app.data.preferences.PlayerPreferences
import com.finalplayer.app.player.DoubleTapSeekState
import org.koin.compose.koinInject

@Composable
fun DoubleTapSeekOvals(
    doubleTapState: DoubleTapSeekState?,
    modifier: Modifier = Modifier,
    playerPrefs: PlayerPreferences = koinInject()
) {
    val showOvals by playerPrefs.showDoubleTapOvals.asFlow().collectAsState(initial = true)

    if (!showOvals) return

    AnimatedVisibility(
        visible = doubleTapState != null,
        enter = fadeIn(tween(150)),
        exit = fadeOut(tween(300)),
        modifier = modifier.fillMaxSize()
    ) {
        if (doubleTapState == null) return@AnimatedVisibility

        val isLeft = doubleTapState.isLeft

        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = if (isLeft) Alignment.CenterStart else Alignment.CenterEnd
        ) {
            // Semi-circle background overlay from edge (alpha 20% white)
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(0.35f)
                    .clip(
                        if (isLeft) CircleShape else CircleShape // Oval clip
                    )
                    .background(Color.White.copy(alpha = 0.20f))
                    .testTag(if (isLeft) "double_tap_oval_left" else "double_tap_oval_right"),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // 3 animated flashing arrows
                    FlashingArrowsRow(isLeft = isLeft)

                    Text(
                        text = "${doubleTapState.amountSeconds} ثوانٍ",
                        style = MaterialTheme.typography.titleMedium.copy(
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    )
                }
            }
        }
    }
}

@Composable
private fun FlashingArrowsRow(isLeft: Boolean) {
    val infiniteTransition = rememberInfiniteTransition(label = "ArrowFlashing")
    val alpha1 by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(250),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alpha1"
    )

    val icon = if (isLeft) Icons.AutoMirrored.Filled.ArrowBack else Icons.AutoMirrored.Filled.ArrowForward

    Row(
        horizontalArrangement = Arrangement.spacedBy((-4).dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(3) { index ->
            val alphaMod = if (index == 1) alpha1 else 0.7f
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier
                    .size(24.dp)
                    .alpha(alphaMod)
            )
        }
    }
}
