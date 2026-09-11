package com.scriptpen.app.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.scriptpen.app.ui.theme.PRESET_BACKGROUNDS

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsSheet(
    currentBackground: Int,
    currentFontSize: Float,
    onBackgroundChange: (Int) -> Unit,
    onFontSizeChange: (Float) -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState()
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text("外观设置", style = MaterialTheme.typography.titleLarge)
            Spacer(Modifier.height(12.dp))
            Text("背景颜色", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                PRESET_BACKGROUNDS.forEach { colorInt ->
                    val selected = colorInt == currentBackground
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color(colorInt))
                            .then(
                                if (selected) {
                                    Modifier.border(3.dp, MaterialTheme.colorScheme.primary, CircleShape)
                                } else {
                                    Modifier
                                }
                            )
                            .clickable { onBackgroundChange(colorInt) }
                    )
                }
            }
            Spacer(Modifier.height(20.dp))
            Text(
                "字体大小：${currentFontSize.toInt()} sp",
                style = MaterialTheme.typography.titleMedium
            )
            Slider(
                value = currentFontSize,
                onValueChange = onFontSizeChange,
                valueRange = 12f..28f,
                steps = 16
            )
            Spacer(Modifier.height(8.dp))
        }
    }
}
