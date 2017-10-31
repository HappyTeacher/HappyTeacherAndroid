package org.jnanaprabodhini.happyteacher.dialog

import android.content.Context
import android.database.DataSetObservable
import android.database.DataSetObserver
import android.util.Log
import android.view.View
import android.widget.CheckedTextView
import android.widget.ListView
import com.google.firebase.firestore.FirebaseFirestore
import org.jnanaprabodhini.happyteacher.R
import org.jnanaprabodhini.happyteacher.activity.base.HappyTeacherActivity
import org.jnanaprabodhini.happyteacher.adapter.firestore.FirestoreObserverListAdapter
import org.jnanaprabodhini.happyteacher.adapter.helper.FirebaseDataObserver
import org.jnanaprabodhini.happyteacher.model.Board
import org.jnanaprabodhini.happyteacher.prefs

/**
 * A dialog for asking the user what Board/Syllabus they
 *  want to set as default.
 */
class BoardChoiceDialog(context: Context): SettingsChoiceDialog(context, R.string.choose_your_syllabus, R.string.you_can_change_this_in_your_settings_later) {

    override fun configureOptionsListView(optionsListView: ListView) {
        optionsListView.choiceMode = ListView.CHOICE_MODE_SINGLE

        val firestoreRoot = FirebaseFirestore.getInstance()
        val firestoreLocalized = firestoreRoot.collection(context.getString(R.string.localized)).document(prefs.getCurrentLanguageCode())
        val boardQuery = firestoreLocalized.collection(context.getString(R.string.boards)) // todo: ordering

        val emptyDataObserver = object: FirebaseDataObserver {}

        var checkedIndex = 0
        val boardChoiceAdapter = object: FirestoreObserverListAdapter<Board>(boardQuery, Board::class.java, R.layout.dialog_option_singlechoice, emptyDataObserver, context) {
            override fun populateView(view: View, model: Board, position: Int) {
                (view as CheckedTextView).text = model.name
                val key = getItemKey(position)
                if (key == prefs.getBoardKey()) {
                    checkedIndex = position
                }
            }
        }

        // Hacky way of setting an item to be checked.
        //  Calling `setItemChecked` in `populateView` doesn't work :(
        optionsListView.addOnLayoutChangeListener { _, _, _, _, _, _, _, _, _ ->
            optionsListView.setItemChecked(checkedIndex, true)
        }

        optionsListView.setOnItemClickListener { _, _, position, _ ->
            val selectedKey = boardChoiceAdapter.getItemKey(position)
            optionsListView.setItemChecked(position, true)
            prefs.setBoardId(selectedKey)
            boardChoiceAdapter.stopListening()
            dismiss()
        }

        boardChoiceAdapter.startListening()
        optionsListView.adapter = boardChoiceAdapter
    }

}