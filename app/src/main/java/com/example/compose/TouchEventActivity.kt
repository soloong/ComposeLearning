package com.example.compose

import android.content.Context
import android.os.Bundle
import android.text.SpannableString
import android.util.AttributeSet
import android.util.Log
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams
import android.widget.FrameLayout
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.ParagraphStyle
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.example.compose.ui.theme.ComposeTheme


const val TAG = "TouchEventActivity"

class TouchEventActivity : ComponentActivity() {

    private var rootFrameLayout: FrameLayout? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)



        setContent {
            ComposeTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    TouchScreen()
                }
            }
        }
    }

    override fun setContentView(view: View?, params: ViewGroup.LayoutParams?) {
        if(rootFrameLayout == null) {
            rootFrameLayout = TouchFrameLayout(this);
            super.setContentView(rootFrameLayout, ViewGroup.LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT))
        }
        rootFrameLayout?.let {
            it.removeAllViews()
            it.addView(view,params)
        }
    }


    @Composable
    fun TouchScreen() {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput("Box#1") {
                    detectDragGestures(onDragStart = {
                        Log.d(TAG, "Box#1 onDragStart")

                    }) { change, dragAmount ->
                        Log.d(TAG, "Box#1 dragging")
                    }

                }

        ) {
            Column(
                modifier = Modifier
                    .align(Alignment.Center)
                    .pointerInput("Column#1") {
                        detectDragGestures(onDragStart = {
                            Log.d(TAG, "Column#1 onDragStart")

                        }) { change, dragAmount ->
                            Log.d(TAG, "Column#1 dragging")
                        }

                    }
            ) {
                Box(
                    modifier = Modifier
                        .width(100.dp)
                        .height(100.dp)
                        .align(Alignment.CenterHorizontally)
                        .background(Color.Cyan)
                ) {
                    Text(text = "A", modifier = Modifier
                        .align(Alignment.Center)
                        .padding(horizontal = 20.dp, vertical = 20.dp)
                        .background(Color.Red)
                        .clickable {
                         Log.d(TAG,"A Click")
                    })
                }
                Box(
                    modifier = Modifier
                        .width(100.dp)
                        .height(100.dp)
                        .align(Alignment.CenterHorizontally)
                        .background(Color(0xFFFF6666))
                ) {
                    Text(text = "B", modifier = Modifier.align(Alignment.Center))
                }
                Box(
                    modifier = Modifier
                        .width(100.dp)
                        .height(100.dp)
                        .align(Alignment.CenterHorizontally)
                        .background(Color(0xFFff9922))
                ) {
                    Text(text =  buildAnnotatedString {
                        withStyle(style = SpanStyle(color = Color.Red)) {
                            append("Vibrant Text")
                        }
                        append("\n\n")
                        append(SpannableString("Regular Text"))

                        withStyle(style = ParagraphStyle()){

                        }

                        withStyle(style = SpanStyle()){

                        }
                    }, modifier = Modifier
                        .align(Alignment.Center)
                        .drawWithContent {
                        })
                }
            }
        }
    }


}

class TouchFrameLayout : FrameLayout{
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    init{
        viewTreeObserver.addOnGlobalFocusChangeListener { oldFocus, newFocus ->
            Log.d(TAG,"oldFocus : $oldFocus , newFocus : $newFocus")
        }
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        val dispatchTouchEvent = super.dispatchTouchEvent(ev)
        if(dispatchTouchEvent){
            findTouchTarget(this);
        }
        return dispatchTouchEvent;
    }

    private fun findTouchTarget(touchFrameLayout: TouchFrameLayout): Boolean {

        return false;
    }

    override fun dispatchKeyEvent(event: KeyEvent?): Boolean {
        return super.dispatchKeyEvent(event)
    }
}

