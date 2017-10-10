package org.jnanaprabodhini.happyteacher.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_subtopic_submissions_list.*
import org.jnanaprabodhini.happyteacher.R
import org.jnanaprabodhini.happyteacher.activity.parent.HappyTeacherActivity
import org.jnanaprabodhini.happyteacher.adapter.CardListContentHeaderRecyclerAdapter
import org.jnanaprabodhini.happyteacher.adapter.LessonHeaderRecyclerAdapter
import org.jnanaprabodhini.happyteacher.adapter.helper.FirebaseDataObserver
import org.jnanaprabodhini.happyteacher.extension.setVisibilityGone
import org.jnanaprabodhini.happyteacher.extension.setVisible
import org.jnanaprabodhini.happyteacher.extension.showToast

class SubtopicSubmissionsListActivity : HappyTeacherActivity(), FirebaseDataObserver {

    companion object IntentExtraHelper {
        fun launchActivity(from: Activity, topicName: String, subtopicId: String, topicId: String) {
            val subtopicSubmissionsIntent = Intent(from, SubtopicSubmissionsListActivity::class.java)

            subtopicSubmissionsIntent.apply {
                putExtra(SubtopicSubmissionsListActivity.TOPIC_NAME, topicName)
                putExtra(SubtopicSubmissionsListActivity.SUBTOPIC_KEY, subtopicId)
                putExtra(SubtopicSubmissionsListActivity.TOPIC_KEY, topicId)
            }

            from.startActivity(subtopicSubmissionsIntent)
        }

        val TOPIC_KEY: String = "TOPIC_KEY"
        fun Intent.hasTopicKey(): Boolean = hasExtra(TOPIC_KEY)
        fun Intent.getTopicKey(): String = getStringExtra(TOPIC_KEY)

        val SUBTOPIC_KEY: String = "SUBTOPIC_KEY"
        fun Intent.hasSubtopicKey(): Boolean = hasExtra(SUBTOPIC_KEY)
        fun Intent.getSubtopicKey(): String = getStringExtra(SUBTOPIC_KEY)

        val TOPIC_NAME: String = "TOPIC_NAME"
        fun Intent.hasTopicName(): Boolean = hasExtra(TOPIC_NAME)
        fun Intent.getTopicName(): String = getStringExtra(TOPIC_NAME)

        fun Intent.hasAllExtras(): Boolean = hasTopicKey() && hasSubtopicKey() && hasTopicName()
    }

    val topicKey: String by lazy {
        intent.getTopicKey()
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

    fun initializeRecyclerViewForSubtopic() {

        val submissionHeadersQuery = databaseReference.child(getString(R.string.subtopic_lesson_headers))
                                                        .child(topicKey)
                                                        .child(subtopicKey)

        submissionRecyclerView.layoutManager = LinearLayoutManager(this)
        submissionRecyclerView.adapter = LessonHeaderRecyclerAdapter(topicName, submissionHeadersQuery, this, this)
    }

    override fun onRequestNewData() {
        submissionRecyclerView.setVisibilityGone()
        emptyTextView.setVisibilityGone()

        progressBar.setVisible()
    }

    override fun onDataLoaded() {
        submissionRecyclerView.setVisible()

        emptyTextView.setVisibilityGone()
        progressBar.setVisibilityGone()
    }

    override fun onDataEmpty() {
        submissionRecyclerView.setVisibilityGone()
        progressBar.setVisibilityGone()

        emptyTextView.setVisible()
    }
}
