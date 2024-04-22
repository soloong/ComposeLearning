package com.example.compose

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.animateIntOffsetAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.layout
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.example.compose.ui.theme.ComposeTheme


class AnimationActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AnimationComposeTheme()
        }
    }
}


@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun AnimationComposeTheme() {
    ComposeTheme {
        // A surface container using the 'background' color from the theme
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background,
        ) {

            var toggled by remember {
                mutableStateOf(false)
            }
            val interactionSource = remember {
                MutableInteractionSource()
            }
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxSize()
                    .clickable(indication = null, interactionSource = interactionSource) {
                        toggled = !toggled
                    }
            ) {
                val offsetTarget = if (toggled) {
                    IntOffset(150, 150)
                } else {
                    IntOffset.Zero
                }
                val offset = animateIntOffsetAsState(
                    targetValue = offsetTarget,
                    label = "offset"
                )
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .background(Color.Yellow)
                )
                Box(
                    modifier = Modifier
                        .layout { measurable, constraints ->
                            val offsetValue = if (isLookingAhead) offsetTarget else offset.value
                            val placeable = measurable.measure(constraints)
                            Log.i(TAG,"A");
                            layout(
                                placeable.width + offsetValue.x,
                                placeable.height + offsetValue.y
                            ) {
                                placeable.placeRelative(offsetValue)
                                Log.i(TAG,"b");
                            }
                        }
                        .size(100.dp)
                        .background(Color.Red)
                )
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .background(Color.Cyan)
                )
            }
        }
    }
}


