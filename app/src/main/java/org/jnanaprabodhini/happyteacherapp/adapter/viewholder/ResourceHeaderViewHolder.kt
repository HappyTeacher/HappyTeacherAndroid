package org.jnanaprabodhini.happyteacherapp.adapter.viewholder

import android.app.Activity
import android.view.View
import android.widget.TextView
import com.google.firebase.firestore.DocumentReference
import kotlinx.android.synthetic.main.list_item_resource_header_card.view.*
import org.jnanaprabodhini.happyteacherapp.R
import org.jnanaprabodhini.happyteacherapp.activity.SubtopicLessonListActivity
import org.jnanaprabodhini.happyteacherapp.extension.setDrawableLeft
import org.jnanaprabodhini.happyteacherapp.extension.setVisibilityGone
import org.jnanaprabodhini.happyteacherapp.extension.setVisible
import org.jnanaprabodhini.happyteacherapp.model.ResourceHeader
import java.text.DateFormat

/**
 * Created by grahamearley on 9/12/17.
 */
class ResourceHeaderViewHolder(itemView: View): BaseResourceHeaderViewHolder(itemView) {
    val titleTextView: TextView = itemView.titleTextView
    val authorNameTextView: TextView = itemView.authorNameTextView
    val institutionTextView: TextView = itemView.institutionTextView
    val locationTextView: TextView = itemView.locationTextView
    val dateEditedTextView: TextView = itemView.dateEditedTextView
    val submissionCountTextView: TextView = itemView.submissionCountTextView
    override val resourceColorBar: View = itemView.resourceColorBar

    fun populateView(resourceHeaderModel: ResourceHeader?, contentDocumentRef: DocumentReference, activity: Activity, dateFormat: DateFormat, showSubmissionCount: Boolean) {
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

        if (showSubmissionCount) {
            showSubmissionCount(resourceHeaderModel, activity)
        }

        setColorBarForResourceType(resourceHeaderModel?.resourceType)
    }

    private fun showSubmissionCount(resourceHeaderModel: ResourceHeader?, activity: Activity) {
        resourceHeaderModel?.let {
            if (resourceHeaderModel.subtopicSubmissionCount > 1) {
                submissionCountTextView.setVisible()
                submissionCountTextView.text = activity.getString(R.string.plus_number, resourceHeaderModel.subtopicSubmissionCount - 1) // subtract one to exclude the featured contentKey
                submissionCountTextView.setOnClickListener {
                    SubtopicLessonListActivity.launch(activity, resourceHeaderModel.subtopic)
                }
            } else {
                submissionCountTextView.setVisibilityGone()
            }
        }
    }
}

