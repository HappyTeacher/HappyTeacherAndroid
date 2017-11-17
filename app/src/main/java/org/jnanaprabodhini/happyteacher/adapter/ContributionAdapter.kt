package org.jnanaprabodhini.happyteacher.adapter

import android.app.Activity
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.DocumentReference
import org.jnanaprabodhini.happyteacher.R
import org.jnanaprabodhini.happyteacher.adapter.firestore.FirestoreObserverRecyclerAdapter
import org.jnanaprabodhini.happyteacher.adapter.helper.FirebaseDataObserver
import org.jnanaprabodhini.happyteacher.adapter.viewholder.ContributionHeaderViewHolder
import org.jnanaprabodhini.happyteacher.extension.setDrawableLeft
import org.jnanaprabodhini.happyteacher.extension.setVisibilityGone
import org.jnanaprabodhini.happyteacher.model.ResourceHeader
import java.util.*

/**
 * A base class for showing list items in the Contribute section.
 *  Each item represents a contribution the user has made. Different
 *  implementations will contain logic for showing drafts, submissions
 *  awaiting feedback, submissions with changes requested, or published
 *  submissions.
 *
 */
abstract class ContributionAdapter(adapterOptions: FirestoreRecyclerOptions<ResourceHeader>, dataObserver: FirebaseDataObserver, val activity: Activity):
        FirestoreObserverRecyclerAdapter<ResourceHeader, ContributionHeaderViewHolder>(adapterOptions, dataObserver) {

    private val dateFormat by lazy {
        DateFormat.getDateFormat(activity)
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ContributionHeaderViewHolder {
        val view = LayoutInflater.from(parent?.context)
                .inflate(R.layout.list_item_contribution_header_card, parent, false)
        return ContributionHeaderViewHolder(view)
    }

    override fun onBindViewHolder(holder: ContributionHeaderViewHolder?, position: Int, model: ResourceHeader?) {
        holder?.apply {
            titleTextView.text = model?.name
            subjectTextView.text = model?.subjectName

            model?.dateEdited?.let {
                dateEditedTextView.text = dateFormat.format(Date(it))
                dateEditedTextView.setDrawableLeft(R.drawable.ic_clock_light_gray)
            } ?: dateEditedTextView.setVisibilityGone()

            setStatusView(holder, model)

            val contributionDocumentRef = snapshots.getSnapshot(position).reference
            setCardButtons(holder, contributionDocumentRef, model)

            val viewLessonOnClickListener = getViewResourceOnClickListener(model, contributionDocumentRef)
            itemView.setOnClickListener(viewLessonOnClickListener)
        }
    }

    abstract fun getViewResourceOnClickListener(model: ResourceHeader?, contributionDocumentRef: DocumentReference): View.OnClickListener

    abstract fun setCardButtons(holder: ContributionHeaderViewHolder, contributionDocumentRef: DocumentReference, model: ResourceHeader?)

    private fun setStatusView(holder: ContributionHeaderViewHolder, model: ResourceHeader?) {
        // todo
    }
}