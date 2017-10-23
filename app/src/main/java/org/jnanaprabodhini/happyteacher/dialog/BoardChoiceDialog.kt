package org.jnanaprabodhini.happyteacher.dialog

import android.content.Context
import android.view.View
import android.widget.CheckedTextView
import android.widget.ListView
import com.firebase.ui.database.FirebaseListAdapter
import com.firebase.ui.database.FirebaseListOptions
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.dialog_settings_choice.*
import org.jnanaprabodhini.happyteacher.R
import org.jnanaprabodhini.happyteacher.activity.base.HappyTeacherActivity
import org.jnanaprabodhini.happyteacher.adapter.firestore.FirestoreObserverListAdapter
import org.jnanaprabodhini.happyteacher.adapter.helper.FirebaseDataObserver
import org.jnanaprabodhini.happyteacher.extension.getBaseReferenceForCurrentLanguage
import org.jnanaprabodhini.happyteacher.model.Board
import org.jnanaprabodhini.happyteacher.prefs

/**
 * A dialog for asking the user what Board/Syllabus they
 *  want to set as default.
 */
class BoardChoiceDialog(val activity: HappyTeacherActivity): SettingsChoiceDialog(activity, R.string.choose_your_syllabus, R.string.you_can_change_this_in_your_settings_later) {

    override fun configureOptionsListView(optionsListView: ListView) {
        optionsListView.choiceMode = ListView.CHOICE_MODE_SINGLE

        val boardQuery = activity.firestoreLocalized.collection("boards") // todo: ordering

        val emptyDataObserver = object: FirebaseDataObserver {}

        val boardChoiceAdapter = object: FirestoreObserverListAdapter<Board>(boardQuery, Board::class.java, R.layout.dialog_option_singlechoice, emptyDataObserver, activity) {
            override fun populateView(view: View, model: Board) {
                (view as CheckedTextView).text = model.name
            }
        }

        optionsListView.setOnItemClickListener { _, _, position, _ ->
            val selectedKey = boardChoiceAdapter.getItemKey(position)
            optionsListView.setItemChecked(position, true)
            prefs.setBoardKey(selectedKey)
            boardChoiceAdapter.stopListening()
            dismiss()
        }

        boardChoiceAdapter.startListening()
        optionsListView.adapter = boardChoiceAdapter
    }

}