package com.scriptpen.app.data

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "settings")

/**
 * 全局设置（DataStore）：
 * - 是否使用深色主题
 * - 新建文档的默认背景色 / 默认字号
 */
class SettingsRepository(private val context: Context) {

    private val darkThemeKey = booleanPreferencesKey("dark_theme")
    private val defaultBgKey = intPreferencesKey("default_background")
    private val defaultFontKey = intPreferencesKey("default_font_size")

    val isDarkTheme: Flow<Boolean> = context.dataStore.data.map { it[darkThemeKey] ?: true }
    val defaultBackground: Flow<Int> =
        context.dataStore.data.map { it[defaultBgKey] ?: 0xFF1E1E1E.toInt() }
    val defaultFontSize: Flow<Float> =
        context.dataStore.data.map { (it[defaultFontKey] ?: 16).toFloat() }

    suspend fun setDarkTheme(enabled: Boolean) {
        context.dataStore.edit { it[darkThemeKey] = enabled }
    }

    suspend fun setDefaultBackground(color: Int) {
        context.dataStore.edit { it[defaultBgKey] = color }
    }

    suspend fun setDefaultFontSize(size: Float) {
        context.dataStore.edit { it[defaultFontKey] = size.toInt() }
    }
}
