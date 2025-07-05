package com.cmp.talklater

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.LayoutDirection
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.cmp.talklater.route.Screen
import com.cmp.talklater.ui.screens.HomeScreen
import com.cmp.talklater.ui.screens.PermissionScreen
import com.cmp.talklater.ui.screens.SettingsScreen
import com.cmp.talklater.ui.theme.TalkLaterTheme
import com.cmp.talklater.util.ThemeUtil
import com.cmp.talklater.viewmodel.MainViewmodel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val prefs = this.getSharedPreferences("theme_pref", MODE_PRIVATE)
        var theme = ThemeUtil.valueOf(prefs.getString("theme", ThemeUtil.SYSTEM.name).toString())

        setContent {

            val mainViewmodel: MainViewmodel = hiltViewModel()
            if (mainViewmodel.theme != null) {
                theme = mainViewmodel.theme!!
            } else {
                mainViewmodel.setCurrentTheme(theme)
            }

            TalkLaterTheme(theme = theme) {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Surface(
                        modifier = Modifier
                            .padding(
                                top = innerPadding.calculateTopPadding(),
                                bottom = innerPadding.calculateBottomPadding(),
                                start = innerPadding.calculateStartPadding(LayoutDirection.Ltr),
                                end = innerPadding.calculateEndPadding(LayoutDirection.Ltr),
                            )
                    ) {
                        App(mainViewmodel)
                    }
                }
            }
        }
    }

    companion object {
        const val REJECTED = 5
        const val MISSED = 3
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun App(mainViewmodel: MainViewmodel) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.Home.route
    ) {
        composable(Screen.Permission.route) {
            PermissionScreen(
                onPermissionDenied = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Permission.route) { inclusive = true }
                    }
                },
                onPermissionGranted = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Permission.route) { inclusive = true }
                    }
                }
            )
        }
        composable(Screen.Home.route) {
            HomeScreen(
                onOpenSettings = {
                    navController.navigate(Screen.Settings.route)
                }
            )
        }
        composable(Screen.Settings.route) {
            SettingsScreen(
                mainViewmodel,
                onBack = { navController.popBackStack() }
            )
        }
    }
}
