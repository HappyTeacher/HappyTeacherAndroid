package org.jnanaprabodhini.happyteacher.activity

import android.content.Intent
import android.os.Bundle
import android.support.annotation.IntegerRes
import android.support.v4.content.res.ResourcesCompat
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import android.widget.AdapterView
import android.widget.TextView
import com.firebase.ui.database.FirebaseIndexListAdapter
import com.firebase.ui.database.FirebaseListAdapter
import kotlinx.android.synthetic.main.activity_board_lessons.*
import org.jnanaprabodhini.happyteacher.BoardChoiceDialog
import org.jnanaprabodhini.happyteacher.DataObserver
import org.jnanaprabodhini.happyteacher.R
import org.jnanaprabodhini.happyteacher.activity.parent.BottomNavigationActivity
import org.jnanaprabodhini.happyteacher.adapter.FirebaseDataObserverRecyclerAdapter
import org.jnanaprabodhini.happyteacher.extension.jiggle
import org.jnanaprabodhini.happyteacher.extension.setVisibilityGone
import org.jnanaprabodhini.happyteacher.extension.setVisible
import org.jnanaprabodhini.happyteacher.extension.showSnackbar
import org.jnanaprabodhini.happyteacher.model.Subject
import org.jnanaprabodhini.happyteacher.model.SyllabusLesson
import org.jnanaprabodhini.happyteacher.prefs
import org.jnanaprabodhini.happyteacher.viewholder.SyllabusLessonViewHolder


class BoardLessonsActivity : BottomNavigationActivity(), DataObserver {

