package ph.edu.comteq.datastoredemo

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first

class UserPreferences(){
    companion object {
        private val Context.dataStore: DataStore<Preferences>
            by preferencesDataStore("user_preferences")

        private val USERNAME_KEY = stringPreferencesKey("username")
        private val DARK_MODE = booleanPreferencesKey("dark_mode")

        suspend fun getUsername(context: Context): String? {
            val preferences = context.dataStore.data.first()
            return preferences[USERNAME_KEY]
        }

        suspend fun saveUsername(context: Context, username: String) {
            context.dataStore.edit { preferences ->
                preferences[USERNAME_KEY] = username
            }
        }

        suspend fun clearUsername(context: Context) {
            context.dataStore.edit { preferences ->
                preferences[USERNAME_KEY] = ""
            }
        }

        suspend fun getDarkMode(context: Context): Boolean {
            val preferences = context.dataStore.data.first()
            return preferences[DARK_MODE] ?: false
        }

        suspend fun saveDarkMode(context: Context, darkMode: Boolean) {
            context.dataStore.edit { preferences ->
                preferences[DARK_MODE] = darkMode
            }
        }
    }
}
