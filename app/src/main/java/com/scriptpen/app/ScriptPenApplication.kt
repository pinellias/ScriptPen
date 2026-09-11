package com.scriptpen.app

import android.app.Application
import com.scriptpen.app.data.AppDatabase
import com.scriptpen.app.data.SettingsRepository

class ScriptPenApplication : Application() {

    // 懒加载的数据库与全局设置仓库，供各 ViewModel 共享
    val database by lazy { AppDatabase.getDatabase(this) }
    val settingsRepository by lazy { SettingsRepository(this) }
}
