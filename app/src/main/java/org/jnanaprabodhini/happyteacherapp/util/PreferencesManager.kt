package org.jnanaprabodhini.happyteacherapp.util

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import android.support.v4.os.ConfigurationCompat
import com.crashlytics.android.Crashlytics
import org.jnanaprabodhini.happyteacherapp.R

/**
 * PreferencesManager is a wrapper for the SharedPreferences API.
 *  It is used for storing preferences data around the app.
 */
class PreferencesManager private constructor(val context: Context) {

    companion object {
        fun getInstance(context: Context): PreferencesManager = PreferencesManager(context)
    }

    val preferences: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)

    fun setCurrentLanguageCode(code: String) {
        val languageCodeKey = context.getString(R.string.prefs_key_current_language_code)
        preferences.edit().putString(languageCodeKey, code).apply()
        Crashlytics.setString("current_language_code", code)
    }

    fun getCurrentLanguageCode(): String {
        val primaryLanguageCode = ConfigurationCompat.getLocales(context.resources.configuration)[0].language
        val languageCodeKey = context.getString(R.string.prefs_key_current_language_code)
        return preferences.getString(languageCodeKey, primaryLanguageCode)
    }

    fun setBoard(boardName: String, boardId: String) {
        setBoardName(boardName)
        setBoardId(boardId)
        setBoardChosen()
    }

    private fun setBoardName(boardName: String) {
        val boardNamePrefsKey = context.getString(R.string.prefs_key_board_name)
        preferences.edit().putString(boardNamePrefsKey, boardName).apply()
    }

    private fun setBoardId(boardId: String) {
        val boardIdPrefsKey = context.getString(R.string.prefs_key_board_id)
        preferences.edit().putString(boardIdPrefsKey, boardId).apply()
    }

    private fun setBoardChosen() {
        val hasChosenBoardPrefsKey = context.getString(R.string.prefs_key_has_chosen_board)
        preferences.edit().putBoolean(hasChosenBoardPrefsKey, true).apply()
    }

    fun resetBoardChoice() {
        val hasChosenBoardPrefsKey = context.getString(R.string.prefs_key_has_chosen_board)
        preferences.edit().putBoolean(hasChosenBoardPrefsKey, false).apply()
    }

    // TODO: what should default board be?
    fun getBoardKey(): String = preferences.getString(context.getString(R.string.prefs_key_board_id), context.getString(R.string.maharashtra_board_key))

    fun getBoardName(): String = preferences.getString(context.getString(R.string.prefs_key_board_name), "")

    fun hasChosenBoard(): Boolean = preferences.getBoolean(context.getString(R.string.prefs_key_has_chosen_board), false)

    fun setUserName(name: String) {
        val userNamePrefsKey = context.getString(R.string.prefs_key_user_name)
        preferences.edit().putString(userNamePrefsKey, name).apply()
    }

    fun getUserName(): String = preferences.getString(context.getString(R.string.prefs_key_user_name), "")

    fun setUserLocation(location: String) {
        val locationPrefsKey = context.getString(R.string.prefs_key_user_location)
        preferences.edit().putString(locationPrefsKey, location).apply()
    }

    fun getUserLocation(): String = preferences.getString(context.getString(R.string.prefs_key_user_location), "")

    fun setUserInstitution(institution: String) {
        val institutionPrefsKey = context.getString(R.string.prefs_key_user_institution)
        preferences.edit().putString(institutionPrefsKey, institution).apply()
    }

    fun getUserInstitution(): String = preferences.getString(context.getString(R.string.prefs_key_user_institution), "")

    fun setUserRole(role: String) {
        val rolePrefsKey = context.getString(R.string.prefs_key_user_role)
        preferences.edit().putString(rolePrefsKey, role).apply()
    }

    fun getUserRole(): String = preferences.getString(context.getString(R.string.prefs_key_user_role), "")

    fun userIsMod() = getUserRole() == UserRoles.MODERATOR

    fun userIsAdmin() = getUserRole() == UserRoles.ADMIN

    fun clearUserProfileData() {
        setUserLocation("")
        setUserName("")
        setUserInstitution("")
        setUserRole("")
    }
}