package org.jnanaprabodhini.happyteacherapp.activity

import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.widget.ProgressBar
import android.widget.Spinner
import android.widget.TextView
import com.google.firebase.firestore.FirebaseFirestoreException
import kotlinx.android.synthetic.main.activity_submissions_for_review.*
import kotlinx.android.synthetic.main.stacked_subject_spinners.*
import org.jnanaprabodhini.happyteacherapp.R
import org.jnanaprabodhini.happyteacherapp.activity.base.HappyTeacherActivity
import org.jnanaprabodhini.happyteacherapp.adapter.helper.FirebaseDataObserver
import org.jnanaprabodhini.happyteacherapp.extension.setVisibilityGone
import org.jnanaprabodhini.happyteacherapp.extension.setVisible
import org.jnanaprabodhini.happyteacherapp.view.SubjectSpinnerRecyclerView
import org.jnanaprabodhini.happyteacherapp.view.manager.SubjectSpinnerManager
import org.jnanaprabodhini.happyteacherapp.view.manager.SubmissionsTopicListManager

class SubmissionsForReviewActivity : HappyTeacherActivity(),
        FirebaseDataObserver, SubjectSpinnerRecyclerView {

    companion object IntentExtraHelper {
        fun launch(from: HappyTeacherActivity) {
            val intent = Intent(from, SubmissionsForReviewActivity::class.java)
            from.startActivity(intent)
        }
    }

    private val subjectSpinnerManager = SubjectSpinnerManager(view = this, activity = this)

    override val parentSpinner: Spinner by lazy { parentSubjectSpinner }
    override val childSpinner: Spinner by lazy { childSubjectSpinner }
    override val statusText: TextView by lazy { statusTextView }
    override val progressBar: ProgressBar by lazy { topicsProgressBar }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_submissions_for_review)
        topicsRecyclerView.layoutManager = LinearLayoutManager(this)

        subjectSpinnerManager.restoreSpinnerState(savedInstanceState)

        initializeUi()
    }

    private fun initializeUi() {
        val topicListManager = SubmissionsTopicListManager(topicsRecyclerView, this, this)
        subjectSpinnerManager.initializeWithTopicsListManager(topicListManager)
    }

    override fun onSaveInstanceState(savedInstanceState: Bundle) {
        subjectSpinnerManager.saveSpinnerState(savedInstanceState)
        super.onSaveInstanceState(savedInstanceState)
    }

    override fun onRequestNewData() {
        topicsRecyclerView.setVisibilityGone()
        topicsProgressBar.setVisible()
        statusText.setVisibilityGone()
    }

    override fun onDataLoaded() {
        topicsProgressBar.setVisibilityGone()
    }

    override fun onDataEmpty() {
        topicsRecyclerView.setVisibilityGone()
        statusText.setVisible()
        statusText.setText(R.string.there_are_no_submissions_for_this_subject_yet)
    }

    override fun onDataNonEmpty() {
        topicsRecyclerView.setVisible()
        statusText.setVisibilityGone()

        // Animate layout changes
        topicsRecyclerView.scheduleLayoutAnimation()
        topicsRecyclerView.invalidate()
    }

    override fun onError(e: FirebaseFirestoreException?) {
        topicsRecyclerView.setVisibilityGone()
        topicsProgressBar.setVisibilityGone()
        statusText.setVisible()
        statusText.setText(R.string.there_was_an_error_loading_submissions_for_this_subject)
    }
}
