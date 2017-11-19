package org.jnanaprabodhini.happyteacherapp.dialog

import android.app.Dialog
import android.content.Context
import android.database.DataSetObserver
import android.os.Bundle
import android.support.annotation.StringRes
import android.widget.ListView
import kotlinx.android.synthetic.main.dialog_settings_choice.*
import org.jnanaprabodhini.happyteacherapp.R
import org.jnanaprabodhini.happyteacherapp.extension.setVisibilityGone
import org.jnanaprabodhini.happyteacherapp.extension.setVisible
import org.jnanaprabodhini.happyteacherapp.util.PreferencesManager

/**
 * A dialog that presents a list of options to the user
 *  for changing settings.
 */
abstract class SettingsChoiceDialog(context: Context, @StringRes val dialogHeaderTextId: Int, @StringRes val dialogSubheaderTextId: Int): Dialog(context) {

    val prefs: PreferencesManager by lazy {
        PreferencesManager.getInstance(context)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        setContentView(R.layout.dialog_settings_choice)

        dialogHeader.setText(dialogHeaderTextId)
        dialogSubheader.setText(dialogSubheaderTextId)

        optionsListView.setVisibilityGone()
        progressBar.setVisible()

        configureOptionsListView(optionsListView)

        if (optionsListView.adapter.count > 0) onDataPresent()

        optionsListView.adapter.registerDataSetObserver(object: DataSetObserver() {
            override fun onChanged() {
                onDataPresent()
            }
        })
    }

    private fun onDataPresent() {
        progressBar.setVisibilityGone()
        optionsListView.setVisible()
    }

    abstract fun configureOptionsListView(optionsListView: ListView)

}