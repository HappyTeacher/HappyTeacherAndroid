package org.jnanaprabodhini.happyteacher.activity

import android.content.Intent
import android.os.Bundle
import android.support.annotation.IntegerRes
import android.support.annotation.LayoutRes
import android.support.v7.widget.LinearLayoutManager
import android.text.format.DateFormat
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.Spinner
import android.widget.TextView
import com.firebase.ui.database.FirebaseListAdapter
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_topics_list.*
import kotlinx.android.synthetic.main.header_syllabus_lesson_topic.*
import org.jnanaprabodhini.happyteacher.DataObserver
import org.jnanaprabodhini.happyteacher.R
import org.jnanaprabodhini.happyteacher.activity.parent.BottomNavigationActivity
import org.jnanaprabodhini.happyteacher.adapter.FirebaseDataObserverRecyclerAdapter
import org.jnanaprabodhini.happyteacher.extension.isGone
import org.jnanaprabodhini.happyteacher.extension.setVisibilityGone
import org.jnanaprabodhini.happyteacher.extension.setVisible
import org.jnanaprabodhini.happyteacher.model.LessonHeader
import org.jnanaprabodhini.happyteacher.model.Subject
import org.jnanaprabodhini.happyteacher.model.Topic
import org.jnanaprabodhini.happyteacher.viewholder.LessonHeaderViewHolder
import org.jnanaprabodhini.happyteacher.viewholder.TopicViewHolder
import java.util.*
import org.jnanaprabodhini.happyteacher.adapter.FirebaseIndexDataObserverRecyclerAdapter


class TopicsListActivity : BottomNavigationActivity(), DataObserver {

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