    @IntegerRes override val bottomNavigationMenuItemId: Int = R.id.navigation_board

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_board_lessons)

        bottomNavigation.selectedItemId = bottomNavigationMenuItemId
        bottomNavigation.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener)

        val layoutManager = LinearLayoutManager(this)
        syllabusLessonsRecyclerView.layoutManager = layoutManager

        // Leave space between each item in the list:
        val dividerItemDecoration = DividerItemDecoration(this, layoutManager.orientation)
        dividerItemDecoration.setDrawable(ResourcesCompat.getDrawable(resources, R.drawable.divider_vertical, null)!!)
        syllabusLessonsRecyclerView.addItemDecoration(dividerItemDecoration)

        initializeSpinners()

        if (!prefs.hasChosenBoard()) {
            // Prompt the user to select which board they would like
            //  to see syllabus lesson plans from.
            showBoardChooser()
        }
    }

    override fun onBottomNavigationItemReselected() {
        syllabusLessonsRecyclerView.smoothScrollToPosition(0)
    }

    private fun showBoardChooser() {
        val dialog = BoardChoiceDialog(this)
        dialog.setOnDismissListener {
            // Re-initialize spinners after board is chosen.
            initializeSpinners()
        }
        dialog.show()
    }

    private fun initializeSpinners() {
        val boardSubjectKeyQuery = databaseReference.child(getString(R.string.boards))
                                                    .child(prefs.getBoardKey())
                                                    .child(getString(R.string.subjects))

        val subjectRef = databaseReference.child(getString(R.string.subjects))

        val boardSubjectSpinnerAdapter = object : FirebaseIndexListAdapter<Subject>(this, Subject::class.java, R.layout.spinner_item, boardSubjectKeyQuery, subjectRef) {
            override fun populateView(view: View, subject: Subject, position: Int) {
                (view as TextView).text = subject.name
            }
        }

        val boardLevelKeyQuery = databaseReference.child(getString(R.string.boards))
                .child(prefs.getBoardKey())
                .child(getString(R.string.levels))

        val levelRef = databaseReference.child(getString(R.string.levels))

        val boardLevelSpinnerAdapter = object : FirebaseIndexListAdapter<Int>(this, Int::class.java, R.layout.spinner_item, boardLevelKeyQuery, levelRef) {
            override fun populateView(view: View, level: Int, position: Int) {
                (view as TextView).text = getString(R.string.standard_n, level)
            }
        }

        subjectSpinner.adapter = boardSubjectSpinnerAdapter
        levelSpinner.adapter = boardLevelSpinnerAdapter

        val spinnerSelectionListener = object: AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if (!boardSubjectSpinnerAdapter.isEmpty && !boardLevelSpinnerAdapter.isEmpty) {
                    val selectedSubjectPosition = subjectSpinner.selectedItemPosition
                    val selectedLevelPosition = levelSpinner.selectedItemPosition

                    if (selectedSubjectPosition != AdapterView.INVALID_POSITION && selectedLevelPosition != AdapterView.INVALID_POSITION) {
                        val selectedSubjectKey = boardSubjectSpinnerAdapter.getRef(selectedSubjectPosition).key
                        val selectedLevel = boardLevelSpinnerAdapter.getRef(selectedLevelPosition).key

                        updateSyllabusLessonList(selectedSubjectKey, selectedLevel)
                    }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        subjectSpinner.onItemSelectedListener = spinnerSelectionListener
        levelSpinner.onItemSelectedListener = spinnerSelectionListener
    }

    private fun  updateSyllabusLessonList(selectedSubjectKey: String, selectedLevel: String) {
        onRequestNewData()

        val syllabusLessonQuery = databaseReference.child(getString(R.string.syllabus_lessons))
                .child(prefs.getBoardKey())
                .child(selectedSubjectKey)
                .child(selectedLevel)
                .orderByChild(getString(R.string.lesson_number))

        val syllabusLessonAdapter = object: FirebaseDataObserverRecyclerAdapter<SyllabusLesson, SyllabusLessonViewHolder>(SyllabusLesson::class.java, R.layout.list_item_syllabus_lesson, SyllabusLessonViewHolder::class.java, syllabusLessonQuery, this) {

            override fun populateViewHolder(syllabusLessonViewHolder: SyllabusLessonViewHolder?, syllabusLessonModel: SyllabusLesson?, syllabusLessonPosition: Int) {
                syllabusLessonViewHolder?.lessonTitleTextView?.text = syllabusLessonModel?.name
                syllabusLessonViewHolder?.lessonNumberTextView?.text = syllabusLessonModel?.lessonNumber.toString()
                syllabusLessonViewHolder?.topicCountTextView?.text = resources.getQuantityString(R.plurals.topics_count, syllabusLessonModel?.topicCount ?: 0, syllabusLessonModel?.topicCount ?: 0)

                syllabusLessonViewHolder?.itemView?.setOnClickListener {

                    val topicCount = syllabusLessonModel?.topicCount ?: 0
                    if (topicCount == 0) {
                        // If there are no topics to display, jiggle the count and tell the user.
                        syllabusLessonViewHolder.topicCountTextView.jiggle()
                        rootLayout.showSnackbar(R.string.this_lesson_has_no_relevant_topics)
                    } else {
                        // Pass syllabus lesson data to the TopicsListActivity
                        //  so that it can display the relevant topics (instead
                        //  of all topics for that subject).

                        val topicsListIntent = Intent(this@BoardLessonsActivity, TopicsListActivity::class.java)
                        val keyUrl = getRef(syllabusLessonPosition).child(getString(R.string.topics)).toString()
                        val subject = syllabusLessonModel?.subject
                        val level = syllabusLessonModel?.level
                        val title = syllabusLessonModel?.name

                        topicsListIntent.putExtra(TopicsListActivity.EXTRA_TOPICS_KEY_URL, keyUrl)
                        topicsListIntent.putExtra(TopicsListActivity.EXTRA_SUBJECT_NAME, subject)
                        topicsListIntent.putExtra(TopicsListActivity.EXTRA_LESSON_TITLE, title)
                        topicsListIntent.putExtra(TopicsListActivity.EXTRA_LEVEL, level)

                        startActivity(topicsListIntent)
                    }
                }
            }
        }

        syllabusLessonsRecyclerView.adapter = syllabusLessonAdapter
    }

    override fun onRequestNewData() {
        emptySyllabusLessonsTextView.setVisibilityGone()
        boardLessonsProgressBar.setVisible()
    }

    override fun onDataLoaded() {
        boardLessonsProgressBar.setVisibilityGone()
    }

    override fun onDataEmpty() {
        // Show empty view
        emptySyllabusLessonsTextView.setVisible()
    }

    override fun onDataNonEmpty() {
        // Hide empty view
        emptySyllabusLessonsTextView.setVisibilityGone()

        // Animate layout changes
        syllabusLessonsRecyclerView.scheduleLayoutAnimation()
        syllabusLessonsRecyclerView.invalidate()
    }
}

