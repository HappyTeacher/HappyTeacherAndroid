package org.jnanaprabodhini.happyteacher.activity

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Window
import android.view.WindowManager
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

        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        supportActionBar?.hide()

        setContentView(R.layout.activity_full_screen_gallery_viewer)

        if (intent.hasAllExtras()) {
            val imageUrls = intent.getImageUrls()
            val selectedImageIndex = intent.getSelectedImage()
            setupViewPagerForImages(imageUrls, selectedImageIndex)
        }

    }

    private fun setupViewPagerForImages(imageUrls: Array<String>, selectedImageIndex: Int) {
        fullscreenImageViewPager.adapter = GalleryViewPagerAdapter(imageUrls)
        fullscreenImageViewPager.currentItem = selectedImageIndex
    }
}
