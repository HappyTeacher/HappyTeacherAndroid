package org.jnanaprabodhini.happyteacherapp.preference

import android.content.Context
import android.support.v7.preference.DialogPreference
import android.support.v7.preference.Preference
import android.support.v7.preference.SwitchPreferenceCompat
import android.util.AttributeSet
import android.util.Log
import com.google.firebase.firestore.DocumentReference
import org.jnanaprabodhini.happyteacherapp.R
import org.jnanaprabodhini.happyteacherapp.dialog.ModeratorSubjectSubscriptionsDialog

/**
 * Created by grahamearley on 11/29/17.
 */
class ModeratorSubjectSubscriptionPreference(context: Context, attrs: AttributeSet): DialogPreference(context, attrs) {

    override fun onClick() {
        val dialog = ModeratorSubjectSubscriptionsDialog(context)
        dialog.show()
    }
}
