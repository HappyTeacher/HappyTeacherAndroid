package org.jnanaprabodhini.happyteacher

import android.content.Context
import android.content.SharedPreferences

/**
 * Created by grahamearley on 9/13/17.
 */
class PreferencesManager(val context: Context) {
    val PREFS_NAME = "DEFAULT_PREFS"
    val preferences: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    val BOARD = "BOARD"

    fun setBoard(boardKey: String) {
        preferences.edit().putString(BOARD, boardKey)
    }

    fun getBoard() {
        preferences.getString(BOARD, context.getString(R.string.maharashtra_state_board))
    }

}