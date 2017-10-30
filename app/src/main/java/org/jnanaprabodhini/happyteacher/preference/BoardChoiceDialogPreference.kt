package org.jnanaprabodhini.happyteacher.preference

import android.content.Context
import android.support.v7.preference.DialogPreference
import android.util.AttributeSet
import org.jnanaprabodhini.happyteacher.dialog.BoardChoiceDialog

/**
 * A simple Preference that launches the Dialog for choosing boards.
 *  This dialog already handles storing its value in SharedPreferences,
 *  so this Preference doesn't deal with setting or getting values.
 */
class BoardChoiceDialogPreference(context: Context, attrs: AttributeSet): DialogPreference(context, attrs) {
    override fun onClick() {
        BoardChoiceDialog(context).show()
    }
}