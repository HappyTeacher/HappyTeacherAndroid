package org.jnanaprabodhini.happyteacher.activity.parent

import android.content.Context
import android.support.annotation.IntegerRes
import android.support.v7.app.AppCompatActivity
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_board_lessons.*
import org.jnanaprabodhini.happyteacher.R
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper

/**
 * Created by grahamearley on 9/12/17.
 */
abstract class HappyTeacherActivity: AppCompatActivity() {
    val databaseInstance: FirebaseDatabase by lazy {
        FirebaseDatabase.getInstance()
    }

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase))
    }
}