package com.cmp.talklater.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.cmp.talklater.util.ThemeUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainViewmodel @Inject constructor() : ViewModel() {

    var theme by mutableStateOf(ThemeUtil.SYSTEM)
        private set

    fun setCurrentTheme(theme: ThemeUtil) {
        this.theme = theme
    }
}