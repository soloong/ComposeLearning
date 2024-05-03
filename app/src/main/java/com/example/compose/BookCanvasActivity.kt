package com.example.compose

import android.os.Bundle
import android.text.TextPaint
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.ImageBitmapConfig
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.PaintingStyle
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathOperation
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.example.compose.ui.theme.ComposeTheme
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.hypot
import kotlin.math.min
import kotlin.math.tan


class BookCanvasActivity() : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            ComposeTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    BookCanvas()
                }
            }
        }
    }


    @Composable
    fun BookCanvas(bookPageNode: BookPageElement = BookPageElement()) {

        var pointerOffset by remember {
            mutableStateOf(Offset(0f, 0f))
        }
        var dragState by remember {
            mutableIntStateOf(BookPageElement.STATE_IDLE)
        }

        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .onSizeChanged {
                    pointerOffset = Offset(it.width.toFloat(), it.height.toFloat())
                }
                .pointerInput("DraggerInput") {
                    detectDragGestures(
                        onDragStart = { it ->

                            val offsetLeft = 150.dp.toPx();
                            val offsetTop = 150.dp.toPx();

                            dragState = if (Rect(
                                    size.width - offsetLeft,
                                    size.height - 100.dp.toPx(),
                                    size.width.toFloat(),
                                    size.height.toFloat()
                                ).contains(it)
                            ) {
                                BookPageElement.STATE_DRAGING_BOTTOM
                            } else if (Rect(
                                    size.width - offsetLeft,
                                    0F,
                                    size.width.toFloat(),
                                    100.dp.toPx()
                                ).contains(it)
                            ) {
                                BookPageElement.STATE_DRAGING_TOP
                            } else if (Rect(
                                    size.width - offsetLeft - 20.dp.toPx(),
                                    offsetTop,
                                    size.width - 20.dp.toPx(),
                                    size.height - offsetTop
                                ).contains(it)
                            ) {
                                BookPageElement.STATE_DRAGING_MIDDLE
                            } else {
                                BookPageElement.STATE_DRAGING_EXCEEDE
                            }

                        },
                        onDragEnd = {
                            dragState = BookPageElement.STATE_IDLE
                            pointerOffset = Offset(size.width.toFloat(), size.height.toFloat())
                        }
                    ) { change, dragAmount ->
                        if (dragState == BookPageElement.STATE_DRAGING_BOTTOM || dragState == BookPageElement.STATE_DRAGING_MIDDLE || dragState == BookPageElement.STATE_DRAGING_TOP) {
                            pointerOffset = change.position
                        }
                    }
                },
        ) {


            drawIntoCanvas { canvas ->

                val bitmap = ImageBitmap(
                    size.width.toInt(),
                    size.height.toInt(),
                    config = ImageBitmapConfig.Argb8888,
                    true
                )
                val bitmapCanvas = Canvas(bitmap)
                val imageBitmap = ImageBitmap.imageResource(resources, R.mipmap.img_pic)
                bitmapCanvas.drawImageRect(
                    image = imageBitmap,
                    dstSize = IntSize(size.width.toInt(), 300.dp.toPx().toInt()),
                    paint = bookPageNode.paint
                )

                if (dragState == BookPageElement.STATE_DRAGING_TOP) {
                    drawTopRightFoldBook(canvas, pointerOffset, bookPageNode, imageBitmap)
                } else if (dragState == BookPageElement.STATE_DRAGING_BOTTOM) {
                    drawBottomFoldBook(canvas, pointerOffset, bookPageNode, imageBitmap)
                } else if (dragState == BookPageElement.STATE_DRAGING_MIDDLE) {
                    drawMiddleFoldPage(canvas, pointerOffset, bookPageNode, imageBitmap)
                } else {
                    drawIdleBook(canvas, pointerOffset, bookPageNode, imageBitmap)
                }

            }

        }

    }

    private fun DrawScope.drawMiddleFoldPage(
        canvas: Canvas,
        pointerOffset: Offset,
        bookPageNode: BookPageElement,
        imageBitmap: ImageBitmap
    ) {

        val paint = bookPageNode.paint
        val foldPath = bookPageNode.foldPath
        val pageOutline = bookPageNode.pageOutline
        val blankOutline = bookPageNode.blankOutline
        val clipPath = bookPageNode.clipPath

        canvas.save()

        canvas.translate(size.width, 0F)

        val pointerPoint = Offset(
            pointerOffset.x - size.width,
            pointerOffset.y - 0
        )

        val verticalPoint = Offset(pointerPoint.x, 0F)
        val halfVerticalPoint = Offset(pointerPoint.x / 2F, 0F)

        foldPath.reset()
        foldPath.moveTo(verticalPoint.x, verticalPoint.y)
        foldPath.lineTo(halfVerticalPoint.x, halfVerticalPoint.y)
        foldPath.lineTo(halfVerticalPoint.x, size.height)
        foldPath.lineTo(verticalPoint.x, size.height)
        foldPath.close()


        pageOutline.reset()
        pageOutline.moveTo(-size.width, 0F)
        pageOutline.lineTo(-size.width, size.height)
        pageOutline.lineTo(0F, size.height)
        pageOutline.lineTo(0F, 0F)
        pageOutline.close()

        blankOutline.reset()
        blankOutline.moveTo(0F, 0F)
        blankOutline.lineTo(halfVerticalPoint.x, halfVerticalPoint.y)
        blankOutline.lineTo(halfVerticalPoint.x, size.height)
        blankOutline.lineTo(0F, size.height)
        blankOutline.close()

        clipPath.reset()
        clipPath.op(pageOutline, blankOutline, PathOperation.Difference)
        canvas.clipPath(clipPath)

        val currentColor = paint.color
        paint.color = Color.Cyan
        canvas.drawRect(Rect(0F, 0F, -size.width, size.height), paint)
        paint.color = currentColor

        canvas.save()
        canvas.translate(-size.width, 0F)
        canvas.drawImageRect(imageBitmap, paint = paint)
        canvas.restore()

        canvas.drawPath(foldPath, paint)

        canvas.drawLine(Offset(size.width, pointerPoint.y), pointerPoint, paint)
        canvas.drawLine(halfVerticalPoint, Offset(halfVerticalPoint.x, size.height), paint)
        canvas.drawLine(verticalPoint, Offset(verticalPoint.x, size.height), paint)

        canvas.restore()

    }


    private fun DrawScope.drawTopRightFoldBook(
        canvas: Canvas,
        pointerOffset: Offset,
        bookPageNode: BookPageElement,
        imageBitmap: ImageBitmap
    ) {
        val paint = bookPageNode.paint
        val blankOutline = bookPageNode.blankOutline
        val foldPath = bookPageNode.foldPath
        val clipPath = bookPageNode.clipPath
        val pageOutline = bookPageNode.pageOutline

        canvas.save()
        canvas.translate(size.width, 0F)

        var pointerPoint = Offset(
            pointerOffset.x - size.width,
            pointerOffset.y - 0
        )
        // atan2斜率范围在 -PI到PI之间，因此第三象限为atan2 =  atan - PI, 那么atan = PI  + atan2

        val startPoint = Offset(0F, 0F);

        val pointerRotate = atan2(pointerPoint.y - startPoint.y, pointerPoint.x - startPoint.x) + PI

        val xLength = min(
            hypot(
                pointerPoint.x - startPoint.x,
                pointerPoint.y - startPoint.y
            ) / cos(pointerRotate),
            size.width * 2.0
        ).toFloat()

        //由于计算出来的Y按0，0点计算的，因此需要转换为
        val yLength = xLength / tan(pointerRotate).toFloat()

        //   xLength / YLength = (xLength   - abs(pointerPoint.x)) / maxY

        val minY = -yLength * (xLength - abs(pointerPoint.x)) / xLength;

        if (minY < pointerPoint.y) {
            pointerPoint = Offset(
                pointerPoint.x,
                minY
            )
        }

        val XHalfAxisPoint = Offset(-xLength / 2F, 0F)
        val YHalfAxisPoint = Offset(0F, -yLength / 2F)

        val controlOffset = abs(50 * (2 * pointerPoint.x / size.width))

        val ld = Offset(
            (pointerPoint.x + XHalfAxisPoint.x) / 2F + controlOffset,
            (pointerPoint.y + XHalfAxisPoint.y) / 2F
        )
        val rt = Offset(
            (pointerPoint.x + YHalfAxisPoint.x) / 2F,
            (pointerPoint.y + YHalfAxisPoint.y) / 2F - controlOffset
        )

        val XControlAxisPoint = Offset(-xLength * 3 / 4F, 0F)
        val YControlfAxisPoint = Offset(0F, -yLength * 3 / 4F)


        foldPath.reset()


        foldPath.moveTo(XHalfAxisPoint.x, XHalfAxisPoint.y)
        foldPath.quadraticBezierTo(ld.x, ld.y, pointerPoint.x, pointerPoint.y)
        foldPath.quadraticBezierTo(rt.x, rt.y, YHalfAxisPoint.x, YHalfAxisPoint.y)
        foldPath.close()


        foldPath.moveTo(XHalfAxisPoint.x, XHalfAxisPoint.y)
        foldPath.quadraticBezierTo(ld.x, ld.y, pointerPoint.x, pointerPoint.y)
        foldPath.quadraticBezierTo(rt.x, rt.y, YHalfAxisPoint.x, YHalfAxisPoint.y)
        foldPath.close()


        pageOutline.reset()
        pageOutline.moveTo(-size.width, 0F)
        pageOutline.lineTo(-size.width, size.height)
        pageOutline.lineTo(0F, size.height)
        pageOutline.lineTo(0F, 0F)
        pageOutline.close()

        blankOutline.reset()
        blankOutline.moveTo(0F, 0F)
        blankOutline.lineTo(YHalfAxisPoint.x, YHalfAxisPoint.y)
        blankOutline.lineTo(XHalfAxisPoint.x, XHalfAxisPoint.y)
        blankOutline.close()

        clipPath.reset()
        clipPath.op(pageOutline, blankOutline, PathOperation.Difference)
        canvas.clipPath(clipPath)

        val currentColor = paint.color
        paint.color = Color.Cyan
        canvas.drawRect(Rect(0F, 0F, -size.width, size.height), paint)
        paint.color = currentColor

        canvas.save()
        canvas.translate(-size.width, 0F)
        canvas.drawImageRect(imageBitmap, paint = paint)
        canvas.restore()

        canvas.drawPath(foldPath, paint)

        canvas.drawLine(Offset(-xLength, 0F), Offset(0F, -yLength), paint)
        canvas.drawLine(XHalfAxisPoint, YHalfAxisPoint, paint)
        canvas.drawLine(XControlAxisPoint, YControlfAxisPoint, paint)
        canvas.drawLine(Offset(0F, 0F), pointerPoint, paint)

        canvas.restore()
    }

    private fun DrawScope.drawBottomFoldBook(
        canvas: Canvas,
        pointerOffset: Offset,
        bookPageNode: BookPageElement,
        imageBitmap: ImageBitmap
    ) {

        val paint = bookPageNode.paint
        val blankOutline = bookPageNode.blankOutline
        val foldPath = bookPageNode.foldPath
        val clipPath = bookPageNode.clipPath
        val pageOutline = bookPageNode.pageOutline

        canvas.save()
        canvas.translate(size.width, size.height)

        var startPoint = Offset(0F, 0F)

        var pointerPoint = Offset(
            pointerOffset.x - size.width,
            pointerOffset.y - size.height
        )


        // atan2斜率范围在 -PI到PI之间，因此第三象限为atan2 =  atan - PI, 那么atan = PI  + atan2

        val pointerRotate = atan2(pointerPoint.y - startPoint.y, pointerPoint.x - startPoint.x) + PI

        val xLength = min(
            hypot(
                pointerPoint.x - startPoint.x,
                pointerPoint.y - startPoint.y
            ) / cos(pointerRotate),
            size.width * 2.0
        ).toFloat()
        val yLength = (xLength / tan(pointerRotate)).toFloat()

        //   xLength / YLength = (xLength   - abs(pointerPoint.x)) / maxY

        val minY = -yLength * (xLength - abs(pointerPoint.x)) / xLength;

        if (minY > pointerPoint.y) {
            pointerPoint = Offset(
                pointerPoint.x,
                minY
            )
        }

        val XHalfAxisPoint = Offset(-xLength / 2F, 0F)
        val YHalfAxisPoint = Offset(0F, -yLength / 2F)


        val controlOffset = abs(55 * (2 * pointerPoint.x / size.width))

        val ld = Offset(
            (pointerPoint.x + XHalfAxisPoint.x) / 2F + controlOffset,
            (pointerPoint.y + XHalfAxisPoint.y) / 2F
        )
        val rt = Offset(
            (pointerPoint.x + YHalfAxisPoint.x) / 2F,
            (pointerPoint.y + YHalfAxisPoint.y) / 2F + controlOffset
        )


        val XControlAxisPoint = Offset(-xLength * 3 / 4F, 0F)
        val YControlfAxisPoint = Offset(0F, -yLength * 3 / 4F)


        foldPath.reset()
        foldPath.moveTo(XHalfAxisPoint.x, XHalfAxisPoint.y)
        foldPath.quadraticBezierTo(ld.x, ld.y, pointerPoint.x, pointerPoint.y)
        foldPath.quadraticBezierTo(rt.x, rt.y, YHalfAxisPoint.x, YHalfAxisPoint.y)
        foldPath.close()


        pageOutline.reset()
        pageOutline.moveTo(-size.width, -size.height)
        pageOutline.lineTo(-size.width, 0F)
        pageOutline.lineTo(0F, 0F)
        pageOutline.lineTo(0F, -size.height)
        pageOutline.close()

        blankOutline.reset()
        blankOutline.moveTo(0F, 0F)
        blankOutline.lineTo(YHalfAxisPoint.x, YHalfAxisPoint.y)
        blankOutline.lineTo(XHalfAxisPoint.x, XHalfAxisPoint.y)
        blankOutline.close()

        clipPath.reset()
        //剔除被裁剪的部分blankOutline
        clipPath.op(pageOutline, blankOutline, PathOperation.Difference)
        canvas.clipPath(clipPath)

        val currentColor = paint.color
        paint.color = Color.Cyan
        canvas.drawRect(Rect(0F, 0F, -size.width, -size.height), paint)
        paint.color = currentColor

        canvas.save()
        canvas.translate(-size.width, -size.height)
        canvas.drawImageRect(imageBitmap, paint = paint)
        canvas.restore()

        //绘制折角
        canvas.drawPath(foldPath, paint)

        //绘制原点与触点的连线
        canvas.drawLine(Offset(0F, 0F), pointerPoint, paint)
        //绘制切线
        canvas.drawLine(XHalfAxisPoint, YHalfAxisPoint, paint)
        //绘制1/2等距离切线
        canvas.drawLine(Offset(-xLength, 0F), Offset(0F, -yLength), paint)
        //绘制3/4等距离切线
        canvas.drawLine(XControlAxisPoint, YControlfAxisPoint, paint)
        canvas.restore()
    }

    private fun DrawScope.drawIdleBook(
        canvas: Canvas,
        pointerOffset: Offset,
        bookPageNode: BookPageElement,
        imageBitmap: ImageBitmap
    ) {
        val paint = bookPageNode.paint
        canvas.save()
        canvas.translate(size.width, size.height)
        val currentColor = paint.color
        paint.color = Color.Cyan
        canvas.drawRect(Rect(0F, 0F, -size.width, -size.height), paint)
        paint.color = currentColor
        canvas.restore()

        canvas.drawImageRect(imageBitmap, paint = paint)

    }


}

@Stable
class BookPageElement {

    val textPaint: android.graphics.Paint = TextPaint();

    var paint: Paint = Paint();

    var foldPath: Path = Path()

    var blankOutline = Path()

    var pageOutline = Path()

    var clipPath = Path()



    init {
        paint.style = PaintingStyle.Fill
        paint.color = Color.Red
        paint.isAntiAlias = true

        textPaint.textSize = 36F;
        textPaint.color = 0xFF000000.toInt();
    }


    companion object {
        const val STATE_IDLE = 0
        const val STATE_DRAGING_EXCEEDE = 1
        const val STATE_DRAGING_TOP = 2
        const val STATE_DRAGING_MIDDLE = 3
        const val STATE_DRAGING_BOTTOM = 4
    }
}