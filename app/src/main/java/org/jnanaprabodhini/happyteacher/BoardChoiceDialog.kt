package org.jnanaprabodhini.happyteacher

import android.app.Dialog
import android.content.Context
import android.util.Log
import android.view.View
import android.widget.CheckedTextView
import android.widget.TextView
import com.firebase.ui.database.FirebaseListAdapter
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.dialog_board_choice.*
import org.jnanaprabodhini.happyteacher.model.Board

/**
 * A dialog for asking the user what Board/Syllabus they
 *  want to set as default.
 */
class BoardChoiceDialog(context: Context): Dialog(context) {

    init {
        setContentView(R.layout.dialog_board_choice)

        val databaseInstance = FirebaseDatabase.getInstance()
        val boardQuery = databaseInstance.getReference(context.getString(R.string.boards))

        val boardChoiceAdapter = object: FirebaseListAdapter<Board>(context, Board::class.java, R.layout.select_dialog_singlechoice_material, boardQuery) {
            override fun populateView(v: View?, model: Board?, position: Int) {
                (v as CheckedTextView).text = model?.names?.get("en") // todo: remove hardcoded language string

                v.setOnClickListener {
                    prefs.setBoardKey(getRef(position).key)
                    dismiss()
                }
            }
        }

        boardOptionsListView.adapter = boardChoiceAdapter
    }
}