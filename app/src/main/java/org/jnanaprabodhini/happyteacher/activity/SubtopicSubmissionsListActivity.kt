package org.jnanaprabodhini.happyteacher.activity

import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.text.format.DateFormat
import android.view.MenuItem
import kotlinx.android.synthetic.main.activity_subtopic_submissions_list.*

import org.jnanaprabodhini.happyteacher.R
import org.jnanaprabodhini.happyteacher.activity.parent.HappyTeacherActivity
import org.jnanaprabodhini.happyteacher.adapter.FirebaseObserverRecyclerAdapter
import org.jnanaprabodhini.happyteacher.adapter.SubtopicLessonHeaderRecyclerAdapter
import org.jnanaprabodhini.happyteacher.adapter.helper.FirebaseDataObserver
import org.jnanaprabodhini.happyteacher.adapter.viewholder.SubtopicHeaderViewHolder
import org.jnanaprabodhini.happyteacher.extension.setDrawableLeft
import org.jnanaprabodhini.happyteacher.extension.setVisibilityGone
import org.jnanaprabodhini.happyteacher.extension.setVisible
import org.jnanaprabodhini.happyteacher.extension.showToast
import org.jnanaprabodhini.happyteacher.model.SubtopicLessonHeader
import java.util.*

class SubtopicSubmissionsListActivity : HappyTeacherActivity(), FirebaseDataObserver {

    companion object IntentExtraHelper {
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_subtopic_submissions_list)

        if (intent.hasAllExtras()) {
            initializeRecyclerViewForSubtopic(intent.getTopicKey(), intent.getSubtopicKey(), intent.getTopicName())
        } else {
            // todo: log error
            showToast(R.string.error_loading_other_lessons)
            finish()
        }
    }

    fun initializeRecyclerViewForSubtopic(topicKey: String, subtopicKey: String, topicName: String) {

        val submissionHeadersQuery = databaseReference.child(getString(R.string.subtopic_lesson_headers))
                                                        .child(topicKey)
                                                        .child(subtopicKey)

        submissionRecyclerView.layoutManager = LinearLayoutManager(this)
        submissionRecyclerView.adapter = SubtopicLessonHeaderRecyclerAdapter(topicName, submissionHeadersQuery, this, this)
    }

    override fun onRequestNewData() {}

    override fun onDataLoaded() {}

    override fun onDataEmpty() {}

    override fun onDataNonEmpty() {}
}
