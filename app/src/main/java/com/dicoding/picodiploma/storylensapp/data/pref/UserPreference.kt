package com.dicoding.picodiploma.storylensapp.data.pref

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

//variabel yang digunakan untuk menyimpan data user
val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "session")
class UserPreference private constructor(private val dataStore: DataStore<Preferences>) {

    //menyimpan session user
    suspend fun saveSession(user: UserModel?) {
        user?.let {
            dataStore.edit { preferences ->
                preferences[EMAIL_KEY] = it.email
                preferences[TOKEN_KEY] = it.token
                preferences[IS_LOGIN_KEY] = true
            }
        }
    }
    //fungsi untuk logout
    suspend fun logout() {
        dataStore.edit { preferences ->
            preferences.clear()
        }
    }
    //mendapatkan session user
    fun getSession(): Flow<UserModel> {
        return dataStore.data.map { preferences ->
            val userModel = UserModel(
                preferences[EMAIL_KEY] ?: "",
                preferences[TOKEN_KEY] ?: "",
                preferences[IS_LOGIN_KEY] ?: false
            )
            userModel
        }
    }
    companion object {
        @Volatile
        //variabel yang digunakan untuk menyimpan instance dari UserPreference
        private var INSTANCE: UserPreference? = null
        private val EMAIL_KEY = stringPreferencesKey("email")
        private val TOKEN_KEY = stringPreferencesKey("token")
        private val IS_LOGIN_KEY = booleanPreferencesKey("isLogin")

        //fungsi untuk mendapatkan instance dari UserPreference
        fun getInstance(dataStore: DataStore<Preferences>): UserPreference {
            return INSTANCE ?: synchronized(this) {
                val instance = UserPreference(dataStore)
                INSTANCE = instance
                instance
            }
        }
    }
}