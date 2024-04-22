package com.example.compose

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.MotionEvent
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.input.pointer.motionEventSpy
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.example.compose.ui.theme.ComposeTheme


class MainActivity : ComponentActivity() {

    var runTask: Runnable? = null

    val handler = Handler(Looper.getMainLooper())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val imageResource = ImageBitmap.imageResource(resources, R.mipmap.img_pic)
        setContent {
            MainComposeTheme(imageResource)
        }

    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG,"onPause")
    }
}
@Composable
fun MainComposeTheme(imageResource: ImageBitmap) {
    ComposeTheme {
        // A surface container using the 'background' color from the theme
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background,
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .drawBehind {
                        drawImage(
                            image = imageResource,
                            dstSize = IntSize(size.width.toInt(), size.height.toInt())
                        )
                    }
            ) {
                val greetingState = Greeting("Android")
                Log.d("MainComposeTheme","greetingState $greetingState")
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier):Any {

    var pointerOffset by remember {
        mutableStateOf(Offset(0f, 0f))
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput("dragging") {
                detectDragGestures(onDragStart = {
                    //pointerOffset和it类型不同，这里会隐式转换，实现拖转开始点赋值给pointerOffset
                    pointerOffset = it //拖转一定距离后才会触发此处的调用
                }) { change, dragAmount ->
                    pointerOffset += dragAmount
                }

            }
            .motionEventSpy {
                if (it.actionMasked == MotionEvent.ACTION_DOWN) {
                    pointerOffset = Offset(it.x, it.y)   //获取按下的位置
                }

            }
            .onSizeChanged {
                pointerOffset = Offset(it.width / 2f, it.height / 2f)
            }
            .drawWithContent {
                // draws a fully black area with a small keyhole at pointerOffset that’ll show part of the UI.
                val shader = Brush.radialGradient(
                    listOf(Color.Transparent, Color.Black),
                    center = pointerOffset,
                    radius = 120.dp.toPx(),
                )

                drawRect(
                    shader
                )
            }
    ) {
        Text(
            text = "Hello $name!,Welcome to use compose",
            modifier = modifier
                .fillMaxWidth()
                .wrapContentHeight(Alignment.CenterVertically)
                .drawWithContent {
                },
            textAlign = TextAlign.Center,
            onTextLayout = {
                Log.d("A", "onTextLayout")
            }
        )

    }
    return pointerOffset
}

