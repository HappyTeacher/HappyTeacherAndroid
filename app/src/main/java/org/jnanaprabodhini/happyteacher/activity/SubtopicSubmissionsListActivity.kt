package org.jnanaprabodhini.happyteacher.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import com.firebase.ui.firestore.FirestoreRecyclerOptions
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
        fun launchActivity(from: Activity, topicName: String, subtopicId: String, topicId: String) {
            val subtopicSubmissionsIntent = Intent(from, SubtopicSubmissionsListActivity::class.java)

            subtopicSubmissionsIntent.apply {
                putExtra(SubtopicSubmissionsListActivity.TOPIC_NAME, topicName)
                putExtra(SubtopicSubmissionsListActivity.SUBTOPIC_KEY, subtopicId)
                putExtra(SubtopicSubmissionsListActivity.TOPIC_KEY, topicId)
            }

            from.startActivity(subtopicSubmissionsIntent)
        }

        const val TOPIC_KEY: String = "TOPIC_KEY"
        fun Intent.hasTopicKey(): Boolean = hasExtra(TOPIC_KEY)
        fun Intent.getTopicKey(): String = getStringExtra(TOPIC_KEY)

        const val SUBTOPIC_KEY: String = "SUBTOPIC_KEY"
        fun Intent.hasSubtopicKey(): Boolean = hasExtra(SUBTOPIC_KEY)
        fun Intent.getSubtopicKey(): String = getStringExtra(SUBTOPIC_KEY)

        const val TOPIC_NAME: String = "TOPIC_NAME"
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
