package com.application.frontend.ui.screen.community.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage

@Composable
fun ImageGrid(urls: List<String>) {
    if (urls.isEmpty()) return
    if (urls.size == 1) {
        AsyncImage(model = urls[0], contentDescription = null, modifier = Modifier
            .fillMaxWidth()
            .height(220.dp))
    } else {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            AsyncImage(model = urls[0], contentDescription = null,
                modifier = Modifier.weight(1f).height(200.dp))
            AsyncImage(model = urls[1], contentDescription = null,
                modifier = Modifier.weight(1f).height(200.dp))
        }
    }
}