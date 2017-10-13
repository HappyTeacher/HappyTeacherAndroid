package org.jnanaprabodhini.happyteacher.dialog

import android.content.Context
import android.view.View
import android.widget.CheckedTextView
import android.widget.ListView
import com.firebase.ui.database.FirebaseListAdapter
import com.firebase.ui.database.FirebaseListOptions
import com.google.firebase.database.FirebaseDatabase
import org.jnanaprabodhini.happyteacher.R
import org.jnanaprabodhini.happyteacher.extension.getBaseReferenceForCurrentLanguage
import org.jnanaprabodhini.happyteacher.model.Board
import org.jnanaprabodhini.happyteacher.prefs

/**
 * A dialog for asking the user what Board/Syllabus they
 *  want to set as default.
 */
class BoardChoiceDialog(context: Context): SettingsChoiceDialog(context, R.string.choose_your_syllabus, R.string.you_can_change_this_in_your_settings_later) {

    override fun configureOptionsListView(optionsListView: ListView) {
        optionsListView.choiceMode = ListView.CHOICE_MODE_SINGLE

        val databaseReference = FirebaseDatabase.getInstance().getBaseReferenceForCurrentLanguage()
        val boardQuery = databaseReference.child(context.getString(R.string.boards))

        val adapterOptions = FirebaseListOptions.Builder<Board>()
                .setQuery(boardQuery, Board::class.java)
                .setLayout(R.layout.dialog_option_singlechoice).build()

        val boardChoiceAdapter = object: FirebaseListAdapter<Board>(adapterOptions) {
            override fun populateView(v: View?, model: Board?, position: Int) {
                (v as CheckedTextView).text = model?.name
            }
        }

        optionsListView.setOnItemClickListener { _, _, position, _ ->
            val selectedKey = boardChoiceAdapter.getRef(position).key
            optionsListView.setItemChecked(position, true)
            prefs.setBoardKey(selectedKey)
            boardChoiceAdapter.stopListening()
            dismiss()
        }

        boardChoiceAdapter.startListening()
        optionsListView.adapter = boardChoiceAdapter
    }

}