package org.jnanaprabodhini.happyteacher.activity

import android.content.Intent
import android.os.Bundle
import android.support.annotation.IntegerRes
import android.support.v7.widget.LinearLayoutManager
import android.text.format.DateFormat
import android.view.View
import android.widget.AdapterView
import android.widget.TextView
import com.firebase.ui.database.FirebaseIndexRecyclerAdapter
import com.firebase.ui.database.FirebaseListAdapter
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_topics_list.*
import org.jnanaprabodhini.happyteacher.R
import org.jnanaprabodhini.happyteacher.activity.parent.BottomNavigationActivity
import org.jnanaprabodhini.happyteacher.adapter.FirebaseEmptyRecyclerAdapter
import org.jnanaprabodhini.happyteacher.extension.setVisibilityGone
import org.jnanaprabodhini.happyteacher.extension.setVisible
import org.jnanaprabodhini.happyteacher.model.LessonHeader
import org.jnanaprabodhini.happyteacher.model.Subject
import org.jnanaprabodhini.happyteacher.model.Topic
import org.jnanaprabodhini.happyteacher.viewholder.LessonHeaderViewHolder
import org.jnanaprabodhini.happyteacher.viewholder.TopicViewHolder
import java.util.*


class TopicsListActivity : BottomNavigationActivity() {

    companion object IntentExtraHelper {
        val EXTRA_TOPICS_KEY_URL: String = "EXTRA_TOPICS_KEY_URL"
        fun Intent.hasTopicsKeyUrl(): Boolean = hasExtra(EXTRA_TOPICS_KEY_URL)
        // Note: since we the url may have ~ or " " chars, we need to decode it in order to read that db location!
        fun Intent.getTopicsKeyUrl(): String = java.net.URLDecoder.decode(getStringExtra(EXTRA_TOPICS_KEY_URL), "UTF-8")

        val EXTRA_SUBJECT_NAME: String = "EXTRA_SUBJECT_NAME"
        fun Intent.hasSubject(): Boolean = hasExtra(EXTRA_SUBJECT_NAME)
        fun Intent.getSubject(): String = getStringExtra(EXTRA_SUBJECT_NAME)

        val EXTRA_LEVEL: String = "EXTRA_LEVEL"
        fun Intent.hasLevel(): Boolean = hasExtra(EXTRA_LEVEL)
        fun Intent.getLevel(): Int = getIntExtra(EXTRA_LEVEL, 0)

        val EXTRA_LESSON_TITLE: String = "EXTRA_LESSON_TITLE"
        fun Intent.hasLessonTitle(): Boolean = hasExtra(EXTRA_LESSON_TITLE)
        fun Intent.getLessonTitle(): String = getStringExtra(EXTRA_LESSON_TITLE)

        fun Intent.hasAllExtras(): Boolean = hasTopicsKeyUrl() && hasSubject() && hasLevel() && hasLessonTitle()
    }

