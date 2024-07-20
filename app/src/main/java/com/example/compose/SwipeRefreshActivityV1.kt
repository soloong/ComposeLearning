//package com.example.compose
//
//import android.os.Bundle
//import android.util.Log
//import androidx.activity.ComponentActivity
//import androidx.activity.compose.setContent
//import androidx.compose.foundation.ExperimentalFoundationApi
//import androidx.compose.foundation.LocalOverscrollConfiguration
//import androidx.compose.foundation.background
//import androidx.compose.foundation.gestures.detectDragGestures
//import androidx.compose.foundation.layout.Arrangement
//import androidx.compose.foundation.layout.Box
//import androidx.compose.foundation.layout.fillMaxWidth
//import androidx.compose.foundation.layout.height
//import androidx.compose.foundation.layout.padding
//import androidx.compose.foundation.lazy.LazyColumn
//import androidx.compose.foundation.lazy.LazyListState
//import androidx.compose.foundation.lazy.rememberLazyListState
//import androidx.compose.material3.MaterialTheme
//import androidx.compose.material3.Text
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.CompositionLocalProvider
//import androidx.compose.runtime.mutableFloatStateOf
//import androidx.compose.runtime.remember
//import androidx.compose.runtime.rememberCoroutineScope
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.geometry.Offset
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.graphics.graphicsLayer
//import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
//import androidx.compose.ui.input.nestedscroll.NestedScrollSource
//import androidx.compose.ui.input.nestedscroll.nestedScroll
//import androidx.compose.ui.input.pointer.pointerInput
//import androidx.compose.ui.layout.Layout
//import androidx.compose.ui.unit.Constraints
//import androidx.compose.ui.unit.Velocity
//import androidx.compose.ui.unit.dp
//import kotlinx.coroutines.CoroutineScope
//import kotlinx.coroutines.launch
//import kotlin.math.abs
//import kotlin.math.absoluteValue
//
//class SwipeRefreshActivity : ComponentActivity() {
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContent {
//            SwipeRefreshColumn(headerIndicator = {
//                Box (modifier = Modifier
//                    .fillMaxWidth()
//                    .height(100.dp)
//                    .background(Color.White),
//                    contentAlignment = Alignment.Center
//                ){
//                    Text(text = "Hi, I am header")
//                }
//            }, footerIndicator = {
//                Box (modifier = Modifier
//                    .fillMaxWidth()
//                    .height(100.dp)
//                    .background(Color.White),
//                    contentAlignment = Alignment.Center){
//                    Text(text = "ooh，long time no see")
//                }
//            }) { nestedScrollModifierNode ->
//                val state: LazyListState = rememberLazyListState()
//                nestedScrollModifierNode.initLazyState(state)
//                LazyColumn (
//                    state = state,
//                    verticalArrangement = Arrangement.spacedBy(1.dp)
//                ){
//                    val list = (0..5).map { it.toString() }
//                    items(count = list.size) {
//                        Box (modifier = Modifier
//                            .fillMaxWidth()
//                            .height(80.dp)
//                            .background(Color.LightGray),
//                            contentAlignment = Alignment.CenterStart){
//                            Text(
//                                text = list[it],
//                                style = MaterialTheme.typography.bodyLarge,
//                                modifier = Modifier
//                                    .fillMaxWidth()
//                                    .padding(horizontal = 16.dp)
//                            )
//                        }
//                    }
//                }
//            }
//        }
//
//    }
//
//}
//
//
//@OptIn(ExperimentalFoundationApi::class)
//@Composable
//fun SwipeRefreshColumn(
//    modifier: Modifier = Modifier,
//    headerIndicator: (@Composable () -> Unit)?,
//    footerIndicator: (@Composable () -> Unit)?,
//    content: (@Composable (SimpleNestedScrollConnection) -> Unit)
//) {
//    var contentIndex = 0
//    val TAG = "SwipeRefreshList"
//    val coroutineScope = rememberCoroutineScope()
//
//    val connection = remember {
//        SimpleNestedScrollConnection(coroutineScope)
//    }
//
//    Layout(
//        modifier = modifier
//            .nestedScroll(connection)
//            .pointerInput("header-footer-capture"){
//                //由于事件存在优先级，lazyList的优先级更高，我们只需要处理header和footer即可
//                detectDragGestures(onDragStart = {
//                    if(findDragTarget(connection,it) == connection.headerOffset){
//                        Log.d(TAG,"onDragStart Header $it")
//                        connection.dispatchUserDragger(connection.headerOffset)
//                    }else if(findDragTarget(connection,it) == connection.footerOffset){
//                        Log.d(TAG,"onDragStart Footer $it")
//                        connection.dispatchUserDragger(connection.footerOffset)
//                    }
//                }, onDragEnd = {
//                    connection.dispatchUserDragger(null)
//                }){change, dragAmount ->
//                    Log.d(TAG,"onDrag $dragAmount")
//                    connection.dispatchUserScroll(dragAmount);
//                }
//            }
//            .graphicsLayer {
//                translationY = connection.graphicYOffset.value
//                Log.d(TAG, "translationY = ${connection.graphicYOffset}")
//            },
//        content = {
//            headerIndicator?.let {
//                contentIndex++;
//                headerIndicator()
//            }
//            CompositionLocalProvider(LocalOverscrollConfiguration.provides(null)) {
//                content(connection)
//            }
//            footerIndicator?.let {
//                footerIndicator()
//            }
//        }
//    ) { measurables, constraints ->
//        // Don't constrain child views further, measure them with given constraints
//        // List of measured children
//
//        val placeables = measurables.mapIndexed { index, measurable ->
//
//            if (contentIndex == index) {
//                val boxWidth = constraints.maxWidth
//                val boxHeight = constraints.maxHeight
//                val matchParentSizeConstraints = Constraints(
//                    minWidth = if (boxWidth != Constraints.Infinity) boxWidth else 0,
//                    minHeight = if (boxHeight != Constraints.Infinity) boxHeight else 0,
//                    maxWidth = boxWidth,
//                    maxHeight = boxHeight
//                )
//                connection.contentOffset.max = boxHeight.toFloat()
//                measurable.measure(matchParentSizeConstraints)
//            } else {
//                val measure = measurable.measure(constraints)
//                if(index < contentIndex){
//                    connection.headerOffset.max = measure.height.toFloat()
//                }else if(index > contentIndex){
//                    connection.footerOffset.max =  measure.height.toFloat()
//                }
//                measure
//            }
//        }
//
//        // Set the size of the layout as big as it can
//        layout(constraints.maxWidth, constraints.maxHeight) {
//            var yPosition = 0
//            placeables.forEach() {  placeable ->
//                placeable.placeRelative(x = 0, y = yPosition)
//                yPosition += placeable.height
//            }
//
//        }
//    }
//}
//
//fun findDragTarget(connection: SimpleNestedScrollConnection,dragStart: Offset): NestedOffset? {
//    connection?.apply {
//        headerOffset.let {
//            if(it.value + it.max > dragStart.y){
//                return headerOffset;
//            }
//        }
//        val offset =  contentOffset?.max ?: 0f
//        footerOffset.let {
//            if(it.value + it.max + offset > dragStart.y){
//                return footerOffset;
//            }
//        }
//    }
//    return null;
//}
//
//
//data class NestedOffset(var key:String,var max: Float, var value: Float)
//
//class SimpleNestedScrollConnection(
//    var coroutineScope: CoroutineScope
//) : NestedScrollConnection{
//
//    private var dragger: NestedOffset? = null
//    private var lazyListState: LazyListState? = null
//    val headerOffset = NestedOffset("header",0F, 0F)
//    val footerOffset = NestedOffset("footer",0F, 0F)
//    var contentOffset  = NestedOffset("content",0F, 0F)
//    var graphicYOffset = mutableFloatStateOf(0F)
//
//
//    override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
//        Log.d(TAG,"$available")
//        return when {
//            available.y < 0  && headerOffset.max != 0f  -> {
//                if(headerOffset.value > -headerOffset.max) {
//                    //拦截向上滑动，不能超过最大范围，这里只处理header ，因为header优先级较高于footer，同时防止被lazylist消费
//                    val offset =  if(available.y + headerOffset.value < -headerOffset.max){
//                        -headerOffset.max - headerOffset.value
//                    }else{
//                        available.y
//                    }
//                    scroll(headerOffset, NestedOffset("",0F, 0F), offset)
//                }else{
//                    Offset.Zero
//                }
//            }
//
//            available.y > 0  -> {
//                if(lazyListState?.canScrollForward == false && lazyListState?.canScrollBackward == true && footerOffset.max != 0f){
//                    //footer向下滚动需要提前拦截，否则可能导致被LazyList消费，这时footer比header优先级高
//                    val offset = if(available.y + footerOffset.value > 0){
//                        abs(footerOffset.value)
//                    }else{
//                        available.y
//                    }
//                    scroll(footerOffset, headerOffset, offset)
//                }else{
//                    Offset.Zero
//                }
//            }
//
//            else ->{
//                Offset.Zero
//            }
//        }
//    }
//
//    //下面是处理没有被lazylist 消费的事件
//    override fun onPostScroll(
//        consumed: Offset,
//        available: Offset,
//        source: NestedScrollSource
//    ): Offset {
//        return when {
//            available.y < 0  -> {
//                //拦截向上滑动，不能超过最大范围，这里只处理footer ，因为footer这个时候优先级最低
//                if(lazyListState?.canScrollForward == false && lazyListState?.canScrollBackward == true &&  footerOffset.max != 0f) {
//                    val offset =  if(available.y + footerOffset.value < -footerOffset.max){
//                        -footerOffset.max - footerOffset.value  //保证不小于边界值
//                    }else{
//                        available.y
//                    }
//                    // 这个时候底部漏出来，那么translationY 是两者之和
//                    scroll(footerOffset, headerOffset, offset)
//                }else{
//                    Offset.Zero
//                }
//            }
//
//            available.y > 0  -> {
//                //拦截向上滑动，不能超过最大范围，这里只处理header ，因为header这个时候优先级最低
//                if(lazyListState?.canScrollBackward == false &&  headerOffset.max != 0f && headerOffset.value < 0){
//                    val offset =  if(available.y + headerOffset.value > 0){
//                        abs(headerOffset.value)  //保证不大于边界值
//                    }else{
//                        available.y
//                    }
//                    //说明在顶部，这时候footerOffset理论上也是0，这里写成这样为了更加直观
//                    scroll(headerOffset, NestedOffset("",0F, 0F), offset)
//                }else{
//                    Offset.Zero
//                }
//            }
//            else -> {
//                Offset.Zero
//            }
//        }
//    }
//
//
//    fun dispatchUserScroll(dragAmount: Offset){
//        when{
//            dragAmount.y < 0 -> {
//                if(dragger == headerOffset && headerOffset.max != 0f) {
//                    //向上时，header优先拦截
//                    onPreScroll(dragAmount,NestedScrollSource.Drag);
//                }else if(dragger == footerOffset && footerOffset.max != 0f){
//                    //向下时，footer优先拦截
//                    onPostScroll(Offset(0f,0f),dragAmount,NestedScrollSource.Drag)
//                }
//            }
//            dragAmount.y > 0 ->{
//                if(dragger == headerOffset && headerOffset.max != 0f) {
//                    //向下时，header优先拦截
//                    onPostScroll(Offset(0f,0f),dragAmount,NestedScrollSource.Drag)
//                }else if(dragger == footerOffset && footerOffset.max != 0f){
//                    onPreScroll(dragAmount,NestedScrollSource.Drag);
//                }
//            }
//            else -> {
//                Offset.Zero
//            }
//        }
//
//    }
//
//    private fun scroll(target: NestedOffset, offset : NestedOffset,canConsumed: Float): Offset {
//        return if (canConsumed.absoluteValue > 0.0f) {
//            target.value += canConsumed
//            //在这里更新而不是在协程中，避免同步事件触发多次
//            coroutineScope.launch {
//                contentOffset.value = lazyListState?.firstVisibleItemScrollOffset?.toFloat() ?: 0f;
//                graphicYOffset.value = target.value + offset.value  //更新偏移距离
//
//                lazyListState?.apply {
//                    if((this.firstVisibleItemIndex + this.layoutInfo.visibleItemsInfo.size) == this.layoutInfo.totalItemsCount){
//                        loadMore(); //利用公式触发加载更多
//                    }
//                }
//            }
//            Offset(0f, canConsumed)
//        } else {
//            Offset.Zero
//        }
//    }
//
//    override suspend fun onPreFling(available: Velocity): Velocity {
//        return super.onPreFling(available)
//    }
//
//    override suspend fun onPostFling(consumed: Velocity, available: Velocity): Velocity {
//        return super.onPostFling(consumed, available)
//    }
//
//    fun loadMore(){
//    }
//
//    fun initLazyState(state: LazyListState) {
//        lazyListState = state
//    }
//
//    fun dispatchUserDragger(dragger: NestedOffset?) {
//        this.dragger = dragger;
//    }
//
//}
