package com.example.compose

import android.os.Bundle
import android.util.Log
import android.view.MotionEvent.ACTION_DOWN
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.motionEventSpy
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.compose.ui.theme.ComposeTheme

class TextFiledActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            ComposeTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    textFieldCompose()
                }
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
    @Preview()
    @Composable
    fun textFieldCompose() {
        var phone = remember {
            mutableStateOf("")
        }
        var password = remember {
            mutableStateOf("")
        }
        val interactionSource = remember {
            MutableInteractionSource()
        }
        val pressState = interactionSource.collectIsPressedAsState()
        val lableText = if (pressState.value) "手机号" else "手机号码"

        Column(
        ) {
            Text(
                text = "ClickMe",
                modifier = Modifier
                    .padding(horizontal = 30.dp, vertical = 50.dp)
                    .background(Color.Cyan)
                    .drawBehind {
                        Log.i("ClickMe", "size = $size");
                    }
                    .clickable {
                        Log.i("ClickMe", "happen Click");
                    }.motionEventSpy {
                        when(it.actionMasked){
                            ACTION_DOWN -> {
                                Log.d(TAG,"Other Event")
                            }
                            else ->
                                Log.d(TAG,"Other Event")
                        }
                    }
            )
            Text(
                text = "BaseLine",
                modifier = Modifier.background(Color.Yellow)
            )

//            TextField(
//                value = phone.value,
//                onValueChange = {
//                    phone.value = it
//                },
//                label = {
//                    Text(lableText)
//                },
//                placeholder = {
//                    Text("请输入手机号码")
//                },
//                leadingIcon = {
//                    // 左边的图片
//                    Image(
//                        painter = painterResource(id = R.mipmap.icon_png_1),
//                        contentDescription = "输入框前面的图标"
//                    )
//                },
//                trailingIcon = {
//                    Image(
//                        painter = painterResource(id = R.mipmap.icon_cat),
//                        contentDescription = "输入框后面的图标"
//                    )
//                },
//                isError = false,
//                keyboardOptions = KeyboardOptions(
//                    keyboardType = KeyboardType.Text,
//                    imeAction = ImeAction.Next,
//                    autoCorrect = true,
//                    capitalization = KeyboardCapitalization.Sentences
//                ),
//                keyboardActions = KeyboardActions(
//                    onDone = {
//
//                    },
//                    onGo = {
//
//                    },
//                    onNext = {
//
//                    },
//                    onPrevious = {
//
//                    },
//                    onSearch = {
//
//                    },
//                    onSend = {
//
//                    }),
//                interactionSource = interactionSource,
//                // singleLine 设置单行
//                singleLine = true,
//                // maxLines设置最大行数
//                maxLines = 2,
//                // 设置背景的形状。比如圆角，圆形等
//                shape = RoundedCornerShape(4f),
//                // 简单举个focusedIndicatorColor的颜色就好其他一样
//                colors = TextFieldDefaults.textFieldColors(focusedIndicatorColor = Color.Red)
//            )
//            OutlinedTextField(
//                value = password.value,
//                onValueChange = { password.value = it },
//                label = { Text("密码") },
//                // 设置输入的文本样式，比如密码的时候输入变成....
//                visualTransformation = PasswordVisualTransformation('*')
//            )
        }
    }

}