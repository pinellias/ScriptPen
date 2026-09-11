package com.scriptpen.app

import android.app.Application
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.scriptpen.app.ui.screen.DocumentListScreen
import com.scriptpen.app.ui.screen.EditorScreen
import com.scriptpen.app.ui.theme.ScriptPenTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ScriptPenTheme(darkTheme = true) {
                AppNavigation(application)
            }
        }
    }
}

@Composable
private fun AppNavigation(application: Application) {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "list") {
        composable("list") {
            DocumentListScreen(navController, application)
        }
        composable("editor/{id}") { backStackEntry ->
            val id = backStackEntry.arguments?.getString("id")?.toLongOrNull() ?: 0L
            EditorScreen(navController, id, application)
        }
    }
}
