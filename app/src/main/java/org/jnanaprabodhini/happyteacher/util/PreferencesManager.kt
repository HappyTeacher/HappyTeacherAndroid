package org.jnanaprabodhini.happyteacher.util

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import android.support.v4.os.ConfigurationCompat
import org.jnanaprabodhini.happyteacher.R

/**
 * PreferencesManager is a wrapper for the SharedPreferences API.
 *  It is used for storing preferences data around the app.
 */
class PreferencesManager(val context: Context) {
    private val preferences: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)

    fun setCurrentLanguageCode(code: String) {
        val languageCodeKey = context.getString(R.string.prefs_key_current_language_code)
        preferences.edit().putString(languageCodeKey, code).apply()
    }

    fun getCurrentLanguageCode(): String {
        val primaryLanguageCode = ConfigurationCompat.getLocales(context.resources.configuration)[0].language
        val languageCodeKey = context.getString(R.string.prefs_key_current_language_code)
        return preferences.getString(languageCodeKey, primaryLanguageCode)
    }

    fun setBoardId(boardId: String) {
        val boardPrefsKey = context.getString(R.string.prefs_key_board)
        val hasChosenBoardPrefsKey = context.getString(R.string.prefs_key_has_chosen_board)
        preferences.edit().putString(boardPrefsKey, boardId).apply()
        preferences.edit().putBoolean(hasChosenBoardPrefsKey, true).apply()
    }

    fun resetBoardChoice() {
        val hasChosenBoardPrefsKey = context.getString(R.string.prefs_key_has_chosen_board)
        preferences.edit().putBoolean(hasChosenBoardPrefsKey, false).apply()
    }

    // TODO: what should default board be?
    fun getBoardKey(): String = preferences.getString(context.getString(R.string.prefs_key_board), context.getString(R.string.maharashtra_state_board_key))

    fun hasChosenBoard(): Boolean = preferences.getBoolean(context.getString(R.string.prefs_key_has_chosen_board), false)

    fun setUserName(name: String) {
        val userNamePrefsKey = context.getString(R.string.prefs_key_user_name)
        preferences.edit().putString(userNamePrefsKey, name).apply()
    }

    fun getUserName() = preferences.getString(context.getString(R.string.prefs_key_user_name), "")

    fun setUserLocation(location: String) {
        val locationPrefsKey = context.getString(R.string.prefs_key_user_location)
        preferences.edit().putString(locationPrefsKey, location).apply()
    }

    fun getUserLocation() = preferences.getString(context.getString(R.string.prefs_key_user_location), "")

    fun setUserInstitution(institution: String) {
        val institutionPrefsKey = context.getString(R.string.prefs_key_user_institution)
        preferences.edit().putString(institutionPrefsKey, institution).apply()
    }

    fun getUserInstitution() = preferences.getString(context.getString(R.string.prefs_key_user_institution), "")
}