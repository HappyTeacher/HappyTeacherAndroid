package org.jnanaprabodhini.happyteacher

import android.content.Context
import android.support.v7.app.AppCompatActivity
import com.google.firebase.database.FirebaseDatabase
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper

/**
 * Created by grahamearley on 9/12/17.
 */
open class HappyTeacherActivity: AppCompatActivity() {
    val databaseInstance: FirebaseDatabase by lazy {
        FirebaseDatabase.getInstance()
    }

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase))
    }
}