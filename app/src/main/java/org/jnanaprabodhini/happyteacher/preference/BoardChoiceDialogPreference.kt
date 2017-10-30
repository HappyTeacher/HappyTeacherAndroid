package org.jnanaprabodhini.happyteacher.preference

import android.content.Context
import android.support.v7.preference.DialogPreference
import android.util.AttributeSet
import org.jnanaprabodhini.happyteacher.dialog.BoardChoiceDialog

/**
 * Created by grahamearley on 10/30/17.
 */
class BoardChoiceDialogPreference(context: Context, attrs: AttributeSet): DialogPreference(context, attrs) {
    override fun onClick() {
        BoardChoiceDialog(context).show()
    }
}