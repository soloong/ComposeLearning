package com.example.compose

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Composer
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.unit.dp
import com.example.compose.ui.theme.ComposeTheme


class CircleMenuActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    //    val menuItems = arrayOf("A", "B", "C", "D", "E", "F","G")
        val menuItems = mapOf<String,Color>(
            "A" to Color.hsl((360 * Math.random()).toFloat(), 0.5F, 0.5F),
            "B" to Color.hsl((360 * Math.random()).toFloat(), 0.5F, 0.5F),
            "C" to Color.hsl((360 * Math.random()).toFloat(), 0.5F, 0.5F),
            "D" to Color.hsl((360 * Math.random()).toFloat(), 0.5F, 0.5F),
            "E" to Color.hsl((360 * Math.random()).toFloat(), 0.5F, 0.5F),
            "F" to Color.hsl((360 * Math.random()).toFloat(), 0.5F, 0.5F),
            "G" to Color.hsl((360 * Math.random()).toFloat(), 0.5F, 0.5F)
        )

        setContent {
            ComposeTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    CircleBox(modifier = Modifier.fillMaxSize()) {
                        menuItems.forEach {
                            MenuBox(it.key, it.value);
                        }
                    }

                }
            }
        }
    }
}

@Composable
fun MenuBox(menu: String, color: Color) {
    Box(
        modifier = Modifier
            .width(50.dp)
            .height(50.dp)
            .drawBehind {
                 drawCircle(color)
            },
        contentAlignment = Alignment.Center
    ) {
        Text(text = menu);
    }
}



@Composable
fun MyBasicColumn(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Layout(
        modifier = modifier,
        content = content
    ) { measurables, constraints ->
        // Don't constrain child views further, measure them with given constraints
        // List of measured children
        val placeables = measurables.map { measurable ->
            // Measure each children
            measurable.measure(constraints)
        }

        // Set the size of the layout as big as it can
        layout(constraints.maxWidth, constraints.maxHeight) {
            // Track the y co-ord we have placed children up to
            var yPosition = 0

            // Place children in the parent layout
            placeables.forEach { placeable ->
                // Position item on the screen
                placeable.placeRelative(x = 0, y = yPosition)

                // Record the y co-ord placed up to
                yPosition += placeable.height
            }
        }
    }
}






