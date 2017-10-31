package org.jnanaprabodhini.happyteacher.dialog

import android.widget.ArrayAdapter
import android.widget.ListView
import org.jnanaprabodhini.happyteacher.R
import org.jnanaprabodhini.happyteacher.activity.base.BottomNavigationActivity
import org.jnanaprabodhini.happyteacher.prefs

/**
 * A Dialog for choosing language from a BottomNavigationActivity.
 */
class LanguageChoiceDialog(val activity: BottomNavigationActivity): SettingsChoiceDialog(activity, R.string.choose_your_language, R.string.you_can_change_this_setting_later) {

    override fun configureOptionsListView(optionsListView: ListView) {

        optionsListView.choiceMode = ListView.CHOICE_MODE_SINGLE

        val supportedLanguages = arrayOf(
                LocaleCodeWithTitle("en", context.getString(R.string.english_in_english)),
                LocaleCodeWithTitle("mr", context.getString(R.string.marathi_in_marathi))
        )

        val supportedLanguagesAdapter = ArrayAdapter(context, R.layout.dialog_option_singlechoice, supportedLanguages)

        optionsListView.adapter = supportedLanguagesAdapter

        // Set current language selected:
        val currentLanguageIndex = supportedLanguages.indexOfFirst { it.code == prefs.getCurrentLanguageCode() }
        optionsListView.setItemChecked(currentLanguageIndex, true)

        optionsListView.setOnItemClickListener { _, _, position, _ ->
            optionsListView.setItemChecked(position, true)
            dismiss()
            activity.changeLocaleAndRefresh(supportedLanguages[position].code)
        }
    }

    data class LocaleCodeWithTitle(val code: String, val title: String) {
        override fun toString(): String = title
    }
}