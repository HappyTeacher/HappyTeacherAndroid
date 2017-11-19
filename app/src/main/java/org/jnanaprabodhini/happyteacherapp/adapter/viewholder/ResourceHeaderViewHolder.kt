package org.jnanaprabodhini.happyteacherapp.adapter.viewholder

import android.app.Activity
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.TextView
import com.google.firebase.firestore.DocumentReference
import kotlinx.android.synthetic.main.list_item_resource_header_card.view.*
import org.jnanaprabodhini.happyteacherapp.R
import org.jnanaprabodhini.happyteacherapp.extension.setDrawableLeft
import org.jnanaprabodhini.happyteacherapp.model.ResourceHeader
import java.text.DateFormat

/**
 * Created by grahamearley on 9/12/17.
 */
abstract class ResourceHeaderViewHolder(itemView: View): BaseResourceHeaderViewHolder(itemView) {
    val titleTextView: TextView = itemView.titleTextView
    val authorNameTextView: TextView = itemView.authorNameTextView
    val institutionTextView: TextView = itemView.institutionTextView
    val locationTextView: TextView = itemView.locationTextView
    val dateEditedTextView: TextView = itemView.dateEditedTextView
    val submissionCountTextView: TextView = itemView.submissionCountTextView
    override val resourceColorBar: View = itemView.resourceColorBar

    open fun populateView(resourceHeaderModel: ResourceHeader?, contentDocumentRef: DocumentReference, activity: Activity, dateFormat: DateFormat) {
        titleTextView.text = resourceHeaderModel?.name
        authorNameTextView.text = resourceHeaderModel?.authorName
        institutionTextView.text = resourceHeaderModel?.authorInstitution
        locationTextView.text = resourceHeaderModel?.authorLocation

        authorNameTextView.setDrawableLeft(R.drawable.ic_person_accent)
        institutionTextView.setDrawableLeft(R.drawable.ic_school_accent)
        locationTextView.setDrawableLeft(R.drawable.ic_location_accent)

        resourceHeaderModel?.dateUpdated?.let {
            dateEditedTextView.text = dateFormat.format(it)
            dateEditedTextView.setDrawableLeft(R.drawable.ic_clock_light_gray)
        }

        itemView.setOnClickListener {
            launchContentViewerActivity(activity, contentDocumentRef, resourceHeaderModel)
        }
    }

    abstract fun launchContentViewerActivity(activity: Activity, contentDocumentRef: DocumentReference, resourceHeaderModel: ResourceHeader?)
}

