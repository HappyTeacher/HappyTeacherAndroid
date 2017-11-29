package org.jnanaprabodhini.happyteacherapp.dialog

import android.app.Dialog
import android.content.Context
import android.database.DataSetObserver
import android.os.Bundle
import android.support.annotation.StringRes
import android.view.ViewGroup
import android.view.Window
import android.widget.Adapter
import android.widget.ListAdapter
import android.widget.ListView
import kotlinx.android.synthetic.main.dialog_settings_choice.*
import org.jnanaprabodhini.happyteacherapp.R
import org.jnanaprabodhini.happyteacherapp.adapter.helper.FirebaseDataObserver
import org.jnanaprabodhini.happyteacherapp.extension.setVisibilityGone
import org.jnanaprabodhini.happyteacherapp.extension.setVisible
import org.jnanaprabodhini.happyteacherapp.util.PreferencesManager

/**
 * A dialog that presents a list of options to the user
 *  for changing settings.
 */
abstract class SettingsChoiceDialog(context: Context, @StringRes private val dialogHeaderTextId: Int,
                                    @StringRes private val dialogSubheaderTextId: Int):
        Dialog(context), FirebaseDataObserver {

    open val windowHeight = ViewGroup.LayoutParams.WRAP_CONTENT
    open val windowWidth = ViewGroup.LayoutParams.MATCH_PARENT

    val prefs: PreferencesManager by lazy {
        PreferencesManager.getInstance(context)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.dialog_settings_choice)

        window.setLayout(windowWidth, windowHeight)

        this.setCancelable(true)
        this.setCanceledOnTouchOutside(true)

        dialogHeader.setText(dialogHeaderTextId)
        dialogSubheader.setText(dialogSubheaderTextId)

        optionsListView.setVisibilityGone()
        subtopicChoiceProgressBar.setVisible()

        closeButton.setOnClickListener {
            this.dismiss()
        }

        configureOptionsListView(optionsListView)
    }

    protected fun setAdapter(adapter: ListAdapter) {
        optionsListView.adapter = adapter
        if (optionsListView.adapter.count > 0) onDataNonEmpty()
    }

    override fun onDataNonEmpty() {
        subtopicChoiceProgressBar.setVisibilityGone()

        optionsListView.setVisible()
        dialogHeader.setVisible()
        dialogSubheader.setVisible()
    }

    abstract fun configureOptionsListView(optionsListView: ListView)

}