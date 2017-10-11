package org.jnanaprabodhini.happyteacher.adapter.viewholder

import android.app.Activity
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.TextView
import kotlinx.android.synthetic.main.list_item_content_header_card.view.*
import org.jnanaprabodhini.happyteacher.R
import org.jnanaprabodhini.happyteacher.extension.setDrawableLeft
import org.jnanaprabodhini.happyteacher.model.CardListContentHeader
import java.text.DateFormat
import java.util.*

/**
 * Created by grahamearley on 9/12/17.
 */
abstract class CardListHeaderViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
    val titleTextView: TextView = itemView.titleTextView
    val authorNameTextView: TextView = itemView.authorNameTextView
    val institutionTextView: TextView = itemView.institutionTextView
    val locationTextView: TextView = itemView.locationTextView
    val dateEditedTextView: TextView = itemView.dateEditedTextView
    val submissionCountTextView: TextView = itemView.submissionCountTextView

    open fun populateView(cardListContentHeaderModel: CardListContentHeader?, topicName: String, activity: Activity, dateFormat: DateFormat) {
        titleTextView.text = cardListContentHeaderModel?.name
        authorNameTextView.text = cardListContentHeaderModel?.authorName
        institutionTextView.text = cardListContentHeaderModel?.authorInstitution
        locationTextView.text = cardListContentHeaderModel?.authorLocation

        authorNameTextView.setDrawableLeft(R.drawable.ic_person_accent)
        institutionTextView.setDrawableLeft(R.drawable.ic_school_accent)
        locationTextView.setDrawableLeft(R.drawable.ic_location_accent)

        cardListContentHeaderModel?.let {
            dateEditedTextView.text = dateFormat.format(Date(cardListContentHeaderModel.dateEdited))
            dateEditedTextView.setDrawableLeft(R.drawable.ic_clock_light_gray)
        }

        itemView.setOnClickListener {
            val contentId = cardListContentHeaderModel?.contentKey ?: ""
            val subtopicId = cardListContentHeaderModel?.subtopic ?: ""
            val subjectName = cardListContentHeaderModel?.subjectName ?: ""
            val topicId = cardListContentHeaderModel?.topic ?: ""
            val subtopicSubmissionCount = cardListContentHeaderModel?.subtopicSubmissionCount ?: 0
            val subtopicName = cardListContentHeaderModel?.name ?: ""

            launchContentViewerActivity(activity, contentId, subtopicId, subjectName, topicName, topicId, subtopicName, subtopicSubmissionCount)
        }
    }

    abstract fun launchContentViewerActivity(activity: Activity, contentId: String, subtopicId: String, subjectName: String, topicName: String, topicId: String, subtopicName: String, subtopicSubmissionCount: Int)
}

