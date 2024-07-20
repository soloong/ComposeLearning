package com.example.compose

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.compose.ui.theme.DragAndDropTheme

// 官方demo
// https://cs.android.com/androidx/platform/frameworks/support/+/androidx-main:compose/foundation/foundation/integration-tests/foundation-demos/src/main/java/androidx/compose/foundation/demos/LazyGridDragAndDropDemo.kt;bpv=1
class DragAndDropActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            var items by remember { mutableStateOf(createItems(18)) }
            DraggableGrid(items = items, itemKey = { index, item ->
                item.id
            }, onMove = { dragingIndex, targetIndex ->
                val mutableList = items.toMutableList().apply {
                    add(targetIndex, removeAt(dragingIndex))  // 交换位置
                }
                items = mutableList  // 更新状态，触发动画

            }) { item, isDragging ->
                Box(modifier = Modifier
                    .background(item.color)
                    .height(100.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "${item.id}")
                }
            }

        }
    }

    fun createItems(count: Int): List<Item> {
        return (1..count).map {
            Item(it, colors[it % colors.size])
        }
    }

}

data class Item(
    val id: Int,
    val color: Color
)

private val colors = listOf(
    Color(0xFFF44336),
    Color(0xFFE91E63),
    Color(0xFF9C27B0),
    Color(0xFF673AB7),
    Color(0xFF3F51B5),
    Color(0xFF2196F3),
    Color(0xFF03A9F4),
    Color(0xFF00BCD4),
    Color(0xFF009688),
    Color(0xFF4CAF50),
    Color(0xFF8BC34A),
    Color(0xFFCDDC39),
    Color(0xFFFFEB3B),
    Color(0xFFFFC107),
    Color(0xFFFF9800),
    Color(0xFFFF5722)
)

