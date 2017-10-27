package org.jnanaprabodhini.happyteacher.activity

import android.content.Intent
import android.os.Bundle
import android.support.annotation.IntegerRes
import android.support.annotation.LayoutRes
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import android.widget.Spinner
import android.widget.TextView
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.Query
import kotlinx.android.synthetic.main.activity_topics_list.*
import kotlinx.android.synthetic.main.header_syllabus_lesson_topic.*
import org.jnanaprabodhini.happyteacher.R
import org.jnanaprabodhini.happyteacher.activity.base.BottomNavigationActivity
import org.jnanaprabodhini.happyteacher.adapter.firestore.FirestoreObserverListAdapter
import org.jnanaprabodhini.happyteacher.adapter.firestore.TopicsRecyclerAdapter
import org.jnanaprabodhini.happyteacher.adapter.helper.FirebaseDataObserver
import org.jnanaprabodhini.happyteacher.extension.*
import org.jnanaprabodhini.happyteacher.model.CardListContentHeader
import org.jnanaprabodhini.happyteacher.model.Subject
import org.jnanaprabodhini.happyteacher.model.Topic

class TopicsListActivity : BottomNavigationActivity(), FirebaseDataObserver {

    companion object IntentExtraHelper {
        fun launchActivity(from: BottomNavigationActivity, syllabusLessonId: String, subjectName: String, lessonTitle: String, level: Int) {
            val topicsListIntent = Intent(from, TopicsListActivity::class.java)
            topicsListIntent.apply{
                putExtra(SYLLABUS_LESSON_ID, syllabusLessonId)
                putExtra(SUBJECT_NAME, subjectName)
                putExtra(LESSON_TITLE, lessonTitle)
                putExtra(LEVEL, level)
            }

            from.startBottomNavigationActivityWithFade(topicsListIntent)
        }

        const val SYLLABUS_LESSON_ID: String = "SYLLABUS_LESSON_ID"
        fun Intent.hasSyllabusLessonId(): Boolean = hasExtra(SYLLABUS_LESSON_ID)
        fun Intent.getSyllabusLessonId(): String = getStringExtra(SYLLABUS_LESSON_ID)

        const val SUBJECT_NAME: String = "SUBJECT_NAME"
        fun Intent.hasSubject(): Boolean = hasExtra(SUBJECT_NAME)
        fun Intent.getSubject(): String = getStringExtra(SUBJECT_NAME)

        const val LEVEL: String = "LEVEL"
        fun Intent.hasLevel(): Boolean = hasExtra(LEVEL)
        fun Intent.getLevel(): Int = getIntExtra(LEVEL, 0)

        const val LESSON_TITLE: String = "LESSON_TITLE"
        fun Intent.hasLessonTitle(): Boolean = hasExtra(LESSON_TITLE)
        fun Intent.getLessonTitle(): String = getStringExtra(LESSON_TITLE)

        fun Intent.hasAllExtras(): Boolean = hasSyllabusLessonId() && hasSubject() && hasLevel() && hasLessonTitle()
    }

    object SavedInstanceStateConstants {
        const val PARENT_SUBJECT_SPINNER_SELECTION = "PARENT_SUBJECT_SPINNER_SELECTION"
        const val CHILD_SUBJECT_SPINNER_SELECTION = "CHILD_SUBJECT_SPINNER_SELECTION"
    }

    private var parentSubjectSelectionIndex = 0
    private var childSubjectSelectionIndex = 0

