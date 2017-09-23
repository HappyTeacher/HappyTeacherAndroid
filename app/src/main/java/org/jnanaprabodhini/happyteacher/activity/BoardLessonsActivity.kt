package org.jnanaprabodhini.happyteacher.activity

import android.content.Intent
import android.os.Bundle
import android.support.annotation.IntegerRes
import android.support.v4.content.res.ResourcesCompat
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import android.widget.TextView
import com.firebase.ui.database.FirebaseIndexListAdapter
import kotlinx.android.synthetic.main.activity_board_lessons.*
import org.jnanaprabodhini.happyteacher.BoardChoiceDialog
import org.jnanaprabodhini.happyteacher.DataObserver
import org.jnanaprabodhini.happyteacher.R
import org.jnanaprabodhini.happyteacher.activity.parent.BottomNavigationActivity
import org.jnanaprabodhini.happyteacher.adapter.FirebaseDataObserverRecyclerAdapter
import org.jnanaprabodhini.happyteacher.adapter.FirebaseIndexDataObserverListAdapter
import org.jnanaprabodhini.happyteacher.extension.*
import org.jnanaprabodhini.happyteacher.model.Subject
import org.jnanaprabodhini.happyteacher.model.SyllabusLesson
import org.jnanaprabodhini.happyteacher.prefs
import org.jnanaprabodhini.happyteacher.viewholder.SyllabusLessonViewHolder


class BoardLessonsActivity : BottomNavigationActivity(), DataObserver {

    @IntegerRes override val bottomNavigationMenuItemId: Int = R.id.navigation_board

    object SavedInstanceStateConstants {
        val LEVEL_SPINNER_SELECTION = "LEVEL_SPINNER_SELECTION"
        val SUBJECT_SPINNER_SELECTION = "SUBJECT_SPINNER_SELECTION"
    }

