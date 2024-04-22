package com.example.compose

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.DrawModifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.PaintingStyle
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.ContentDrawScope
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.input.pointer.PointerEventType.Companion.Move
import androidx.compose.ui.input.pointer.PointerEventType.Companion.Press
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import com.example.compose.ui.theme.ComposeTheme


class GuaguaCardActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            ComposeTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    ScrapeLayerPage()
                }
            }
        }
    }
}

@Composable
fun ScrapeLayerPage(){
    var linePath by remember {
        mutableStateOf(Offset.Zero)
    }
    val path by remember {
        mutableStateOf(Path())
    }
    Column(modifier = Modifier
        .fillMaxWidth()
        .pointerInput("dragging") {
            awaitPointerEventScope {
                while (true) {
                    val event = awaitPointerEvent()
                    when (event.type) {
                        //按住时，更新起始点
                        Press -> {
                            path.moveTo(
                                event.changes.first().position.x,
                                event.changes.first().position.y
                            )
                        }
                        //移动时，更新起始点 移动时，记录路径path
                        Move -> {
                            linePath = event.changes.first().position
                        }
                    }
                }
            }
        }
        .scrapeLayer(path, linePath)
    ) {
        Image(
            modifier = Modifier.fillMaxSize(),
            painter = painterResource(id = R.mipmap.img_pic),
            contentDescription = ""
        )
    }
}

fun Modifier.scrapeLayer(startPath: Path, moveOffset: Offset) =
    this.then(ScrapeLayer(startPath, moveOffset))

class ScrapeLayer(private val strokePath: Path, private val moveOffset: Offset) : DrawModifier {

    private val pathPaint = Paint().apply {
        alpha = 0f
        style = PaintingStyle.Stroke
        strokeWidth = 70f
        blendMode = BlendMode.Clear
        strokeJoin = StrokeJoin.Round
        strokeCap = StrokeCap.Round
    }

    private val layerPaint = Paint().apply {
        color = Color.Gray
    }

    override fun ContentDrawScope.draw() {
        drawContent()
        drawIntoCanvas {
            val rect = Rect(0f, 0f, size.width, size.height)
            it.saveLayer(rect, layerPaint)
            //从当前画布，裁切一个新的图层
            it.drawRect(rect, layerPaint)
            strokePath.lineTo(moveOffset.x, moveOffset.y)
            it.drawPath(strokePath, pathPaint)
            it.restore()
        }
    }
}