    @IntegerRes override val bottomNavigationMenuItemId: Int = R.id.navigation_topics

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)
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

    private fun initializeUiFromIntent() {
        if (intent.hasAllExtras()) {
            // Show topics related to the given syllabus lesson plan
            val syllabusLessonId = intent.getSyllabusLessonId()
            val subject = intent.getSubject()
            val level = intent.getLevel()
            val title = intent.getLessonTitle()

            showSyllabusLessonTopicHeader(title, subject, level)
            updateListOfTopicsForSyllabusLesson(syllabusLessonId)
        } else {
            initializeTopicListForSubject()
        }
    }

    private fun initializeTopicListForSubject() {
        hideSyllabusLessonTopicHeader()
        topicsProgressBar.setVisible()

        // Show all topics for a subject, selected by spinner
        setupParentSubjectSpinner()
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

    private fun setupParentSubjectSpinner() {
        // Parent subjects have no parents, so pass null as value for parentSubject:
        setupSpinner(parentSubjectSpinner, R.layout.spinner_item, null, parentSubjectSelectionIndex)
    }

    /**
     *  Set up one of the two spinners (the subject spinner or the sub-subject spinner).
     *
     *  The subject spinner is shown when this activity is not being used
     *   to display topics relevant to a specific syllabus lesson plan.
     */
    private fun setupSpinner(spinner: Spinner, @LayoutRes spinnerLayout: Int, parentSubjectId: String?, selectionIndex: Int) {
        val subjectQuery = firestoreLocalized.collection(getString(R.string.subjects)).whereEqualTo(getString(R.string.parent_subject), parentSubjectId)

        val spinnerDataObserver = object: FirebaseDataObserver {
            override fun onDataNonEmpty() {
                topicsProgressBar.setVisibilityGone()
                spinner.setVisible()
            }
        }

        val adapter = object: FirestoreObserverListAdapter<Subject>(subjectQuery, Subject::class.java, spinnerLayout, spinnerDataObserver, this) {
            override fun populateView(view: View, model: Subject) {
                (view as TextView).text = model.name
            }
        }

        adapter.startListening()
        spinner.adapter = adapter

        spinner.selectIndexWhenPopulated(selectionIndex)

        spinner.onItemSelected { position ->
            val subject = adapter.getItem(position)
            val selectedSubjectKey = adapter.getItemKey(position)

            if (subject.hasChildren) {
                setupSpinner(childSubjectSpinner, R.layout.spinner_item_child, selectedSubjectKey, childSubjectSelectionIndex)
            } else if (!subject.hasChildren && spinner == childSubjectSpinner) {
                updateListOfTopicsForSubject(selectedSubjectKey)
            } else {
                updateListOfTopicsForSubject(selectedSubjectKey)
                childSubjectSpinner.setVisibilityGone()
            }
        }
    }

    /**
     *  Display list of topics for the selected subject.
     */
    private fun updateListOfTopicsForSubject(subjectKey: String) {
        val topicQuery = firestoreLocalized.collection(getString(R.string.topics))
                .whereEqualTo(getString(R.string.subject), subjectKey) // todo: ordering

        val topicAdapterOptions = FirestoreRecyclerOptions.Builder<Topic>()
                .setQuery(topicQuery, Topic::class.java).build()

        val topicAdapter = object: TopicsRecyclerAdapter(topicAdapterOptions, this, this) {
            override fun getSubtopicAdapterOptions(topicId: String): FirestoreRecyclerOptions<CardListContentHeader> {
                val query: Query = firestoreLocalized.collection(getString(R.string.lessons))
                        .whereEqualTo(getString(R.string.topic), topicId)
                        .whereEqualTo(getString(R.string.is_featured), true)

                return FirestoreRecyclerOptions.Builder<CardListContentHeader>().setQuery(query, CardListContentHeader::class.java).build()
            }
        }

        topicAdapter.startListening()

        topicsRecyclerView.adapter = topicAdapter
    }

    private fun updateListOfTopicsForSyllabusLesson(syllabusLessonId: String) {
        val topicsQuery = firestoreLocalized.collection(getString(R.string.topics))
                .whereEqualTo("syllabus_lessons.$syllabusLessonId", true)

        val topicsAdapterOptions = FirestoreRecyclerOptions.Builder<Topic>()
                .setQuery(topicsQuery, Topic::class.java).build()

        val adapter = object: TopicsRecyclerAdapter(topicsAdapterOptions, this, this) {
            override fun getSubtopicAdapterOptions(topicId: String): FirestoreRecyclerOptions<CardListContentHeader> {
                val subtopicQuery = firestoreLocalized.collection(getString(R.string.lessons))
                        .whereEqualTo(getString(R.string.topic), topicId)
                        .whereEqualTo("syllabus_lessons.$syllabusLessonId", true)
                        .whereEqualTo(getString(R.string.is_featured), true)

                return FirestoreRecyclerOptions.Builder<CardListContentHeader>()
                        .setQuery(subtopicQuery, CardListContentHeader::class.java).build()
            }
        }
        adapter.startListening()

        topicsRecyclerView.adapter = adapter
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

        val standardString = getString(R.string.standard_n, standard)
        syllabusLessonSubjectStandardTextView.text = "$subject, $standardString"
    }

    private fun hideSyllabusLessonTopicHeader() {
        syllabusLessonTopicsHeaderView.setVisibilityGone()
    }

    private fun hideSpinners() {
        parentSubjectSpinner.setVisibilityGone()
        childSubjectSpinner.setVisibilityGone()
    }

    override fun onRequestNewData() {
        topicsRecyclerView.setVisibilityGone()
        topicsProgressBar.setVisible()
        statusTextView.setVisibilityGone()
    }

    override fun onDataLoaded() {
        topicsProgressBar.setVisibilityGone()
    }

    override fun onDataEmpty() {
        topicsRecyclerView.setVisibilityGone()
        statusTextView.setVisible()
        statusTextView.setText(R.string.there_are_no_topics_for_this_subject_yet)
    }

    override fun onDataNonEmpty() {
        topicsRecyclerView.setVisible()
        statusTextView.setVisibilityGone()

        // Animate layout changes
        topicsRecyclerView.scheduleLayoutAnimation()
        topicsRecyclerView.invalidate()
    }

    override fun onError(e: FirebaseFirestoreException?) {
        topicsRecyclerView.setVisibilityGone()
        topicsProgressBar.setVisibilityGone()
        statusTextView.setVisible()
        statusTextView.setText(R.string.there_was_an_error_loading_topics_for_this_subject)
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
