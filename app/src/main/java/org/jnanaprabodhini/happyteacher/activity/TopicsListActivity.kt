package org.jnanaprabodhini.happyteacher.activity

import android.content.Intent
import android.os.Bundle
import android.support.annotation.IntegerRes
import android.support.v7.widget.LinearLayoutManager
import android.text.format.DateFormat
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.TextView
import com.firebase.ui.database.FirebaseIndexRecyclerAdapter
import com.firebase.ui.database.FirebaseListAdapter
import com.firebase.ui.database.FirebaseRecyclerAdapter
import kotlinx.android.synthetic.main.activity_topics_list.*
import org.jnanaprabodhini.happyteacher.R
import org.jnanaprabodhini.happyteacher.activity.parent.BottomNavigationActivity
import org.jnanaprabodhini.happyteacher.adapter.FirebaseEmptyRecyclerAdapter
import org.jnanaprabodhini.happyteacher.extension.getPrimaryLanguageCode
import org.jnanaprabodhini.happyteacher.extension.setVisibilityGone
import org.jnanaprabodhini.happyteacher.extension.setVisible
import org.jnanaprabodhini.happyteacher.model.LessonHeader
import org.jnanaprabodhini.happyteacher.model.Subject
import org.jnanaprabodhini.happyteacher.model.Topic
import org.jnanaprabodhini.happyteacher.viewholder.LessonHeaderViewHolder
import org.jnanaprabodhini.happyteacher.viewholder.TopicViewHolder
import java.util.*


class TopicsListActivity : BottomNavigationActivity() {

    companion object {
        val EXTRA_TOPICS_KEY_URL: String = "EXTRA_TOPICS_KEY_URL"
        fun Intent.hasTopicsKeyUrl(): Boolean = hasExtra(EXTRA_TOPICS_KEY_URL)
        fun Intent.getTopicsKeyUrl(): String = getStringExtra(EXTRA_TOPICS_KEY_URL)

        val EXTRA_SUBJECT: String = "EXTRA_SUBJECT"
        fun Intent.hasSubject(): Boolean = hasExtra(EXTRA_SUBJECT)
        fun Intent.getSubject(): String = getStringExtra(EXTRA_SUBJECT)
    }

