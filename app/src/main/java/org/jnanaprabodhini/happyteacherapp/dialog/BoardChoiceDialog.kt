package org.jnanaprabodhini.happyteacherapp.dialog

import android.content.Context
import android.view.View
import android.widget.CheckedTextView
import android.widget.ListView
import com.google.firebase.firestore.FirebaseFirestore
import org.jnanaprabodhini.happyteacherapp.R
import org.jnanaprabodhini.happyteacherapp.adapter.firestore.FirestoreObservableListAdapter
import org.jnanaprabodhini.happyteacherapp.adapter.helper.FirebaseDataObserver
import org.jnanaprabodhini.happyteacherapp.model.Board

/**
 * A dialog for asking the user what Board/Syllabus they
 *  want to set as default.
 */
class BoardChoiceDialog(context: Context): SettingsChoiceDialog(context, R.string.choose_your_syllabus, R.string.you_can_change_this_setting_later) {

    override fun show() {
        super.show()
        prefs.setHasSeenBoardChooser(true)
    }

    override fun configureOptionsListView(optionsListView: ListView) {
        optionsListView.choiceMode = ListView.CHOICE_MODE_SINGLE

        val firestoreRoot = FirebaseFirestore.getInstance()
        val firestoreLocalized = firestoreRoot.collection(context.getString(R.string.localized)).document(prefs.getCurrentLanguageCode())
        val boardQuery = firestoreLocalized.collection(context.getString(R.string.boards))

        val emptyDataObserver = object: FirebaseDataObserver {}

        var checkedIndex = 0
        val boardChoiceAdapter = object: FirestoreObservableListAdapter<Board>(boardQuery, Board::class.java, R.layout.dialog_option_singlechoice, emptyDataObserver, context) {
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
            val boardKey = boardChoiceAdapter.getItemKey(position)
            val boardName = boardChoiceAdapter.getItem(position).name
            optionsListView.setItemChecked(position, true)
            prefs.setBoard(boardName, boardKey)
            boardChoiceAdapter.stopListening()
            dismiss()
        }

        boardChoiceAdapter.startListening()
        optionsListView.adapter = boardChoiceAdapter
    }

}