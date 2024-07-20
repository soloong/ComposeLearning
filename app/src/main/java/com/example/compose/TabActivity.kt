package com.example.compose

import android.os.Bundle
import android.view.MotionEvent
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveableStateHolder
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Red
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.motionEventSpy
import androidx.compose.ui.layout.layout
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.compose.ui.theme.ComposeTheme
import com.example.compose.ui.theme.PurpleGrey80
import kotlinx.coroutines.launch

const val PAGER_STATE_DRAG_START = 0;
const val PAGER_STATE_DRAGGING = 1;
const val PAGER_STATE_IDLE = 2;

class TabActivity : ComponentActivity() {
    val tabData = getTabList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ComposeTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainScreen()
                }
            }
        }

    }

    @OptIn(ExperimentalFoundationApi::class, ExperimentalComposeUiApi::class)
    @Composable
    fun MainScreen() {
        val pagerState = rememberPagerState(initialPage = 0) {
            tabData.size
        }
        var dragState by remember {
            mutableIntStateOf(PAGER_STATE_IDLE)
        }
        Column(modifier = Modifier.fillMaxSize()) {
            TabContent(pagerState, modifier = Modifier
                .weight(1f)
                .motionEventSpy { event ->
                    when (event.actionMasked) {
                        MotionEvent.ACTION_DOWN ->
                            dragState = PAGER_STATE_DRAG_START

                        MotionEvent.ACTION_MOVE ->
                            dragState = PAGER_STATE_DRAGGING

                        MotionEvent.ACTION_UP ->
                            dragState = PAGER_STATE_IDLE

                        else -> {
                            dragState = dragState
                        }

                    }
                }
            )
            TabLayout(tabData, pagerState,dragState)
        }
    }
}



@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TabLayout(tabData: List<Pair<String, ImageVector>>, pagerState: PagerState, dragState: Int) {

    val scope = rememberCoroutineScope()
    var selectIndex by remember { mutableIntStateOf(0) }
    /* val tabColor = listOf(
         Color.Gray,
         Color.Yellow,
         Color.Blue,
         Color.Red
     )
 */
    TabRow(
        selectedTabIndex = pagerState.currentPage,
        divider = {
            Spacer(modifier = Modifier.height(0.dp))
        },
        indicator = { tabPositions ->
            TabRowDefaults.Indicator(
                modifier = Modifier.tabIndicatorOffset(tabPositions[pagerState.currentPage]),
                height = 0.dp,
                color = Color.White
            )
        },
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
    ) {
        tabData.forEachIndexed { index, s ->

            if(dragState == PAGER_STATE_DRAG_START){
                selectIndex = pagerState.currentPage
            }
            val isSelectedItem =
                if (dragState == PAGER_STATE_DRAG_START || dragState == PAGER_STATE_DRAGGING || pagerState.isScrollInProgress) {
                    selectIndex == index
                } else if (pagerState.targetPage == index) {
                    selectIndex = index;
                    true
                } else {
                    false
                }
            val tabTintColor = if (isSelectedItem) {
                Red
            } else {
                LocalContentColor.current
            }
            Tab(
                modifier = Modifier.drawBehind {
                   if(isSelectedItem) {
                       drawCircle( color = PurpleGrey80, radius = (size.minDimension - 8.dp.toPx())/2f)
                   }
                },
                selected = pagerState.currentPage == index,
                onClick = {
                    scope.launch {
                        selectIndex = index
                        pagerState.animateScrollToPage(index)
                    }
                },
                icon = {
                    Icon(imageVector = s.second, contentDescription = null, tint = tabTintColor,
                        modifier = Modifier
                            .drawWithContent {
                                drawContent()
                            }
                            .layout { measurable, constraints ->
                                val placeable = measurable.measure(constraints)
                                layout(placeable.width, placeable.height) {
                                    placeable.placeRelative(0, 15)
                                }
                            }
                    )
                },
                text = {
                    Text(text = s.first, color = tabTintColor, fontSize = 12.sp, modifier = Modifier.scale(0.8f))
                },
                selectedContentColor = TabRowDefaults.containerColor
            )
        }
    }
}



@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TabContent(
    pagerState: PagerState,
    modifier: Modifier
) {
    HorizontalPager(state = pagerState, modifier = modifier) { index ->
        when (index) {
            0 -> {
                HomeScreen()
            }

            1 -> {
                SearchScreen()
            }

            2 -> {
                FavoritesScreen()
            }

            3 -> {
                SettingsScreen()
            }
        }

    }
}


private fun getTabList(): List<Pair<String, ImageVector>> {
    return listOf(
        "Home" to Icons.Default.Home,
        "Search" to Icons.Default.Search,
        "Favorites" to Icons.Default.Favorite,
        "Settings" to Icons.Default.Settings,
    )
}




