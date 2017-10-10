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
import org.jnanaprabodhini.happyteacher.R
import org.jnanaprabodhini.happyteacher.activity.parent.BottomNavigationActivity
import org.jnanaprabodhini.happyteacher.adapter.*
import org.jnanaprabodhini.happyteacher.adapter.helper.FirebaseDataObserver
import org.jnanaprabodhini.happyteacher.extension.*
import org.jnanaprabodhini.happyteacher.model.Subject
import org.jnanaprabodhini.happyteacher.model.SubtopicLessonHeader
import org.jnanaprabodhini.happyteacher.model.Topic
import org.jnanaprabodhini.happyteacher.prefs
import org.jnanaprabodhini.happyteacher.adapter.viewholder.SubtopicHeaderViewHolder
import org.jnanaprabodhini.happyteacher.adapter.SubtopicLessonHeaderRecyclerAdapter
import org.jnanaprabodhini.happyteacher.adapter.viewholder.ColoredHorizontalRecyclerViewHolder

class TopicsListActivity : BottomNavigationActivity(), FirebaseDataObserver {

    companion object IntentExtraHelper {
        fun launchActivity(from: BottomNavigationActivity, keyUrl: String, subjectName: String, lessonTitle: String, level: Int) {
            val topicsListIntent = Intent(from, TopicsListActivity::class.java)
            topicsListIntent.apply{
                putExtra(TopicsListActivity.TOPICS_INDEX_LIST_URL, keyUrl)
                putExtra(TopicsListActivity.SUBJECT_NAME, subjectName)
                putExtra(TopicsListActivity.LESSON_TITLE, lessonTitle)
                putExtra(TopicsListActivity.LEVEL, level)
            }

            from.startBottomNavigationActivityWithFade(topicsListIntent)
        }

        val TOPICS_INDEX_LIST_URL: String = "TOPICS_INDEX_LIST_URL"
        fun Intent.hasTopicsIndexListUrl(): Boolean = hasExtra(TOPICS_INDEX_LIST_URL)
        // Note: since we the url may have ~ or " " chars, we need to decode it in order to read that db location!
        fun Intent.getTopicsIndexListUrl(): String = java.net.URLDecoder.decode(getStringExtra(TOPICS_INDEX_LIST_URL), "UTF-8")

        val SUBJECT_NAME: String = "SUBJECT_NAME"
        fun Intent.hasSubject(): Boolean = hasExtra(SUBJECT_NAME)
        fun Intent.getSubject(): String = getStringExtra(SUBJECT_NAME)

        val LEVEL: String = "LEVEL"
        fun Intent.hasLevel(): Boolean = hasExtra(LEVEL)
        fun Intent.getLevel(): Int = getIntExtra(LEVEL, 0)

        val LESSON_TITLE: String = "LESSON_TITLE"
        fun Intent.hasLessonTitle(): Boolean = hasExtra(LESSON_TITLE)
        fun Intent.getLessonTitle(): String = getStringExtra(LESSON_TITLE)

        fun Intent.hasAllExtras(): Boolean = hasTopicsIndexListUrl() && hasSubject() && hasLevel() && hasLessonTitle()
    }

    object SavedInstanceStateConstants {
        val PARENT_SUBJECT_SPINNER_SELECTION = "PARENT_SUBJECT_SPINNER_SELECTION"
        val CHILD_SUBJECT_SPINNER_SELECTION = "CHILD_SUBJECT_SPINNER_SELECTION"
    }

    private var parentSubjectSelectionIndex = 0
    private var childSubjectSelectionIndex = 0

    private val dateFormat by lazy {
        DateFormat.getDateFormat(this)
    }

