package org.jnanaprabodhini.happyteacher.dialog

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ListAdapter
import android.widget.ListView
import org.jnanaprabodhini.happyteacher.R
import org.jnanaprabodhini.happyteacher.activity.parent.BottomNavigationActivity

/**
 * Created by grahamearley on 10/13/17.
 */
class LanguageChoiceDialog(val activity: BottomNavigationActivity): SettingsChoiceDialog(activity, R.string.choose_your_language, R.string.you_can_change_this_in_your_settings_later) {

    override fun configureOptionsListView(optionsListView: ListView) {

        val supportedLanguages = arrayOf(
                LocaleCodeWithTitle("en", "English"),
                LocaleCodeWithTitle("mr", "Marathi")
        )

        val supportedLanguagesAdapter = LanguageListAdapter(activity, supportedLanguages)

        optionsListView.adapter = supportedLanguagesAdapter
    }

    class LocaleCodeWithTitle(val code: String, val title: String) {
        override fun toString(): String = title
    }

    class LanguageListAdapter(val activity: BottomNavigationActivity, val items: Array<LocaleCodeWithTitle>):
            ArrayAdapter<LocaleCodeWithTitle>(activity, R.layout.select_dialog_singlechoice_material, items) {

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            val view = super.getView(position, convertView, parent)
            view.setOnClickListener{
                activity.changeLocaleAndRefresh(items[position].code)
            }

            return view
        }
    }
}