        topicsRecyclerView.layoutManager = LinearLayoutManager(this)
        bottomNavigation.selectedItemId = bottomNavigationMenuItemId
        bottomNavigation.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener)

        initializeUiFromIntent()
    }

    fun initializeUiFromIntent() {
        if (intent.hasAllExtras()) {
            // Show topics related to the given syllabus lesson plan
            val topicsKeyUrl = intent.getTopicsKeyUrl()
            val subject = intent.getSubject()
            val level = intent.getLevel()
            val title = intent.getLessonTitle()

            showSyllabusLessonTopicHeader(title, subject, level)
            updateListOfTopicsFromIndices(topicsKeyUrl, subject)
        } else {
            initializeTopicListForSubject()
        }
    }

    fun initializeTopicListForSubject() {
        // Show all topics for a subject, selected by spinner
        setupParentSubjectSpinner()
        hideSyllabusLessonTopicHeader()
    }

    override fun onBottomNavigationItemReselected() {
        if (parentSubjectSpinner.isGone()) {
            // Reset to subject spinner view:
            initializeTopicListForSubject()
        } else {
            // Scroll to top of topic list:
            topicsRecyclerView.smoothScrollToPosition(0)
        }
    }

    /**
     *  The subject spinner is shown when this activity is not being used
     *   to display topics relevant to a specific syllabus lesson plan.
     */
    private fun setupSpinner(spinner: Spinner, @LayoutRes spinnerLayout: Int, parentSubjectId: String?) {
        val subjectQuery = databaseReference.child(getString(R.string.subjects)).orderByChild(getString(R.string.parent_subject)).equalTo(parentSubjectId)

        val subjectAdapter = object : FirebaseListAdapter<Subject>(this, Subject::class.java, spinnerLayout, subjectQuery) {
            override fun populateView(view: View, subject: Subject, position: Int) {
                (view as TextView).text = subject.name
            }
        }

        spinner.adapter = subjectAdapter

        spinner.onItemSelectedListener = object: AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val subject = subjectAdapter.getItem(position)
                val selectedSubjectKey = subjectAdapter.getRef(position).key

                if (subject.hasChildren) {
                    setupSpinner(childSubjectSpinner, R.layout.spinner_item_child, selectedSubjectKey)
                } else if (!subject.hasChildren && spinner == childSubjectSpinner) {
                    updateListOfTopics(selectedSubjectKey)
                } else {
                    updateListOfTopics(selectedSubjectKey)
                    childSubjectSpinner.setVisibilityGone()
                }

            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        spinner.setVisible()
    }

    private fun setupParentSubjectSpinner() {
        // Parents have no parents, so pass null as value for parentSubject:
        setupSpinner(parentSubjectSpinner, R.layout.spinner_item, null)
    }

    /**
     *  Display list of topics for the selected subject.
     */
    fun updateListOfTopics(subjectKey: String) {
        onRequestNewData()

        val topicQuery = databaseReference.child(getString(R.string.topics))
                .orderByChild(getString(R.string.subject)).equalTo(subjectKey)

        val topicAdapter = object: FirebaseDataObserverRecyclerAdapter<Topic, TopicViewHolder>(Topic::class.java, R.layout.list_item_topic, TopicViewHolder::class.java, topicQuery, this) {
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
        hideSpinners()
        syllabusLessonTopicsHeaderView.setVisible()

        headerBackArrow.setOnClickListener { finish() }

        syllabusLessonPlanNameTextView.text = syllabusLessonPlanTitle

        // Get the actual subject model so we can access its name:
        databaseReference.child(getString(R.string.subjects)).child(subject).ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError?) {}
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val subjectModel = dataSnapshot.getValue(Subject::class.java)
                val subjectName = subjectModel?.name

                val standardString = getString(R.string.standard_n, standard)
                syllabusLessonSubjectStandardTextView.text = "$subjectName, $standardString"
            }
        })
    }

    private fun hideSyllabusLessonTopicHeader() {
        syllabusLessonTopicsHeaderView.setVisibilityGone()
    }

    /**
     *  Show the list of topics relevant to the given syllabus lesson plan.
     *   The adapter looks for topics with specific keys (these keys come
     *   from the syllabus lesson's list of relevant topics).
     */
    fun updateListOfTopicsFromIndices(keyLocationUrl: String, subjectKey: String) {
        onRequestNewData()

        val keyReference = databaseRoot.getReferenceFromUrl(keyLocationUrl)
        val topicsReference = databaseReference.child(getString(R.string.topics))

        val topicIndexAdapter = object: FirebaseIndexDataObserverRecyclerAdapter<Topic, TopicViewHolder>(Topic::class.java, R.layout.list_item_topic, TopicViewHolder::class.java, keyReference, topicsReference, this) {
            override fun populateViewHolder(topicViewHolder: TopicViewHolder?, topicModel: Topic?, topicPosition: Int) {
//                val topicKey = this.getRef(topicPosition).key // todo : use this line, not dummy query
                val topicKey = getString(R.string.DUMMY_KEY_FOR_TESTING) // todo: remove dummy!
                populateTopicViewHolder(topicViewHolder, topicModel, topicPosition, topicKey)
            }
        }
        topicsRecyclerView.adapter = topicIndexAdapter
    }

    private fun hideSpinners() {
        parentSubjectSpinner.setVisibilityGone()
        childSubjectSpinner.setVisibilityGone()
    }

    override fun onRequestNewData() {
        topicsProgressBar.setVisible()
        emptyTopicsTextView.setVisibilityGone()
    }

    override fun onDataLoaded() {
        topicsProgressBar.setVisibilityGone()
    }

    override fun onDataEmpty() {
        emptyTopicsTextView.setVisible()
    }

    override fun onDataNonEmpty() {
        emptyTopicsTextView.setVisibilityGone()

        // Animate layout changes
        topicsRecyclerView.scheduleLayoutAnimation()
        topicsRecyclerView.invalidate()
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

        val dateFormat = DateFormat.getDateFormat(this@TopicsListActivity)

        val lessonHeaderQuery = databaseReference.child(getString(R.string.lesson_headers))
                .child(topicKey) // hardcoded just for testing!
                .orderByChild(getString(R.string.name))

        val lessonHeaderDataObserver = object: DataObserver {
            override fun onRequestNewData() {
                // TODO: Show progress bar here!
            }

            override fun onDataLoaded() {
                // TODO: Hide progress bar here!
            }

            override fun onDataEmpty() {
                topicViewHolder?.lessonsRecyclerView?.setVisibilityGone()
                topicViewHolder?.emptyView?.setVisible()
            }

            override fun onDataNonEmpty() {
                topicViewHolder?.lessonsRecyclerView?.setVisible()
                topicViewHolder?.emptyView?.setVisibilityGone()
            }
        }

        val lessonHeaderAdapter = object: FirebaseDataObserverRecyclerAdapter<LessonHeader, LessonHeaderViewHolder>(LessonHeader::class.java, R.layout.list_item_lesson, LessonHeaderViewHolder::class.java, lessonHeaderQuery, lessonHeaderDataObserver) {
            override fun populateViewHolder(lessonHeaderViewHolder: LessonHeaderViewHolder?, lessonHeaderModel: LessonHeader?, lessonHeaderPosition: Int) {
                populateLessonHeaderViewHolder(lessonHeaderViewHolder, lessonHeaderModel, dateFormat)
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