    private var levelSpinnerSelectionIndex = 0
    private var subjectSpinnerSelectionIndex = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_board_lessons)

        bottomNavigation.selectedItemId = bottomNavigationMenuItemId
        bottomNavigation.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener)

        setSpinnerSelectionIndicesFromSavedInstanceState(savedInstanceState)
        setupRecyclerView()
        setupSubjectSpinner()

        if (!prefs.hasChosenBoard()) {
            // Prompt the user to select which board they would like
            //  to see syllabus lesson plans from.
            showBoardChooser()
        }
    }

    private fun showBoardChooser() {
        val dialog = BoardChoiceDialog(this)
        dialog.setOnDismissListener {
            // Re-initialize spinners after board is chosen.
            setupSubjectSpinner()
        }
        dialog.show()
    }

    private fun setSpinnerSelectionIndicesFromSavedInstanceState(savedInstanceState: Bundle?) {
        if (savedInstanceState == null) return

        val levelSpinnerStoredSelection = savedInstanceState.getInt(SavedInstanceStateConstants.LEVEL_SPINNER_SELECTION, 0)
        val subjectSpinnerStoredSelection = savedInstanceState.getInt(SavedInstanceStateConstants.SUBJECT_SPINNER_SELECTION, 0)

        this.levelSpinnerSelectionIndex = levelSpinnerStoredSelection
        this.subjectSpinnerSelectionIndex = subjectSpinnerStoredSelection
    }

    private fun setupRecyclerView() {
        val layoutManager = LinearLayoutManager(this)
        syllabusLessonsRecyclerView.layoutManager = layoutManager

        // Leave space between each item in the list:
        val dividerItemDecoration = DividerItemDecoration(this, layoutManager.orientation)
        dividerItemDecoration.setDrawable(ResourcesCompat.getDrawable(resources, R.drawable.divider_vertical, null)!!)
        syllabusLessonsRecyclerView.addItemDecoration(dividerItemDecoration)
    }

    override fun onBottomNavigationItemReselected() {
        syllabusLessonsRecyclerView.smoothScrollToPosition(0)
    }

    private fun setupSubjectSpinner() {
        // Get an index list of subjects that are used by the currently active board:
        val boardSubjectIndexQuery = databaseReference.child(getString(R.string.boards))
                .child(prefs.getBoardKey())
                .child(getString(R.string.subjects))

        val subjectRef = databaseReference.child(getString(R.string.subjects))

        val boardSubjectSpinnerAdapter = object : FirebaseIndexListAdapter<Subject>(this, Subject::class.java, R.layout.spinner_item, boardSubjectIndexQuery, subjectRef) {
            override fun populateView(view: View, subject: Subject, position: Int) {
                (view as TextView).text = subject.name
            }
        }

        // The level spinner depends on what subject is selected:
        subjectSpinner.onItemSelected { pos -> setupLevelSpinnerForSubject(boardSubjectSpinnerAdapter.getRef(pos).key) }

        subjectSpinner.adapter = boardSubjectSpinnerAdapter
        subjectSpinner.selectIndexWhenPopulated(subjectSpinnerSelectionIndex)
    }

    private fun setupLevelSpinnerForSubject(subjectKey: String) {
        val previousSelection = levelSpinner.selectedItem

        val levelRef = databaseReference.child(getString(R.string.levels))

        // Get an index list of levels that are used by the currently active board:
        val boardLevelIndexQuery = databaseReference.child(getString(R.string.boards))
                                                    .child(prefs.getBoardKey())
                                                    .child(getString(R.string.subjects))
                                                    .child(subjectKey)

        // Observe data in this spinner, and if the previously selected item
        //  is loaded into the data, then select that item.
        val levelDataObserver = object: DataObserver {
            override fun onDataNonEmpty() {
                if (previousSelection != null && previousSelection is Int) {
                    val indexOfPreviousSelection = levelSpinner.items().indexOf(previousSelection)
                    if (indexOfPreviousSelection != -1) {
                        levelSpinner.setSelection(indexOfPreviousSelection, true)
                    }
                }
            }
        }

        val boardLevelSpinnerAdapter = object : FirebaseIndexDataObserverListAdapter<Int>(this, Int::class.java, R.layout.spinner_item, boardLevelIndexQuery, levelRef, levelDataObserver) {
            override fun populateView(view: View, level: Int, position: Int) {
                (view as TextView).text = getString(R.string.standard_n, level)
            }
        }

        // Once a level is selected, we can update the list of lessons
        levelSpinner.onItemSelected { pos -> updateSyllabusLessonList(subjectKey, boardLevelSpinnerAdapter.getRef(pos).key) }

        levelSpinner.adapter = boardLevelSpinnerAdapter
        levelSpinner.selectIndexWhenPopulated(levelSpinnerSelectionIndex)
    }

    private fun updateSyllabusLessonList(selectedSubjectKey: String, selectedLevel: String) {
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

                        topicsListIntent.putExtra(TopicsListActivity.TOPICS_INDEX_LIST_URL, keyUrl)
                        topicsListIntent.putExtra(TopicsListActivity.SUBJECT_NAME, subject)
                        topicsListIntent.putExtra(TopicsListActivity.LESSON_TITLE, title)
                        topicsListIntent.putExtra(TopicsListActivity.LEVEL, level)

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

    override fun onSaveInstanceState(savedInstanceState: Bundle) {
        val levelSpinnerSelectionIndex = levelSpinner.selectedItemPosition
        val subjectSpinnerSelectionIndex = subjectSpinner.selectedItemPosition

        savedInstanceState.putInt(SavedInstanceStateConstants.LEVEL_SPINNER_SELECTION, levelSpinnerSelectionIndex)
        savedInstanceState.putInt(SavedInstanceStateConstants.SUBJECT_SPINNER_SELECTION, subjectSpinnerSelectionIndex)

        super.onSaveInstanceState(savedInstanceState)
    }
}