    @IntegerRes override val bottomNavigationMenuItemId: Int = R.id.navigation_topics

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_topics_list)

        topicsRecyclerView.layoutManager = LinearLayoutManager(this)
        bottomNavigation.selectedItemId = bottomNavigationMenuItemId
        bottomNavigation.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener)

        savedInstanceState?.let { setSpinnerSelectionIndicesFromSavedInstanceState(it) }

        initializeUiFromIntent()
    }

    /**
     *  If there are stored index positions for the spinners, set their properties accordingly.
     *   These properties will be used to select the stored selection when the spinners are populated.
     */
    private fun setSpinnerSelectionIndicesFromSavedInstanceState(savedInstanceState: Bundle) {
        val parentSubjectStoredSelection = savedInstanceState.getInt(SavedInstanceStateConstants.PARENT_SUBJECT_SPINNER_SELECTION, 0)
        val childSubjectStoredSelection = savedInstanceState.getInt(SavedInstanceStateConstants.CHILD_SUBJECT_SPINNER_SELECTION, 0)

        this.parentSubjectSelectionIndex = parentSubjectStoredSelection
        this.childSubjectSelectionIndex = childSubjectStoredSelection
    }

    fun initializeUiFromIntent() {
        if (intent.hasAllExtras()) {
            // Show topics related to the given syllabus lesson plan
            val topicsIndexListUrl = intent.getTopicsIndexListUrl()
            val subject = intent.getSubject()
            val level = intent.getLevel()
            val title = intent.getLessonTitle()

            showSyllabusLessonTopicHeader(title, subject, level)
            updateListOfTopicsFromIndexList(topicsIndexListUrl, level)
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
     *  Set up one of the two spinners (the subject spinner or the sub-subject spinner).
     *
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

        spinner.adapter = subjectAdapter
        spinner.selectIndexWhenPopulated(selectionIndex)

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
        // Parent subjects have no parents, so pass null as value for parentSubject:
        setupSpinner(parentSubjectSpinner, R.layout.spinner_item, null, parentSubjectSelectionIndex)
    }

    /**
     *  Display list of topics for the selected subject.
     */
    fun updateListOfTopics(subjectKey: String) {
        val topicQuery = databaseReference.child(getString(R.string.topics))
                .orderByChild(getString(R.string.subject)).equalTo(subjectKey)

        val topicAdapter = object: FirebaseObserverRecyclerAdapter<Topic, ColoredHorizontalRecyclerViewHolder>(Topic::class.java, R.layout.list_item_colored_horizontal_recycler, ColoredHorizontalRecyclerViewHolder::class.java, topicQuery, this) {
            override fun populateViewHolder(topicViewHolder: ColoredHorizontalRecyclerViewHolder?, topicModel: Topic?, topicPosition: Int) {
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
     *   of that lesson.
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
     *   from the syllabus lesson's index list of relevant topics).
     */
    private fun updateListOfTopicsFromIndexList(indexListLocationUrl: String, level: Int) {
        val topicsIndexListReference = databaseRoot.getReferenceFromUrl(indexListLocationUrl)
        val topicsReference = databaseReference.child(getString(R.string.topics))

        val topicIndexAdapter = object: FirebaseObserverIndexRecyclerAdapter<Topic, ColoredHorizontalRecyclerViewHolder>(Topic::class.java, R.layout.list_item_colored_horizontal_recycler, ColoredHorizontalRecyclerViewHolder::class.java, topicsIndexListReference, topicsReference, this) {
            override fun populateViewHolder(topicViewHolder: ColoredHorizontalRecyclerViewHolder?, topicModel: Topic?, topicPosition: Int) {
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

    /**
     * This data observer will respond to subtopic load events and
     *  update the UI accordingly. It is used by subtopic adapters.
     */
    private fun getSubtopicDataObserverForViewHolder(topicViewHolder: ColoredHorizontalRecyclerViewHolder?, level: Int? = null) = object: FirebaseDataObserver {
        override fun onRequestNewData() {
            topicViewHolder?.progressBar?.setVisible()
        }

        override fun onDataLoaded() {
            topicViewHolder?.progressBar?.setVisibilityGone()
        }

        override fun onDataEmpty() {
            topicViewHolder?.horizontalRecyclerView?.setVisibilityGone()
            topicViewHolder?.emptyView?.setVisible()
            topicViewHolder?.emptyTextView?.setText(R.string.there_are_no_lessons_for_this_topic_yet)

            level?.let { topicViewHolder?.emptyTextView?.text = getString(R.string.no_lessons_at_level_yet, level) }
        }

        override fun onDataNonEmpty() {
            topicViewHolder?.horizontalRecyclerView?.setVisible()
            topicViewHolder?.emptyView?.setVisibilityGone()
        }
    }

    private fun populateUnfilteredTopicViewHolder(topicViewHolder: ColoredHorizontalRecyclerViewHolder?, topicModel: Topic?, topicPosition: Int, topicKey: String) {
        populateTopicViewHolder(topicViewHolder, topicModel, topicPosition)

        val featuredSubtopicQuery = databaseReference.child(getString(R.string.featured_subtopic_lesson_headers))
                .child(topicKey)

        setSubtopicRecyclerAdapterUnfiltered(topicViewHolder, topicModel?.name, featuredSubtopicQuery)
    }

    private fun populateLevelFilteredTopicViewHolder(topicViewHolder: ColoredHorizontalRecyclerViewHolder?, topicModel: Topic?, topicPosition: Int, topicKey: String, level: Int) {
        populateTopicViewHolder(topicViewHolder, topicModel, topicPosition)
        setSubtopicViewHolderRecyclerFilteredByLevel(topicViewHolder, level, topicModel?.name, topicKey)
    }

    private fun populateTopicViewHolder(topicViewHolder: ColoredHorizontalRecyclerViewHolder?, topicModel: Topic?, topicPosition: Int) {
        topicViewHolder?.titleTextView?.text = topicModel?.name

        // Alternate between these four colors:
        when (topicPosition % 4) {
            0 -> topicViewHolder?.itemView?.setBackgroundResource(R.color.seaTeal)
            1 -> topicViewHolder?.itemView?.setBackgroundResource(R.color.grassGreen)
            2 -> topicViewHolder?.itemView?.setBackgroundResource(R.color.bubbleGumPink)
            3 -> topicViewHolder?.itemView?.setBackgroundResource(R.color.dreamsicleOrange)
        }
    }

    private fun setSubtopicRecyclerAdapterUnfiltered(topicViewHolder: ColoredHorizontalRecyclerViewHolder?, topicName: String?, subtopicQuery: Query) {
        topicViewHolder?.horizontalRecyclerView?.setAdapter(SubtopicLessonHeaderRecyclerAdapter(topicName ?: "", subtopicQuery, this, getSubtopicDataObserverForViewHolder(topicViewHolder)))
    }

    private fun setSubtopicViewHolderRecyclerFilteredByLevel(topicViewHolder: ColoredHorizontalRecyclerViewHolder?, level: Int, topicName: String?, topicKey: String) {
        val boardLevelSubtopicsQuery = databaseReference.child(getString(R.string.boards))
                .child(prefs.getBoardKey())
                .child(getString(R.string.level_subtopics))
                .child(level.toString())

        val featuredSubtopicLessonHeaderReference = databaseReference.child(getString(R.string.featured_subtopic_lesson_headers)).child(topicKey)

        val adapter = object: FirebaseObserverIndexRecyclerAdapter<SubtopicLessonHeader, SubtopicHeaderViewHolder>(SubtopicLessonHeader::class.java, R.layout.list_item_lesson_header, SubtopicHeaderViewHolder::class.java, boardLevelSubtopicsQuery, featuredSubtopicLessonHeaderReference, getSubtopicDataObserverForViewHolder(topicViewHolder, level)) {
            override fun populateViewHolder(subtopicHeaderViewHolder: SubtopicHeaderViewHolder?, subtopicHeaderModel: SubtopicLessonHeader?, lessonHeaderPosition: Int) {
                subtopicHeaderViewHolder?.populateView(subtopicHeaderModel, topicName ?: "", this@TopicsListActivity, dateFormat)
            }
        }
        topicViewHolder?.horizontalRecyclerView?.setAdapter(adapter)
    }

    override fun onBackPressed() {
        if (intent.hasAllExtras()) {
            // If the topics list is displayed from a
            //  syllabus lesson plan, then pressing back
            //  should take us back to that activity.
            finish()
            overridePendingTransition(R.anim.fade_in_quick, R.anim.fade_out_quick)
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
