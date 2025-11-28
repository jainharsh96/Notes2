package com.notes.shared.utils

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DebugWindow(onDismissRequest: (() -> Unit)) {
    Popup(
        alignment = Alignment.TopStart,
        onDismissRequest = onDismissRequest,
        properties = PopupProperties(dismissOnClickOutside = false)
    ) {
        val listState = rememberLazyListState()
        val items = remember { mutableStateListOf<InMemoryLog>() }
        LaunchedEffect(Unit) {
            NotesLogger.inMemoryLogFlow.collect { log ->
                items.add(log)
                if (items.size > 100) {
                    items.removeFirst()
                }
                listState.scrollToItem(items.size - 1)
            }
        }
        LazyColumn(
            state = listState,
            modifier = Modifier.fillMaxWidth().heightIn(max = 500.dp)
                .background(color = Color.LightGray).padding(horizontal = 16.dp)
        ) {
            stickyHeader {
                Row(
                    modifier = Modifier.padding(16.dp).fillMaxWidth()
                        .background(color = Color.LightGray)
                        .padding(bottom = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(modifier = Modifier, text = "Logs")
                    Text(
                        modifier = Modifier.clickable(onClick = onDismissRequest),
                        text = "Close"
                    )
                }
            }
            items(items = items) { log ->
                Column(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp)
                        .border(width = 1.dp, color = Color.Black)
                        .padding(horizontal = 8.dp, vertical = 12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(modifier = Modifier, text = log.tag)
                        Text(modifier = Modifier, text = log.time)
                    }
                    Text(modifier = Modifier, text = log.message)
                }
            }
        }
    }
}