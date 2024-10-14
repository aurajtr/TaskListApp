package com.example.tracklist.util

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
import java.util.prefs.Preferences

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class PreferencesManager(context: Context) {

    private val dataStore = context.dataStore

    companion object {
        val SORT_ORDER_KEY = booleanPreferencesKey("sort_order")
        val THEME_KEY = booleanPreferencesKey("is_dark_theme")
    }

    val preferenceFlow: Flow<UserPreferences> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            val sortOrder = preferences[SORT_ORDER_KEY] ?: false
            val isDarkTheme = preferences[THEME_KEY] ?: false
            UserPreferences(sortOrder, isDarkTheme)
        }

    suspend fun updateSortOrder(isAscending: Boolean) {
        dataStore.edit { preferences ->
            preferences[SORT_ORDER_KEY] = isAscending
        }
    }

    suspend fun updateTheme(isDarkTheme: Boolean) {
        dataStore.edit { preferences ->
            preferences[THEME_KEY] = isDarkTheme
        }
    }
}

data class UserPreferences(
    val sortOrderAscending: Boolean,
    val isDarkTheme: Boolean
)