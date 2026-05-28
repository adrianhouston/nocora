package io.nocora

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class SettingsViewModel(app: Application) : AndroidViewModel(app) {
    val repo = SettingsRepository(app)

    val isDynamicColor = repo.isDynamicColor

    fun setDynamicColor(value: Boolean) {
        viewModelScope.launch { repo.setDynamicColor(value) }
    }
}
