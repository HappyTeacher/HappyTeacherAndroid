package org.jnanaprabodhini.happyteacher.activity

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_full_screen_gallery_viewer.*
import org.jnanaprabodhini.happyteacher.R
import org.jnanaprabodhini.happyteacher.adapter.GalleryViewPagerAdapter

class FullScreenGalleryViewerActivity : AppCompatActivity() {

    companion object IntentExtraHelper {
        val IMAGE_URLS: String = "IMAGE_URLS"
        fun Intent.hasImageUrls(): Boolean = hasExtra(IMAGE_URLS)
        fun Intent.getImageUrls(): Array<String> = getStringArrayExtra(IMAGE_URLS)

        val SELECTED_IMAGE: String = "SELECTED_IMAGE"
        fun Intent.hasSelectedImage(): Boolean = hasExtra(SELECTED_IMAGE)
        fun Intent.getSelectedImage(): Int = getIntExtra(SELECTED_IMAGE, 0)

        fun Intent.hasAllExtras(): Boolean = hasImageUrls() && hasSelectedImage()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_full_screen_gallery_viewer)

        if (intent.hasAllExtras()) {
            val imageUrls = intent.getImageUrls()
            val selectedImageIndex = intent.getSelectedImage()
            setupViewPagerForImages(imageUrls, selectedImageIndex)
        }

        closeButton.setOnClickListener { finish() }
    }

    private fun setupViewPagerForImages(imageUrls: Array<String>, selectedImageIndex: Int) {
        fullscreenImageViewPager.adapter = GalleryViewPagerAdapter(imageUrls)
        fullscreenImageViewPager.currentItem = selectedImageIndex
    }

    override fun onPause() {
        super.onPause()
        overridePendingTransition(R.anim.fade_in_quick, R.anim.fade_out_quick)
    }

    override fun onResume() {
        super.onResume()
        overridePendingTransition(R.anim.fade_in_quick, R.anim.fade_out_quick)
    }
}
