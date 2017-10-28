package org.jnanaprabodhini.happyteacher.activity

import android.os.Bundle
import com.google.android.gms.location.places.AutocompleteFilter
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment
import kotlinx.android.synthetic.main.activity_profile.*
import org.jnanaprabodhini.happyteacher.R
import org.jnanaprabodhini.happyteacher.activity.base.HappyTeacherActivity

class ProfileActivity : HappyTeacherActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        signOutButton.setOnClickListener {
            auth.signOut()
            finish()
        }

        val cityFilter = AutocompleteFilter.Builder()
                .setTypeFilter(AutocompleteFilter.TYPE_FILTER_CITIES)
                .build()

        val placeAutocompleteFragment = fragmentManager.findFragmentById(R.id.placeAutocompleteFragment) as PlaceAutocompleteFragment
        placeAutocompleteFragment.setFilter(cityFilter)
    }
}
