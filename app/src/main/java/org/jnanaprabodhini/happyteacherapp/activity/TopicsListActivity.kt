package org.jnanaprabodhini.happyteacherapp.activity

import android.content.Intent
import android.os.Bundle
import android.support.annotation.IntegerRes
import android.support.v7.widget.LinearLayoutManager
import android.widget.ProgressBar
import android.widget.Spinner
import android.widget.TextView
import com.crashlytics.android.Crashlytics
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks
import com.google.firebase.firestore.FirebaseFirestoreException
import kotlinx.android.synthetic.main.activity_topics_list.*
import kotlinx.android.synthetic.main.header_syllabus_lesson_topic.*
import kotlinx.android.synthetic.main.stacked_subject_spinners.*
import org.jnanaprabodhini.happyteacherapp.R
import org.jnanaprabodhini.happyteacherapp.activity.base.BottomNavigationActivity
import org.jnanaprabodhini.happyteacherapp.adapter.firestore.TopicContentRecyclerAdapter
import org.jnanaprabodhini.happyteacherapp.adapter.helper.FirebaseDataObserver
import org.jnanaprabodhini.happyteacherapp.extension.isVisible
import org.jnanaprabodhini.happyteacherapp.extension.setVisibilityGone
import org.jnanaprabodhini.happyteacherapp.extension.setVisible
import org.jnanaprabodhini.happyteacherapp.extension.showToast
import org.jnanaprabodhini.happyteacherapp.model.ResourceHeader
import org.jnanaprabodhini.happyteacherapp.model.Topic
import org.jnanaprabodhini.happyteacherapp.util.ResourceStatus
import org.jnanaprabodhini.happyteacherapp.view.SubjectSpinnerRecyclerView
import org.jnanaprabodhini.happyteacherapp.view.manager.PublishedLessonTopicListManager
import org.jnanaprabodhini.happyteacherapp.view.manager.SubjectSpinnerManager


class TopicsListActivity : BottomNavigationActivity(), FirebaseDataObserver, SubjectSpinnerRecyclerView {

    companion object IntentExtraHelper {
        fun launch(from: BottomNavigationActivity, syllabusLessonId: String, subjectName: String, lessonTitle: String, level: Int) {
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

    override val parentSpinner: Spinner by lazy { parentSubjectSpinner }
    override val childSpinner: Spinner by lazy { childSubjectSpinner }
    override val statusText: TextView by lazy { statusTextView }
    override val progressBar: ProgressBar by lazy { topicsProgressBar }

    private val subjectSpinnerManager = SubjectSpinnerManager(view = this, activity = this)

    @IntegerRes override val bottomNavigationMenuItemId: Int = R.id.navigation_topics

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_topics_list)

        topicsRecyclerView.layoutManager = LinearLayoutManager(this)
        bottomNavigation.selectedItemId = bottomNavigationMenuItemId
        bottomNavigation.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener)

        openDeepLinkIfAvailable()

        subjectSpinnerManager.restoreSpinnerState(savedInstanceState)

        initializeUiFromIntent()
    }

    private fun initializeUiFromIntent() {
        if (intent.hasAllExtras()) {
            // Show topics related to the given syllabus lesson plan
            val syllabusLessonId = intent.getSyllabusLessonId()
            val subject = intent.getSubject()
            val level = intent.getLevel()
            val title = intent.getLessonTitle()

            showSyllabusLessonTopicHeader(title, subject, level)
            updateListOfTopicsForSyllabusLesson(syllabusLessonId, level)
        } else {
            initializeTopicListForSubject()
        }
    }

    private fun initializeTopicListForSubject() {
        hideSyllabusLessonTopicHeader()
        topicsProgressBar.setVisible()

        val topicListManager = PublishedLessonTopicListManager(topicsRecyclerView, this, this)
        subjectSpinnerManager.initializeWithTopicsListManager(topicListManager)
    }

    override fun onBottomNavigationItemReselected() {
        if (syllabusLessonTopicsHeaderView.isVisible()) {
            // Reset to subject spinner view:
            initializeTopicListForSubject()
        } else {
            // Scroll to top of topic list:
            topicsRecyclerView.smoothScrollToPosition(0)
        }
    }

    private fun updateListOfTopicsForSyllabusLesson(syllabusLessonId: String, standardLevel: Int) {
        val topicsQuery = firestoreLocalized.collection(getString(R.string.topics))
                .whereEqualTo("syllabus_lessons.$syllabusLessonId", true)

        val topicsAdapterOptions = FirestoreRecyclerOptions.Builder<Topic>()
                .setQuery(topicsQuery, Topic::class.java).build()

        val adapter = object: TopicContentRecyclerAdapter(topicsAdapterOptions, showSubmissionCount = true,
                topicsDataObserver = this, activity = this, standardLevel = standardLevel) {
            override fun getSubtopicAdapterOptions(topicId: String): FirestoreRecyclerOptions<ResourceHeader> {
                val subtopicQuery = firestoreLocalized.collection(getString(R.string.resources))
                        .whereEqualTo(getString(R.string.resource_type), getString(R.string.lesson))
                        .whereEqualTo(getString(R.string.topic), topicId)
                        .whereEqualTo("syllabus_lessons.$syllabusLessonId", true)
                        .whereEqualTo(getString(R.string.status), ResourceStatus.PUBLISHED)
                        .whereEqualTo(getString(R.string.is_featured), true)

                return FirestoreRecyclerOptions.Builder<ResourceHeader>()
                        .setQuery(subtopicQuery, ResourceHeader::class.java).build()
            }
        }
        adapter.startListening()

        topicsRecyclerView.adapter = adapter
    }

    private fun openDeepLinkIfAvailable() {
        FirebaseDynamicLinks.getInstance()
            .getDynamicLink(intent)
            .addOnSuccessListener(this) { pendingDynamicLinkData ->
                // pendingDynamicLinkData will be null if there is no link in the intent
                pendingDynamicLinkData?.let {
                    showToast(R.string.opening_resource_from_link)
                    val deepLink = pendingDynamicLinkData.link
                    val resourceRefPath = deepLink.encodedPath.removePrefix("/")

                    try {
                        val resourceRef = firestoreRoot.document(resourceRefPath)
                        ResourceViewerActivity.launchViewerForResource(this, resourceRef)
                    } catch (e: IllegalArgumentException) {
                        Crashlytics.setString("malformed_deeplink", deepLink.toString())
                        Crashlytics.logException(e)
                        showToast(R.string.error_loading_link)
                    }
                }
            }
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
        subjectSpinnerManager.saveSpinnerState(savedInstanceState)
        super.onSaveInstanceState(savedInstanceState)
    }
}
