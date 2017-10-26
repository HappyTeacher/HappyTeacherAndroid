package org.jnanaprabodhini.happyteacher.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.FirebaseFirestoreException
import kotlinx.android.synthetic.main.activity_subtopic_submissions_list.*
import org.jnanaprabodhini.happyteacher.R
import org.jnanaprabodhini.happyteacher.activity.base.HappyTeacherActivity
import org.jnanaprabodhini.happyteacher.adapter.firestore.LessonHeaderRecyclerAdapter
import org.jnanaprabodhini.happyteacher.adapter.helper.FirebaseDataObserver
import org.jnanaprabodhini.happyteacher.extension.setVisibilityGone
import org.jnanaprabodhini.happyteacher.extension.setVisible
import org.jnanaprabodhini.happyteacher.extension.showToast
import org.jnanaprabodhini.happyteacher.model.CardListContentHeader

class SubtopicSubmissionsListActivity : HappyTeacherActivity(), FirebaseDataObserver {

    companion object IntentExtraHelper {
        fun launchActivity(from: Activity, topicName: String, subtopicId: String) {
            val subtopicSubmissionsIntent = Intent(from, SubtopicSubmissionsListActivity::class.java)

            subtopicSubmissionsIntent.apply {
                putExtra(SubtopicSubmissionsListActivity.TOPIC_NAME, topicName)
                putExtra(SubtopicSubmissionsListActivity.SUBTOPIC_KEY, subtopicId)
            }

            from.startActivity(subtopicSubmissionsIntent)
        }

        const val SUBTOPIC_KEY: String = "SUBTOPIC_KEY"
        fun Intent.hasSubtopicKey(): Boolean = hasExtra(SUBTOPIC_KEY)
        fun Intent.getSubtopicKey(): String = getStringExtra(SUBTOPIC_KEY)

        const val TOPIC_NAME: String = "TOPIC_NAME"
        fun Intent.hasTopicName(): Boolean = hasExtra(TOPIC_NAME)
        fun Intent.getTopicName(): String = getStringExtra(TOPIC_NAME)

        fun Intent.hasAllExtras(): Boolean = hasSubtopicKey() && hasTopicName()
    }

    val subtopicKey: String by lazy {
        intent.getSubtopicKey()
    }

    val topicName: String by lazy {
        intent.getTopicName()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_subtopic_submissions_list)

        if (!intent.hasAllExtras()) {
            // todo: log error
            showToast(R.string.error_loading_other_lessons)
            finish()
        }

        initializeRecyclerViewForSubtopic()
    }

    private fun initializeRecyclerViewForSubtopic() {

        val submissionHeadersQuery = firestoreLocalized.collection(getString(R.string.lessons))
                .whereEqualTo(getString(R.string.subtopic), subtopicKey)
                .orderBy(getString(R.string.is_featured))

        val adapterOptions = FirestoreRecyclerOptions.Builder<CardListContentHeader>()
                .setQuery(submissionHeadersQuery, CardListContentHeader::class.java).build()

        val shouldShowSubmissionsCount = false
        val adapter = LessonHeaderRecyclerAdapter(topicName, shouldShowSubmissionsCount, adapterOptions, this, this)
        adapter.startListening()

        submissionRecyclerView.layoutManager = LinearLayoutManager(this)
        submissionRecyclerView.adapter = adapter
    }

    override fun onRequestNewData() {
        submissionRecyclerView.setVisibilityGone()
        statusTextView.setVisibilityGone()

        progressBar.setVisible()
    }

    override fun onDataLoaded() {
        submissionRecyclerView.setVisible()

        statusTextView.setVisibilityGone()
        progressBar.setVisibilityGone()
    }

    override fun onDataEmpty() {
        submissionRecyclerView.setVisibilityGone()
        progressBar.setVisibilityGone()

        statusTextView.setVisible()
        statusTextView.setText(R.string.there_are_no_other_lessons)
    }

    override fun onError(e: FirebaseFirestoreException?) {
        submissionRecyclerView.setVisibilityGone()
        progressBar.setVisibilityGone()

        statusTextView.setVisible()
        statusTextView.setText(R.string.there_was_an_error_loading_the_other_lessons)
    }
}
