package org.jnanaprabodhini.happyteacher.activity

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import android.widget.AdapterView
import android.widget.TextView
import com.firebase.ui.database.FirebaseListAdapter
import com.firebase.ui.database.FirebaseRecyclerAdapter
import kotlinx.android.synthetic.main.activity_board_lessons.*
import org.jnanaprabodhini.happyteacher.R
import org.jnanaprabodhini.happyteacher.extension.getPrimaryLanguageCode
import org.jnanaprabodhini.happyteacher.extension.getPrimaryLocale
import org.jnanaprabodhini.happyteacher.model.Subject
import org.jnanaprabodhini.happyteacher.model.SyllabusLesson
import org.jnanaprabodhini.happyteacher.prefs
import org.jnanaprabodhini.happyteacher.viewholder.SyllabusLessonViewHolder


class BoardLessonsActivity : HappyTeacherActivity() {

    private val onNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_board -> {
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_topics -> {
                val topicsActivityIntent = Intent(this, TopicsListActivity::class.java)
                startActivity(topicsActivityIntent)
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_contribute -> {
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_board_lessons)

        bottomNavigation.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener)
        bottomNavigation.selectedItemId = R.id.navigation_board

        val layoutManager = LinearLayoutManager(this)
        syllabusLessonsRecyclerView.layoutManager = layoutManager

        val dividerItemDecoration = DividerItemDecoration(this, layoutManager.orientation)
        syllabusLessonsRecyclerView.addItemDecoration(dividerItemDecoration)

        initializeSpinners()
    }

    private fun initializeSpinners() {
        val subjectQuery = databaseInstance.getReference(getString(R.string.subjects))
                .orderByChild(getString(R.string.is_active))
                .equalTo(true)

        val subjectAdapter = object : FirebaseListAdapter<Subject>(this, Subject::class.java, R.layout.spinner_item, subjectQuery) {
            override fun populateView(view: View, subject: Subject, position: Int) {
                val languageCode = getPrimaryLocale().language
                (view as TextView).text = subject.names[languageCode]
            }
        }

        val levelQuery = databaseInstance.getReference(getString(R.string.levels))

        val levelAdapter = object : FirebaseListAdapter<Boolean>(this, Boolean::class.java, R.layout.spinner_item, levelQuery) {
            override fun populateView(view: View, level: Boolean, position: Int) {
                try {
                    val levelNumber = Integer.parseInt(this.getRef(position).key)
                    (view as TextView).text = getString(R.string.standard_n, levelNumber) // key = level number
                } catch (e: NumberFormatException) {
                    (view as TextView).text = this.getRef(position).key
                }
            }
        }

        subjectSpinner.adapter = subjectAdapter
        levelSpinner.adapter = levelAdapter

        val spinnerSelectionListener = object: AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedSubjectPosition = subjectSpinner.selectedItemPosition
                var selectedLevelPosition = levelSpinner.selectedItemPosition

                if (selectedLevelPosition < 0) {selectedLevelPosition = 0} // todo fix this ya know

                val selectedSubjectKey = subjectAdapter.getRef(selectedSubjectPosition).key
                val selectedLevel = levelAdapter.getRef(selectedLevelPosition).key

                updateSyllabusLessonList(selectedSubjectKey, selectedLevel)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        subjectSpinner.onItemSelectedListener = spinnerSelectionListener
        levelSpinner.onItemSelectedListener = spinnerSelectionListener
    }

    private fun  updateSyllabusLessonList(selectedSubjectKey: String, selectedLevel: String) {
        val syllabusLessonQuery = databaseInstance.getReference(getString(R.string.syllabus_lessons))
                .child(prefs.getBoardKey())
                .child(selectedSubjectKey)
                .child(selectedLevel)
                .orderByChild(getString(R.string.lesson_number))

        val syllabusLessonAdapter = object: FirebaseRecyclerAdapter<SyllabusLesson, SyllabusLessonViewHolder>(SyllabusLesson::class.java, R.layout.list_item_syllabus_lesson, SyllabusLessonViewHolder::class.java, syllabusLessonQuery) {
            override fun populateViewHolder(syllabusLessonViewHolder: SyllabusLessonViewHolder?, syllabusLessonModel: SyllabusLesson?, syllabusLessonPosition: Int) {
                syllabusLessonViewHolder?.lessonTitleTextView?.text = syllabusLessonModel?.names?.get(getPrimaryLanguageCode())
                syllabusLessonViewHolder?.lessonNumberTextView?.text = syllabusLessonModel?.lessonNumber.toString()
                syllabusLessonViewHolder?.topicCountTextView?.text = resources.getQuantityString(R.plurals.topics_count, syllabusLessonModel?.topicCount ?: 0, syllabusLessonModel?.topicCount ?: 0)
            }
        }

        syllabusLessonsRecyclerView.adapter = syllabusLessonAdapter
    }

    // Remove transition for this activity to avoid bottom navigation jumpiness.
    public override fun onPause() {
        super.onPause()
        overridePendingTransition(0, 0)
    }
}

