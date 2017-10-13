package org.jnanaprabodhini.happyteacher.dialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.support.annotation.StringRes
import android.view.View
import android.widget.CheckedTextView
import android.widget.ListView
import com.firebase.ui.database.FirebaseListAdapter
import com.firebase.ui.database.FirebaseListOptions
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.dialog_board_choice.*
import org.jnanaprabodhini.happyteacher.R
import org.jnanaprabodhini.happyteacher.extension.getBaseReferenceForCurrentLanguage
import org.jnanaprabodhini.happyteacher.model.Board
import org.jnanaprabodhini.happyteacher.prefs

/**
 * A dialog that presents a list of options to the user
 *  for changing settings.
 */
abstract class SettingsChoiceDialog(context: Context, @StringRes val dialogHeaderTextId: Int, @StringRes val dialogSubheaderTextId: Int): Dialog(context) {

    override fun onCreate(savedInstanceState: Bundle?) {
        setContentView(R.layout.dialog_board_choice)

        configureOptionsListView(boardOptionsListView)

        dialogHeader.setText(dialogHeaderTextId)
        dialogSubheader.setText(dialogSubheaderTextId)
    }

    abstract fun configureOptionsListView(optionsListView: ListView)

}