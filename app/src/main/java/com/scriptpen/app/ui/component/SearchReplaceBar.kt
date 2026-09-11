package com.scriptpen.app.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun SearchReplaceBar(
    query: String,
    replacement: String,
    onQueryChange: (String) -> Unit,
    onReplacementChange: (String) -> Unit,
    onFindPrev: () -> Unit,
    onFindNext: () -> Unit,
    onReplaceNext: () -> Unit,
    onReplaceAll: () -> Unit,
    onClose: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
            OutlinedTextField(
                value = query,
                onValueChange = onQueryChange,
                placeholder = { Text("搜索") },
                singleLine = true,
                modifier = Modifier.weight(1f)
            )
            IconButton(onClick = onFindPrev) { Icon(Icons.Filled.ArrowDropUp, "上一个") }
            IconButton(onClick = onFindNext) { Icon(Icons.Filled.ArrowDropDown, "下一个") }
            IconButton(onClick = onClose) { Icon(Icons.Filled.Close, "关闭") }
        }
        Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
            OutlinedTextField(
                value = replacement,
                onValueChange = onReplacementChange,
                placeholder = { Text("替换为") },
                singleLine = true,
                modifier = Modifier.weight(1f)
            )
            TextButton(onClick = onReplaceNext) {
                Icon(Icons.Filled.SwapHoriz, null)
                Text("替换")
            }
            TextButton(onClick = onReplaceAll) { Text("全部") }
        }
    }
}
