package org.jnanaprabodhini.happyteacher.adapter.firestore

import android.content.Context
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.ViewGroup
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import org.jnanaprabodhini.happyteacher.R
import org.jnanaprabodhini.happyteacher.adapter.helper.FirebaseDataObserver
import org.jnanaprabodhini.happyteacher.adapter.viewholder.DraftHeaderViewHolder
import org.jnanaprabodhini.happyteacher.extension.setVisibilityGone
import org.jnanaprabodhini.happyteacher.extension.showSnackbar
import org.jnanaprabodhini.happyteacher.model.CardListContentHeader
import java.util.*

/**
 * Created by grahamearley on 11/5/17.
 */
class DraftHeaderRecyclerAdapter(adapterOptions: FirestoreRecyclerOptions<CardListContentHeader>, dataObserver: FirebaseDataObserver, val context: Context):
        FirestoreObserverRecyclerAdapter<CardListContentHeader, DraftHeaderViewHolder>(adapterOptions, dataObserver) {

    private val dateFormat by lazy {
        DateFormat.getDateFormat(context)
    }

    override fun onBindViewHolder(holder: DraftHeaderViewHolder?, position: Int, model: CardListContentHeader?) {
        holder?.apply {
            titleTextView.text = model?.name
            subjectTextView.text = model?.subjectName

            model?.dateEdited?.let { dateEditedTextView.text = dateFormat.format(Date(it)) }
                    ?: dateEditedTextView.setVisibilityGone()

            deleteButton.setOnClickListener {
                val reference = snapshots.getSnapshot(position).reference
                reference.delete().addOnSuccessListener {
                    itemView.showSnackbar("Draft deleted.")
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): DraftHeaderViewHolder {
        val view =  LayoutInflater.from(parent?.context)
                .inflate(R.layout.list_item_draft_header_card, parent, false)
        return DraftHeaderViewHolder(view)
    }
}

