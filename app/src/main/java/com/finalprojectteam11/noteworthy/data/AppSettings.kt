package com.finalprojectteam11.noteworthy.data

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager

object AppSettings {
    private const val SELECTED_LANGUAGE_KEY = "selectedLanguage"
    private const val SELECTED_SORT_BY_KEY = "selectedSortBy"
    private const val SELECTED_QUERY_DIRECTION_KEY = "selectedQueryDirection"

    private lateinit var sharedPreferences: SharedPreferences

    fun init(context: Context) {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
    }

    var selectedLanguage: String
        get() = sharedPreferences.getString(SELECTED_LANGUAGE_KEY, "English") ?: "English"
        set(value) = sharedPreferences.edit().putString(SELECTED_LANGUAGE_KEY, value).apply()

    var selectedSortBy: String
        get() = sharedPreferences.getString(SELECTED_SORT_BY_KEY, "time") ?: "time"
        set(value) = sharedPreferences.edit().putString(SELECTED_SORT_BY_KEY, value).apply()

    var selectedQueryDirection: String
        get() = sharedPreferences.getString(SELECTED_QUERY_DIRECTION_KEY, "DESCENDING") ?: "DESCENDING"
        set(value) = sharedPreferences.edit().putString(SELECTED_QUERY_DIRECTION_KEY, value).apply()
}
