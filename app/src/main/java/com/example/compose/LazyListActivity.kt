package com.example.compose

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

class LazyListActivity : ComponentActivity() {

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        setContent {
            val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
            Scaffold(
                modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
                topBar = {
                    MediumTopAppBar(
                        title = {

                            Text(
                                "Medium TopAppBar",
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        },
                        navigationIcon = {
                            IconButton(onClick = { /* doSomething() */ }) {
                                Icon(
                                    imageVector = Icons.Filled.Menu,
                                    contentDescription = "Localized description"
                                )
                            }
                        },
                        actions = {
                            IconButton(onClick = { /* doSomething() */ }) {
                                Icon(
                                    imageVector = Icons.Filled.Favorite,
                                    contentDescription = "Localized description"
                                )
                            }
                        },
                        scrollBehavior = scrollBehavior
                    )
                },
                content = { innerPadding ->
                    LazyColumn(
                        contentPadding = innerPadding,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        val list = (0..75).map { it.toString() }
                        items(count = list.size) {
                            Text(
                                text = list[it],
                                style = MaterialTheme.typography.bodyLarge,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp)
                            )
                        }
                    }
                }
            )
        }

    }
}