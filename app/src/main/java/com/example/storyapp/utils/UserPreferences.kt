package com.example.storyapp.utils

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

data class AuthData(
    var name: String,
    var userId: String,
    var token: String,
)

class UserPreferences private constructor(private val dataStore: DataStore<Preferences>) {

    private val NAME_KEY = stringPreferencesKey("name_setting")
    private val USERID_KEY = stringPreferencesKey("userid_setting")
    private val TOKEN_KEY = stringPreferencesKey("token_setting")

    fun getUserSetting(): Flow<AuthData> {
        return dataStore.data.map { preferences ->
            val name = preferences[NAME_KEY] ?: ""
            val userId = preferences[USERID_KEY] ?: ""
            val token = preferences[TOKEN_KEY] ?: ""
            AuthData(name, userId, token)
        }
    }

    suspend fun saveUserSetting(name: String, userId: String, token: String) {
        dataStore.edit { preferences ->
            preferences[NAME_KEY] = name
            preferences[USERID_KEY] = userId
            preferences[TOKEN_KEY] = token
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: UserPreferences? = null

        fun getInstance(dataStore: DataStore<Preferences>): UserPreferences {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: UserPreferences(dataStore).also { INSTANCE = it }
            }
        }
    }
}
