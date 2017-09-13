package org.jnanaprabodhini.happyteacher.activity

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.support.design.widget.BottomNavigationView
import android.view.View
import android.widget.AdapterView
import android.widget.TextView
import com.firebase.ui.database.FirebaseListAdapter
import kotlinx.android.synthetic.main.activity_board_lessons.*

import org.jnanaprabodhini.happyteacher.R
import org.jnanaprabodhini.happyteacher.extension.getPrimaryLocale
import org.jnanaprabodhini.happyteacher.model.Subject

class BoardLessonsActivity : HappyTeacherActivity() {

    private val onNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_board -> {
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_topics -> {
                // Delay transition to allow BottomNav animation to complete
                Handler().postDelayed({
                    val topicsActivityIntent = Intent(this, TopicsListActivity::class.java)
                    startActivity(topicsActivityIntent)
                }, 300)

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
    }

    private fun setupSubjectSpinner() {
        val subjectQuery = databaseInstance.getReference(getString(R.string.subjects))
                .orderByChild(getString(R.string.is_active))
                .equalTo(true)

        val subjectAdapter = object : FirebaseListAdapter<Subject>(this, Subject::class.java, R.layout.spinner_item, subjectQuery) {
            override fun populateView(view: View, subject: Subject, position: Int) {
                val languageCode = getPrimaryLocale().language
                (view as TextView).text = subject.names[languageCode]
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

    // Remove transition for this activity to avoid bottom navigation jumpiness.
    public override fun onPause() {
        super.onPause()
        overridePendingTransition(0, 0)
    }
}
