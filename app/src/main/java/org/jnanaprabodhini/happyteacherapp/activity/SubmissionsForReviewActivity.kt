package org.jnanaprabodhini.happyteacherapp.activity

import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import com.google.firebase.firestore.FirebaseFirestoreException
import kotlinx.android.synthetic.main.activity_submissions_for_review.*
import kotlinx.android.synthetic.main.stacked_subject_spinners.*
import org.jnanaprabodhini.happyteacherapp.R
import org.jnanaprabodhini.happyteacherapp.activity.base.HappyTeacherActivity
import org.jnanaprabodhini.happyteacherapp.adapter.helper.FirebaseDataObserver
import org.jnanaprabodhini.happyteacherapp.extension.setVisibilityGone
import org.jnanaprabodhini.happyteacherapp.extension.setVisible
import org.jnanaprabodhini.happyteacherapp.view.manager.SubjectSpinnerManager
import org.jnanaprabodhini.happyteacherapp.view.manager.SubmissionsTopicListManager

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
        val topicListManager = SubmissionsTopicListManager(topicsRecyclerView, this, this)
        subjectSpinnerManager.initializeWithTopicsListManager(parentSubjectSpinner, childSubjectSpinner, topicsProgressBar, topicListManager)
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
