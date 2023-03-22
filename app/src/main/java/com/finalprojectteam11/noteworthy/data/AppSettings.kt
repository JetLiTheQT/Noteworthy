package com.finalprojectteam11.noteworthy.data

import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.preference.PreferenceManager

object AppSettings {
    private const val SELECTED_LANGUAGE_KEY = "selectedLanguage"
    private const val SELECTED_SORT_BY_KEY = "selectedSortBy"
    private const val SELECTED_QUERY_DIRECTION_KEY = "selectedQueryDirection"
    private const val DISPLAY_CHOICE_KEY = "displayChoice"


    private lateinit var sharedPreferences: SharedPreferences

    private val _settingsUpdated = MutableLiveData<Boolean>()
    val settingsUpdated: LiveData<Boolean> get() = _settingsUpdated

    fun init(context: Context) {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
    }

    var selectedSortBy: String
        get() = sharedPreferences.getString(SELECTED_SORT_BY_KEY, "Date (Newest-Oldest)") ?: "Date (Newest-Oldest)"
        set(value) = sharedPreferences.edit().putString(SELECTED_SORT_BY_KEY, value).apply()

    var displayChoice: Boolean
        get() = sharedPreferences.getBoolean(DISPLAY_CHOICE_KEY, false)
        set(value) = sharedPreferences.edit().putBoolean(DISPLAY_CHOICE_KEY, value).apply()

    fun updateSettings() {
        _settingsUpdated.value = true
    }

    fun resetSettingsUpdatedFlag() {
        _settingsUpdated.value = false
    }
}
