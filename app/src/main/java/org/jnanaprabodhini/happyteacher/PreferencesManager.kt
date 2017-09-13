package org.jnanaprabodhini.happyteacher

import android.content.Context
import android.content.SharedPreferences

/**
 * Created by grahamearley on 9/13/17.
 */
class PreferencesManager(val context: Context) {
    private val PREFS_NAME = "DEFAULT_PREFS"
    private val preferences: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    private val BOARD = "BOARD"

    fun setBoardKey(boardKey: String) {
        preferences.edit().putString(BOARD, boardKey)
    }

    fun getBoardKey(): String = preferences.getString(BOARD, context.getString(R.string.maharashtra_state_board))

}