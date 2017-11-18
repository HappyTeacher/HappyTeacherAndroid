package org.jnanaprabodhini.happyteacherapp.dialog

import android.widget.ArrayAdapter
import android.widget.ListView
import org.jnanaprabodhini.happyteacherapp.R
import org.jnanaprabodhini.happyteacherapp.activity.base.BottomNavigationActivity
import org.jnanaprabodhini.happyteacherapp.util.LocaleManager

/**
 * A Dialog for choosing language from a BottomNavigationActivity.
 */
class LanguageChoiceDialog(val activity: BottomNavigationActivity): SettingsChoiceDialog(activity, R.string.choose_your_language, R.string.you_can_change_this_setting_later) {

    override fun configureOptionsListView(optionsListView: ListView) {

        optionsListView.choiceMode = ListView.CHOICE_MODE_SINGLE

        val supportedLanguages = LocaleManager.getSupportedLanguagesWithTitles(activity)
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
}