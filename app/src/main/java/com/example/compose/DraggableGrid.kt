package com.example.compose

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridItemScope
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun <T : Any> DraggableGrid(
    items: List<T>,
    itemKey:(Int,T) -> Any,
    onMove: (Int, Int) -> Unit,
    content: @Composable (T, Boolean) -> Unit,
) {

    val gridState = rememberLazyGridState()
    val dragDropState = rememberGridDragDropState(gridState, onMove)
    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        modifier = Modifier.dragContainer(dragDropState),
        state = gridState,
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(5.dp),
        horizontalArrangement = Arrangement.spacedBy(5.dp),
    ) {
        itemsIndexed(items, key = { index, item ->
            itemKey(index,item)
        }) { index, item ->
            DraggableItem(dragDropState, index) { isDragging ->
                content(item, isDragging)
            }
        }
    }
}

fun Modifier.dragContainer(dragDropState: GridDragDropState): Modifier {
    return pointerInput(key1 = dragDropState) {
        detectDragGesturesAfterLongPress(
            onDrag = { change, offset ->
                change.consume()
                dragDropState.onDrag(offset = offset)
            },
            onDragStart = { offset ->
                dragDropState.onDragStart(offset)
            },
            onDragEnd = { dragDropState.onDragInterrupted() },
            onDragCancel = { dragDropState.onDragInterrupted() }
        )
    }
}

@ExperimentalFoundationApi
@Composable
fun LazyGridItemScope.DraggableItem(
    dragDropState: GridDragDropState,
    index: Int,
    content: @Composable (isDragging: Boolean) -> Unit,
) {
    val dragging = index == dragDropState.draggingItemIndex
    val draggingModifier = if (dragging) {
        //被拖拽时
        Modifier
            .zIndex(1f) //防止被遮挡
            .graphicsLayer {
                translationX = dragDropState.draggingItemOffset.x
                translationY = dragDropState.draggingItemOffset.y
            }
    } else if (index == dragDropState.previousIndexOfDraggedItem) {
        //松手后的"回归"动画
        Modifier
            .zIndex(1f)  //防止被遮挡
            .graphicsLayer {
                translationX = dragDropState.previousItemOffset.value.x
                translationY = dragDropState.previousItemOffset.value.y
            }
    } else {
        //idle状态
        Modifier.animateItemPlacement()
    }
    Box(modifier = Modifier.then(draggingModifier) , propagateMinConstraints = true) {
        content(dragging)
    }
}
