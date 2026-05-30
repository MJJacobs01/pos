package com.refresh.pos.ui

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.navigation.compose.rememberNavController
import com.refresh.pos.ui.navigation.PosNavHost
import com.refresh.pos.ui.theme.PosTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun attachBaseContext(newBase: Context?) {
        val locale = newBase?.let { LocaleHelper.getSavedLocale(it) } ?: "en"
        super.attachBaseContext(
            newBase?.let { LocaleHelper.wrapContext(it, locale) } ?: newBase
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PosTheme {
                PosApp()
            }
        }
    }
}

@Composable
private fun PosApp() {
    val navController = rememberNavController()
    PosNavHost(navController = navController)
}
