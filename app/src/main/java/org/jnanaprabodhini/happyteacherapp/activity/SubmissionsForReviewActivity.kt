package org.jnanaprabodhini.happyteacherapp.activity

import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.Query
import kotlinx.android.synthetic.main.activity_submissions_for_review.*
import kotlinx.android.synthetic.main.stacked_subject_spinners.*
import org.jnanaprabodhini.happyteacherapp.R
import org.jnanaprabodhini.happyteacherapp.activity.base.HappyTeacherActivity
import org.jnanaprabodhini.happyteacherapp.adapter.firestore.TopicLessonsRecyclerAdapter
import org.jnanaprabodhini.happyteacherapp.adapter.helper.FirebaseDataObserver
import org.jnanaprabodhini.happyteacherapp.extension.setVisibilityGone
import org.jnanaprabodhini.happyteacherapp.extension.setVisible
import org.jnanaprabodhini.happyteacherapp.model.ResourceHeader
import org.jnanaprabodhini.happyteacherapp.model.Topic
import org.jnanaprabodhini.happyteacherapp.util.FirestoreKeys
import org.jnanaprabodhini.happyteacherapp.util.ResourceStatus
import org.jnanaprabodhini.happyteacherapp.view.SubjectSpinnerManager

class SubmissionsForReviewActivity : HappyTeacherActivity(), FirebaseDataObserver {
    // TODO: keep spinner position across config changes

    companion object IntentExtraHelper {
        fun launch(from: HappyTeacherActivity) {
            val intent = Intent(from, SubmissionsForReviewActivity::class.java)
            from.startActivity(intent)
        }
    }

    private val subjectSpinnerManager = SubjectSpinnerManager(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_submissions_for_review)
        topicsRecyclerView.layoutManager = LinearLayoutManager(this)

        initializeUi()
    }

    private fun initializeUi() {
        subjectSpinnerManager.initializeSpinners(parentSubjectSpinner, childSubjectSpinner, topicsProgressBar,
                onSpinnerSelectionsComplete = {subjectKey -> updateListOfTopicsForSubject(subjectKey)})
    }

    private fun updateListOfTopicsForSubject(subjectKey: String) {
        val topicQuery = firestoreLocalized.collection(getString(R.string.topics))
                .whereEqualTo(getString(R.string.subject), subjectKey)

        val topicAdapterOptions = FirestoreRecyclerOptions.Builder<Topic>()
                .setQuery(topicQuery, Topic::class.java).build()

        val topicAdapter = object: TopicLessonsRecyclerAdapter(topicAdapterOptions, showSubmissionCount = false,
                topicsDataObserver = this, activity = this) {
            override fun getSubtopicAdapterOptions(topicId: String): FirestoreRecyclerOptions<ResourceHeader> {
                val query: Query = firestoreLocalized.collection(getString(R.string.resources))
                        .whereEqualTo(getString(R.string.topic), topicId)
                        .whereEqualTo(FirestoreKeys.STATUS, ResourceStatus.AWAITING_REVIEW)

                return FirestoreRecyclerOptions.Builder<ResourceHeader>().setQuery(query, ResourceHeader::class.java).build()
            }
        }

        topicAdapter.startListening()

        topicsRecyclerView.adapter = topicAdapter
    }

    override fun onRequestNewData() {
        topicsRecyclerView.setVisibilityGone()
        topicsProgressBar.setVisible()
        statusTextView.setVisibilityGone()
    }

    override fun onDataLoaded() {
        topicsProgressBar.setVisibilityGone()
    }

    override fun onDataEmpty() {
        topicsRecyclerView.setVisibilityGone()
        statusTextView.setVisible()
        statusTextView.setText(R.string.there_are_no_topics_for_this_subject_yet)
    }

    override fun onDataNonEmpty() {
        topicsRecyclerView.setVisible()
        statusTextView.setVisibilityGone()

        // Animate layout changes
        topicsRecyclerView.scheduleLayoutAnimation()
        topicsRecyclerView.invalidate()
    }

    override fun onError(e: FirebaseFirestoreException?) {
        topicsRecyclerView.setVisibilityGone()
        topicsProgressBar.setVisibilityGone()
        statusTextView.setVisible()
        statusTextView.setText(R.string.there_was_an_error_loading_topics_for_this_subject)
    }
}