    @IntegerRes override val bottomNavigationMenuItemId: Int = R.id.navigation_topics

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_topics_list)

        if (intent.hasAllExtras()) {
            // Show topics related to the given syllabus lesson plan

            val topicsKeyUrl = intent.getTopicsKeyUrl()
            val subject = intent.getSubject()
            val level = intent.getLevel()
            val title = intent.getLessonTitle()

            showSyllabusLessonTopicHeader(title, subject, level)
            updateListOfTopicsFromIndices(topicsKeyUrl, subject)

            // If there are no topics, we will not come to this activity,
            //  so the topics list isn't empty and this view can be hidden:
            emptyTopicsTextView.setVisibilityGone()
        } else {
            // Show all topics for a subject, selected by spinner
            hideSyllabusLessonTopicHeader()
            setupSubjectSpinner()
        }

        topicsRecyclerView.layoutManager = LinearLayoutManager(this)
        bottomNavigation.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener)
    }

    /**
     *  The subject spinner is shown when this activity is not being used
     *   to display topics relevant to a specific syllabus lesson plan.
     */
    private fun setupSubjectSpinner() {
        subjectSpinner.setVisible()

        val subjectQuery = databaseReference.child(getString(R.string.subjects))
                .orderByChild(getString(R.string.is_active))
                .equalTo(true)

        val subjectAdapter = object : FirebaseListAdapter<Subject>(this, Subject::class.java, R.layout.spinner_item, subjectQuery) {
            override fun populateView(view: View, subject: Subject, position: Int) {
                (view as TextView).text = subject.name
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

    /**
     *  Display list of topics for the selected subject.
     */
    fun updateListOfTopics(subjectKey: String) {
        val topicQuery = databaseReference.child(getString(R.string.topics))
                .child(subjectKey)
                .orderByChild(getString(R.string.is_active))
                .equalTo(true)

        val topicAdapter = object: FirebaseEmptyRecyclerAdapter<Topic, TopicViewHolder>(Topic::class.java, R.layout.list_item_topic, TopicViewHolder::class.java, topicQuery) {
            override fun onEmpty() {
                emptyTopicsTextView.setVisible()
            }

            override fun onNonEmpty() {
                emptyTopicsTextView.setVisibilityGone()
            }

            override fun populateViewHolder(topicViewHolder: TopicViewHolder?, topicModel: Topic?, topicPosition: Int) {
//                val topicKey = this.getRef(topicPosition).key // todo : use this line, not dummy query
                val topicKey = getString(R.string.DUMMY_KEY_FOR_TESTING) // todo: remove dummy!
                populateTopicViewHolder(topicViewHolder, topicModel, topicPosition, topicKey)
            }
        }
        topicsRecyclerView.adapter = topicAdapter
    }

    /**
     *  Show the Topic Header for a syllabus lesson.
     *
     *   This is shown if we are coming into the Topics List from clicking on
     *   a Syllabus Lesson. This header will show the name, subject, and level
     *   of that less.
     */
    private fun showSyllabusLessonTopicHeader(syllabusLessonPlanTitle: String, subject: String, standard: Int) {
        subjectSpinner.setVisibilityGone()
        syllabusLessonPlanNameTextView.setVisible()

        syllabusLessonPlanNameTextView.text = syllabusLessonPlanTitle

        // Get the actual subject model so we can access its name:
        databaseReference.child(getString(R.string.subjects)).child(subject).ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError?) {}
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val subjectModel = dataSnapshot.getValue(Subject::class.java)
                val subjectName = subjectModel?.name
                syllabusLessonSubjectStandardTextView.setVisible()

                val standardString = getString(R.string.standard_n, standard)
                syllabusLessonSubjectStandardTextView.text = "$subjectName, $standardString"
            }
        })
    }

    private fun hideSyllabusLessonTopicHeader() {
        syllabusLessonPlanNameTextView.setVisibilityGone()
        syllabusLessonSubjectStandardTextView.setVisibilityGone()
    }

    /**
     *  Show the list of topics relevant to the given syllabus lesson plan.
     *   The adapter looks for topics with specific keys (these keys come
     *   from the syllabus lesson's list of relevant topics).
     */
    fun updateListOfTopicsFromIndices(keyLocationUrl: String, subjectKey: String) {
        val keyReference = databaseRoot.getReferenceFromUrl(keyLocationUrl)
        val topicsReference = databaseReference.child(getString(R.string.topics)).child(subjectKey)

        val topicAdapter = object: FirebaseIndexRecyclerAdapter<Topic, TopicViewHolder>(Topic::class.java, R.layout.list_item_topic, TopicViewHolder::class.java, keyReference, topicsReference) {
            override fun populateViewHolder(topicViewHolder: TopicViewHolder?, topicModel: Topic?, topicPosition: Int) {
//                val topicKey = this.getRef(topicPosition).key // todo : use this line, not dummy query
                val topicKey = getString(R.string.DUMMY_KEY_FOR_TESTING) // todo: remove dummy!
                populateTopicViewHolder(topicViewHolder, topicModel, topicPosition, topicKey)
            }
        }
        topicsRecyclerView.adapter = topicAdapter
    }

    fun populateTopicViewHolder(topicViewHolder: TopicViewHolder?, topicModel: Topic?, topicPosition: Int, topicKey: String) {
        topicViewHolder?.topicTextView?.text = topicModel?.name

        // Alternate between these four colors:
        when (topicPosition % 4) {
            0 -> topicViewHolder?.itemView?.setBackgroundResource(R.color.seaTeal)
            1 -> topicViewHolder?.itemView?.setBackgroundResource(R.color.grassGreen)
            2 -> topicViewHolder?.itemView?.setBackgroundResource(R.color.bubbleGumPink)
            3 -> topicViewHolder?.itemView?.setBackgroundResource(R.color.dreamsicleOrange)
        }

        val lessonHeaderQuery = databaseReference.child(getString(R.string.lesson_headers))
                .child(topicKey) // hardcoded just for testing!
                .orderByChild(getString(R.string.name))

        val dateFormat = DateFormat.getDateFormat(this@TopicsListActivity)

        val lessonHeaderAdapter = object: FirebaseEmptyRecyclerAdapter<LessonHeader, LessonHeaderViewHolder>(LessonHeader::class.java, R.layout.list_item_lesson, LessonHeaderViewHolder::class.java, lessonHeaderQuery) {
            override fun populateViewHolder(lessonHeaderViewHolder: LessonHeaderViewHolder?, lessonHeaderModel: LessonHeader?, lessonHeaderPosition: Int) {
                populateLessonHeaderViewHolder(lessonHeaderViewHolder, lessonHeaderModel, dateFormat)
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

    private fun populateLessonHeaderViewHolder(lessonHeaderViewHolder: LessonHeaderViewHolder?, lessonHeaderModel: LessonHeader?, dateFormat: java.text.DateFormat) {
        lessonHeaderViewHolder?.lessonTitleTextView?.text = lessonHeaderModel?.name
        lessonHeaderViewHolder?.authorNameTextView?.text = lessonHeaderModel?.authorName
        lessonHeaderViewHolder?.institutionTextView?.text = lessonHeaderModel?.authorInstitution
        lessonHeaderViewHolder?.locationTextView?.text = lessonHeaderModel?.authorLocation

        if (lessonHeaderModel != null) {
            lessonHeaderViewHolder?.dateEditedTextView?.text = dateFormat.format(Date(lessonHeaderModel.dateEdited))
        }
    }
}
