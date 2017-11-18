package org.jnanaprabodhini.happyteacher.adapter.contribute

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
import org.jnanaprabodhini.happyteacher.util.ResourceStatus

/**
 * A base class for showing list items in the Contribute section.
 *  Each item represents a contribution the user has made.
 *
 *  Different implementations will contain logic for card buttons and
 *  onClicks.
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
            holder.setTextColorForResourceType(model?.resourceType)

            model?.dateUpdated?.let {
                dateEditedTextView.text = dateFormat.format(it)
                dateEditedTextView.setDrawableLeft(R.drawable.ic_clock_light_gray)
            } ?: dateEditedTextView.setVisibilityGone()

            setStatusView(holder, model?.status)

            val contributionDocumentRef = snapshots.getSnapshot(position).reference
            setCardButtons(holder, contributionDocumentRef, model)

            val viewLessonOnClickListener = getViewResourceOnClickListener(model, contributionDocumentRef)
            itemView.setOnClickListener(viewLessonOnClickListener)
        }
    }

    abstract fun getViewResourceOnClickListener(model: ResourceHeader?, contributionDocumentRef: DocumentReference): View.OnClickListener

    abstract fun setCardButtons(holder: ContributionHeaderViewHolder, contributionDocumentRef: DocumentReference, model: ResourceHeader?)

    private fun setStatusView(holder: ContributionHeaderViewHolder, status: String?) {
        when (status) {
            ResourceStatus.DRAFT -> holder.hideStatusView()
            ResourceStatus.AWAITING_REVIEW -> holder.showAwaitingReviewStatus(activity)
            ResourceStatus.CHANGES_REQUESTED -> holder.showChangesRequestedStatus(activity)
            ResourceStatus.PUBLISHED -> holder.showPublishedStatus(activity)
            else -> holder.hideStatusView()
        }
    }
}