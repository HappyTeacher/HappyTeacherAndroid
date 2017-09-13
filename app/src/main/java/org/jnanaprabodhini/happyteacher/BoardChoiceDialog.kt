package org.jnanaprabodhini.happyteacher

import android.app.Dialog
import android.content.Context
import android.view.View
import android.widget.CheckedTextView
import android.widget.TextView
import com.firebase.ui.database.FirebaseListAdapter
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.dialog_board_choice.*
import org.jnanaprabodhini.happyteacher.model.Board

/**
 * Created by grahamearley on 9/13/17.
 */
class BoardChoiceDialog(context: Context): Dialog(context) {

    init {
        setContentView(R.layout.dialog_board_choice)

        val databaseInstance = FirebaseDatabase.getInstance()
        val boardQuery = databaseInstance.getReference(context.getString(R.string.boards))

        val boardChoiceAdapter = object: FirebaseListAdapter<Board>(context, Board::class.java, R.layout.select_dialog_singlechoice_material, boardQuery) {
            override fun populateView(v: View?, model: Board?, position: Int) {
                (v as CheckedTextView).text = model?.names?.get("en")

                val boardKey = getRef(position).key

                // If this item has the same key as the current/default key, mark it as selected
                v.isSelected = boardKey == prefs.getBoardKey()

                v.setOnClickListener {
                    prefs.setBoardKey(boardKey)
                    v.isChecked = true
                    dismiss()
                }
            }
        }

        boardOptionsListView.adapter = boardChoiceAdapter
    }
}