    @IntegerRes override val bottomNavigationMenuItemId: Int = R.id.navigation_topics

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_topics_list)

        if (intent.hasTopicsKeyUrl() && intent.hasSubject()) {
            Log.d("GRAHAM", "Topic key url: ${intent.getTopicsKeyUrl()}")
            updateListOfTopicsFromIndices(intent.getTopicsKeyUrl(), intent.getSubject())
        } else {
            this.setupSubjectSpinner()
        }

        topicsRecyclerView.layoutManager = LinearLayoutManager(this)
        bottomNavigation.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener)
        bottomNavigation.selectedItemId = R.id.navigation_topics
    }

    private fun setupSubjectSpinner() {
        val subjectQuery = databaseInstance.getReference(getString(R.string.subjects))
                .orderByChild(getString(R.string.is_active))
                .equalTo(true)

        val subjectAdapter = object : FirebaseListAdapter<Subject>(this, Subject::class.java, R.layout.spinner_item, subjectQuery) {
            override fun populateView(view: View, subject: Subject, position: Int) {
                (view as TextView).text = subject.names[getPrimaryLanguageCode()]
            }
        }

        subjectSpinner.adapter = subjectAdapter

        subjectSpinner.onItemSelectedListener = object: AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedSubjectKey = subjectAdapter.getRef(position).key
                updateListOfTopics(selectedSubjectKey)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    fun updateListOfTopics(subjectKey: String) {
        val topicQuery = databaseInstance.getReference(getString(R.string.topics))
                .child(subjectKey)
                .orderByChild(getString(R.string.is_active))
                .equalTo(true)

        val topicAdapter = object: FirebaseRecyclerAdapter<Topic, TopicViewHolder>(Topic::class.java, R.layout.list_item_topic, TopicViewHolder::class.java, topicQuery) {
            override fun populateViewHolder(topicViewHolder: TopicViewHolder?, topicModel: Topic?, topicPosition: Int) {
//                val topicKey = this.getRef(topicPosition).key // todo : use this line, not dummy query
                val topicKey = getString(R.string.mathematics_addition) // todo: remove dummy!
                populateTopicViewHolder(topicViewHolder, topicModel, topicPosition, topicKey)
            }
        }
        topicsRecyclerView.adapter = topicAdapter
    }

    fun updateListOfTopicsFromIndices(keyLocationUrl: String, subjectKey: String) {
        val keyReference = databaseInstance.getReferenceFromUrl(keyLocationUrl)
        val topicsReference = databaseInstance.getReference(getString(R.string.topics)).child(subjectKey)

        Log.d("GRAHAM", "keyRef: $keyReference")
        Log.d("GRAHAM", "keyRef: $topicsReference")

        val topicAdapter = object: FirebaseIndexRecyclerAdapter<Topic, TopicViewHolder>(Topic::class.java, R.layout.list_item_topic, TopicViewHolder::class.java, keyReference, topicsReference) {
            override fun populateViewHolder(topicViewHolder: TopicViewHolder?, topicModel: Topic?, topicPosition: Int) {
//                val topicKey = this.getRef(topicPosition).key // todo : use this line, not dummy query
                val topicKey = getString(R.string.mathematics_addition) // todo: remove dummy!
                populateTopicViewHolder(topicViewHolder, topicModel, topicPosition, topicKey)
            }
        }
        topicsRecyclerView.adapter = topicAdapter
    }

    fun populateTopicViewHolder(topicViewHolder: TopicViewHolder?, topicModel: Topic?, topicPosition: Int, topicKey: String) {
        topicViewHolder?.topicTextView?.text = topicModel?.names?.get("en")

        // Alternate between these four colors:
        when (topicPosition % 4) {
            0 -> topicViewHolder?.itemView?.setBackgroundResource(R.color.seaTeal)
            1 -> topicViewHolder?.itemView?.setBackgroundResource(R.color.grassGreen)
            2 -> topicViewHolder?.itemView?.setBackgroundResource(R.color.bubbleGumPink)
            3 -> topicViewHolder?.itemView?.setBackgroundResource(R.color.dreamsicleOrange)
        }

        val lessonHeaderQuery = databaseInstance.getReference(getString(R.string.lesson_headers)) // todo: camelCase lessonHeaders
                .child(topicKey) // hardcoded just for testing!
                .orderByChild(getString(R.string.name))

        val dateFormat = DateFormat.getDateFormat(this@TopicsListActivity)

        // TODO: remove crazy nesting! hehe
        val lessonHeaderAdapter = object: FirebaseEmptyRecyclerAdapter<LessonHeader, LessonHeaderViewHolder>(LessonHeader::class.java, R.layout.list_item_lesson, LessonHeaderViewHolder::class.java, lessonHeaderQuery) {

            override fun populateViewHolder(lessonHeaderViewHolder: LessonHeaderViewHolder?, lessonHeaderModel: LessonHeader?, lessonHeaderPosition: Int) {
                lessonHeaderViewHolder?.lessonTitleTextView?.text = lessonHeaderModel?.name
                lessonHeaderViewHolder?.authorNameTextView?.text = lessonHeaderModel?.authorName
                lessonHeaderViewHolder?.institutionTextView?.text = lessonHeaderModel?.authorInstitution
                lessonHeaderViewHolder?.locationTextView?.text = lessonHeaderModel?.authorLocation

                if (lessonHeaderModel != null) {
                    lessonHeaderViewHolder?.dateEditedTextView?.text = dateFormat.format(Date(lessonHeaderModel.dateEdited))
                }
            }

            override fun onEmpty() {
                topicViewHolder?.lessonsRecyclerView?.setVisibilityGone()
                topicViewHolder?.emptyView?.setVisible()
            }

            override fun onNonEmpty() {
                topicViewHolder?.lessonsRecyclerView?.setVisible()
                topicViewHolder?.emptyView?.setVisibilityGone()
            }
        }

        val horizontalLayoutManager = LinearLayoutManager(this@TopicsListActivity, LinearLayoutManager.HORIZONTAL, false)
        topicViewHolder?.lessonsRecyclerView?.layoutManager = horizontalLayoutManager
        topicViewHolder?.lessonsRecyclerView?.adapter = lessonHeaderAdapter
    }

    // Remove transition for this activity to avoid bottom navigation jumpiness.
    public override fun onPause() {
        super.onPause()
        overridePendingTransition(0, 0)
    }
}
