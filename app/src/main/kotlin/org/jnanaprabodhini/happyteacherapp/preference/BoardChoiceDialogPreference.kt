package org.jnanaprabodhini.happyteacherapp.preference

import android.content.Context
import android.support.v7.preference.DialogPreference
import android.util.AttributeSet
import org.jnanaprabodhini.happyteacherapp.dialog.BoardChoiceDialog
import org.jnanaprabodhini.happyteacherapp.util.PreferencesManager

/**
 * A simple Preference that launches the Dialog for choosing boards.
 *  This dialog already handles storing its value in SharedPreferences,
 *  so this Preference doesn't deal with setting or getting values.
 */
class BoardChoiceDialogPreference(context: Context, attrs: AttributeSet): DialogPreference(context, attrs) {
    override fun onClick() {
        val dialog = BoardChoiceDialog(context)

        dialog.setOnDismissListener {
            notifyChanged()
        }

        dialog.show()
    }

    override fun getSummary(): CharSequence {
        val boardName = PreferencesManager.getInstance(context).getBoardName()

        return if (boardName.isEmpty()) {
            super.getSummary()
        } else {
            boardName
        }
    }
}