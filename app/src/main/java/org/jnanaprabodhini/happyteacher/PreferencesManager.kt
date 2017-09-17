package org.jnanaprabodhini.happyteacher

import android.content.Context
import android.content.SharedPreferences
import android.os.LocaleList
import android.support.v4.os.ConfigurationCompat
import java.util.*

/**
 * PreferencesManager is a wrapper for the SharedPreferences API.
 *  It is used for storing preferences data around the app.
 */
class PreferencesManager(val context: Context) {
    private val PREFS_NAME = "DEFAULT_PREFS"
    private val preferences: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    private val CURRENT_LANGUAGE_CODE: String = "CURRENT_LANGUAGE_CODE"
    private val BOARD = "BOARD"
    private val HAS_CHOSEN_BOARD = "HAS_CHOSEN_BOARD"

    fun setCurrentLanguageCode(code: String) {
        preferences.edit().putString(CURRENT_LANGUAGE_CODE, code).apply()
    }

    fun getCurrentLanguageCode(): String {
        val primaryLanguageCode = ConfigurationCompat.getLocales(context.resources.configuration)[0].language
        return preferences.getString(CURRENT_LANGUAGE_CODE, primaryLanguageCode)
    }

    fun setBoardKey(boardKey: String) {
        preferences.edit().putString(BOARD, boardKey).apply()
        preferences.edit().putBoolean(HAS_CHOSEN_BOARD, true).apply()
    }

    fun getBoardKey(): String = preferences.getString(BOARD, context.getString(R.string.maharashtra_state_board_key))

    fun hasChosenBoard(): Boolean = preferences.getBoolean(HAS_CHOSEN_BOARD, false)
}