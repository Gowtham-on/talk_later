package com.cmp.talklater.route

sealed class Screen(val route: String) {
    object Permission: Screen("Permission")
    object Home: Screen("Home")
    object Settings: Screen("Settings")
}