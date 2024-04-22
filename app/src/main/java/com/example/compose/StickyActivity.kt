package com.example.compose

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

class StickyActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun GroupedList() {
    val sections = listOf("A", "B", "C")

    LazyColumn {
        sections.forEach { section ->
            stickyHeader {
                Column(
                    modifier = Modifier
                        .height(40.dp)
                        .fillMaxWidth()
                        .background(Color.LightGray)
                ) {
                    Text(
                        text = section,
                        modifier = Modifier.fillMaxWidth(),
                        style = MaterialTheme.typography.headlineLarge,
                        textAlign = TextAlign.Center
                    )
                }
            }
            items(100) { item ->
                Text(text = "Some item $item")
            }
        }
    }
}