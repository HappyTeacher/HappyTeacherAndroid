package org.jnanaprabodhini.happyteacher.activity

import android.content.Intent
import android.os.Bundle
import android.support.annotation.IntegerRes
import android.support.annotation.LayoutRes
import android.support.v7.widget.LinearLayoutManager
import android.text.format.DateFormat
import android.view.View
import android.widget.Spinner
import android.widget.TextView
import com.firebase.ui.database.FirebaseListAdapter
import com.google.firebase.database.Query
import kotlinx.android.synthetic.main.activity_topics_list.*
import kotlinx.android.synthetic.main.header_syllabus_lesson_topic.*
import org.jnanaprabodhini.happyteacher.DataObserver
import org.jnanaprabodhini.happyteacher.R
import org.jnanaprabodhini.happyteacher.activity.parent.BottomNavigationActivity
import org.jnanaprabodhini.happyteacher.adapter.FirebaseDataObserverRecyclerAdapter
import org.jnanaprabodhini.happyteacher.model.Subject
import org.jnanaprabodhini.happyteacher.model.Topic
import org.jnanaprabodhini.happyteacher.viewholder.SubtopicViewHolder
import org.jnanaprabodhini.happyteacher.viewholder.TopicViewHolder
import java.util.*
import org.jnanaprabodhini.happyteacher.adapter.FirebaseIndexDataObserverRecyclerAdapter
import org.jnanaprabodhini.happyteacher.extension.*
import org.jnanaprabodhini.happyteacher.model.Subtopic
import org.jnanaprabodhini.happyteacher.prefs


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

    object SavedInstanceStateConstants {
        val PARENT_SUBJECT_SPINNER_SELECTION = "PARENT_SUBJECT_SPINNER_SELECTION"
        val CHILD_SUBJECT_SPINNER_SELECTION = "CHILD_SUBJECT_SPINNER_SELECTION"
    }

    var parentSubjectSelectionIndex = 0
    var childSubjectSelectionIndex = 0

    @IntegerRes override val bottomNavigationMenuItemId: Int = R.id.navigation_topics

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_topics_list)

        topicsRecyclerView.layoutManager = LinearLayoutManager(this)
        bottomNavigation.selectedItemId = bottomNavigationMenuItemId
        bottomNavigation.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener)

        setSpinnerSelectionIndicesFromSavedInstanceState(savedInstanceState)

        initializeUiFromIntent()
    }

    /**
     *  If there are stored index positions for the spinners, set their properties accordingly.
     *   These properties will be used to select the stored selection when the spinners are populated.
     */
    private fun setSpinnerSelectionIndicesFromSavedInstanceState(savedInstanceState: Bundle?) {
        if (savedInstanceState == null) return

        val parentSubjectStoredSelection = savedInstanceState.getInt(SavedInstanceStateConstants.PARENT_SUBJECT_SPINNER_SELECTION, 0)
        val childSubjectStoredSelection = savedInstanceState.getInt(SavedInstanceStateConstants.CHILD_SUBJECT_SPINNER_SELECTION, 0)

        this.parentSubjectSelectionIndex = parentSubjectStoredSelection
        this.childSubjectSelectionIndex = childSubjectStoredSelection
    }

    fun initializeUiFromIntent() {
        if (intent.hasAllExtras()) {
            // Show topics related to the given syllabus lesson plan
            val topicsKeyUrl = intent.getTopicsKeyUrl()
            val subject = intent.getSubject()
            val level = intent.getLevel()
            val title = intent.getLessonTitle()

            showSyllabusLessonTopicHeader(title, subject, level)
            updateListOfTopicsFromIndices(topicsKeyUrl, level)
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
    private fun setupSpinner(spinner: Spinner, @LayoutRes spinnerLayout: Int, parentSubjectId: String?, selectionIndex: Int) {
        val subjectQuery = databaseReference.child(getString(R.string.subjects)).orderByChild(getString(R.string.parent_subject)).equalTo(parentSubjectId)

        val subjectAdapter = object : FirebaseListAdapter<Subject>(this, Subject::class.java, spinnerLayout, subjectQuery) {
            override fun populateView(view: View, subject: Subject, position: Int) {
                (view as TextView).text = subject.name
            }
        }

        subjectAdapter.onDataChanged {
            if (spinner.count >= selectionIndex) spinner.setSelection(selectionIndex)
        }

        spinner.adapter = subjectAdapter

        spinner.onItemSelected { position ->
            val subject = subjectAdapter.getItem(position)
            val selectedSubjectKey = subjectAdapter.getRef(position).key

            if (subject.hasChildren) {
                setupSpinner(childSubjectSpinner, R.layout.spinner_item_child, selectedSubjectKey, childSubjectSelectionIndex)
            } else if (!subject.hasChildren && spinner == childSubjectSpinner) {
                updateListOfTopics(selectedSubjectKey)
            } else {
                updateListOfTopics(selectedSubjectKey)
                childSubjectSpinner.setVisibilityGone()
            }
        }

        spinner.setVisible()
    }

    private fun setupParentSubjectSpinner() {
        // Parents have no parents, so pass null as value for parentSubject:
        setupSpinner(parentSubjectSpinner, R.layout.spinner_item, null, parentSubjectSelectionIndex)
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
                val topicKey = this.getRef(topicPosition).key
                populateUnfilteredTopicViewHolder(topicViewHolder, topicModel, topicPosition, topicKey)
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
        databaseReference.child(getString(R.string.subjects)).child(subject).ref.onSingleValueEvent { dataSnapshot ->
            val subjectModel = dataSnapshot?.getValue(Subject::class.java)
            val subjectName = subjectModel?.name

            val standardString = getString(R.string.standard_n, standard)
            syllabusLessonSubjectStandardTextView.text = "$subjectName, $standardString"
        }
    }

    private fun hideSyllabusLessonTopicHeader() {
        syllabusLessonTopicsHeaderView.setVisibilityGone()
    }

    /**
     *  Show the list of topics relevant to the given syllabus lesson plan.
     *   The adapter looks for topics with specific keys (these keys come
     *   from the syllabus lesson's list of relevant topics).
     */
    private fun updateListOfTopicsFromIndices(keyLocationUrl: String, level: Int) {
        onRequestNewData()

        val keyReference = databaseRoot.getReferenceFromUrl(keyLocationUrl)
        val topicsReference = databaseReference.child(getString(R.string.topics))

        val topicIndexAdapter = object: FirebaseIndexDataObserverRecyclerAdapter<Topic, TopicViewHolder>(Topic::class.java, R.layout.list_item_topic, TopicViewHolder::class.java, keyReference, topicsReference, this) {
            override fun populateViewHolder(topicViewHolder: TopicViewHolder?, topicModel: Topic?, topicPosition: Int) {
                val topicKey = this.getRef(topicPosition).key
                populateLevelFilteredTopicViewHolder(topicViewHolder, topicModel, topicPosition, topicKey, level)
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

    private fun getSubtopicDataObserverForViewHolder(topicViewHolder: TopicViewHolder?) = object: DataObserver {
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

    private fun populateUnfilteredTopicViewHolder(topicViewHolder: TopicViewHolder?, topicModel: Topic?, topicPosition: Int, topicKey: String) {
        populateTopicViewHolder(topicViewHolder, topicModel, topicPosition)

        val subtopicQuery = databaseReference.child(getString(R.string.subtopic_lesson_headers))
                .child(topicKey)
                .orderByChild(getString(R.string.name))

        setSubtopicRecyclerAdapterUnfiltered(topicViewHolder, subtopicQuery, DateFormat.getDateFormat(this@TopicsListActivity))
    }

    private fun populateLevelFilteredTopicViewHolder(topicViewHolder: TopicViewHolder?, topicModel: Topic?, topicPosition: Int, topicKey: String, level: Int) {
        populateTopicViewHolder(topicViewHolder, topicModel, topicPosition)
        setSubtopicViewHolderRecyclerFilteredByLevel(topicViewHolder, level, topicKey, DateFormat.getDateFormat(this@TopicsListActivity))
    }

    private fun populateTopicViewHolder(topicViewHolder: TopicViewHolder?, topicModel: Topic?, topicPosition: Int) {
        topicViewHolder?.topicTextView?.text = topicModel?.name

        // Alternate between these four colors:
        when (topicPosition % 4) {
            0 -> topicViewHolder?.itemView?.setBackgroundResource(R.color.seaTeal)
            1 -> topicViewHolder?.itemView?.setBackgroundResource(R.color.grassGreen)
            2 -> topicViewHolder?.itemView?.setBackgroundResource(R.color.bubbleGumPink)
            3 -> topicViewHolder?.itemView?.setBackgroundResource(R.color.dreamsicleOrange)
        }

        val horizontalLayoutManager = LinearLayoutManager(this@TopicsListActivity, LinearLayoutManager.HORIZONTAL, false)
        topicViewHolder?.lessonsRecyclerView?.layoutManager = horizontalLayoutManager
    }

    private fun setSubtopicRecyclerAdapterUnfiltered(topicViewHolder: TopicViewHolder?, subtopicQuery: Query, dateFormat: java.text.DateFormat) {
        topicViewHolder?.lessonsRecyclerView?.adapter = object: FirebaseDataObserverRecyclerAdapter<Subtopic, SubtopicViewHolder>(Subtopic::class.java, R.layout.list_item_lesson, SubtopicViewHolder::class.java, subtopicQuery, getSubtopicDataObserverForViewHolder(topicViewHolder)) {
            override fun populateViewHolder(subtopicViewHolder: SubtopicViewHolder?, subtopicModel: Subtopic?, lessonHeaderPosition: Int) {
                populateLessonHeaderViewHolder(subtopicViewHolder, subtopicModel, dateFormat)
            }
        }
    }

    private fun setSubtopicViewHolderRecyclerFilteredByLevel(topicViewHolder: TopicViewHolder?, level: Int, topicKey: String, dateFormat: java.text.DateFormat) {
        val boardLevelSubtopicsQuery = databaseReference.child("boards")
                .child(prefs.getBoardKey())
                .child("level_topics")
                .child(level.toString())

        // TODO (BACKEND): add a subject parent above topic
        val subtopicLessonHeaderReference = databaseReference.child(getString(R.string.subtopic_lesson_headers)).child(topicKey)

        topicViewHolder?.lessonsRecyclerView?.adapter = object: FirebaseIndexDataObserverRecyclerAdapter<Subtopic, SubtopicViewHolder>(Subtopic::class.java, R.layout.list_item_lesson, SubtopicViewHolder::class.java, boardLevelSubtopicsQuery, subtopicLessonHeaderReference, getSubtopicDataObserverForViewHolder(topicViewHolder)) {
            override fun populateViewHolder(subtopicViewHolder: SubtopicViewHolder?, subtopicModel: Subtopic?, lessonHeaderPosition: Int) {
                populateLessonHeaderViewHolder(subtopicViewHolder, subtopicModel, dateFormat)
            }
        }
    }

    private fun populateLessonHeaderViewHolder(subtopicViewHolder: SubtopicViewHolder?, subtopicModel: Subtopic?, dateFormat: java.text.DateFormat) {
        subtopicViewHolder?.lessonTitleTextView?.text = subtopicModel?.name
        subtopicViewHolder?.authorNameTextView?.text = subtopicModel?.authorName
        subtopicViewHolder?.institutionTextView?.text = subtopicModel?.authorInstitution
        subtopicViewHolder?.locationTextView?.text = subtopicModel?.authorLocation

        subtopicViewHolder?.authorNameTextView?.setDrawableLeft(R.drawable.ic_person_accent)
        subtopicViewHolder?.institutionTextView?.setDrawableLeft(R.drawable.ic_school_accent)
        subtopicViewHolder?.locationTextView?.setDrawableLeft(R.drawable.ic_location_accent)

        if (subtopicModel != null) {
            subtopicViewHolder?.dateEditedTextView?.text = dateFormat.format(Date(subtopicModel.dateEdited))
            subtopicViewHolder?.dateEditedTextView?.setDrawableLeft(R.drawable.ic_clock_light_gray)
        }
    }

    override fun onBackPressed() {
        if (intent.hasAllExtras()) {
            // If the topics list is displayed from a
            //  syllabus lesson plan, then pressing back
            //  should take us back to that activity.
            finish()
        } else {
            super.onBackPressed()
        }
    }

    override fun onSaveInstanceState(savedInstanceState: Bundle) {
        val parentSubjectSpinnerSelectionIndex = parentSubjectSpinner.selectedItemPosition
        val childSubjectSpinnerSelectionIndex = childSubjectSpinner.selectedItemPosition

        savedInstanceState.putInt(SavedInstanceStateConstants.PARENT_SUBJECT_SPINNER_SELECTION, parentSubjectSpinnerSelectionIndex)
        savedInstanceState.putInt(SavedInstanceStateConstants.CHILD_SUBJECT_SPINNER_SELECTION, childSubjectSpinnerSelectionIndex)

        super.onSaveInstanceState(savedInstanceState)
    }
}
