package org.jnanaprabodhini.happyteacherapp.extension

import android.graphics.Typeface
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.StyleSpan
import java.net.URLDecoder

fun CharSequence.getYoutubeUrlId(): String? {
    val youtubeIdPattern = "(?<=watch\\?v=|/videos/|embed\\/|youtu.be\\/|\\/v\\/|\\/e\\/|watch\\?v%3D|watch\\?feature=player_embedded&v=|%2Fvideos%2F|embed%\u200C\u200B2F|youtu.be%2F|%2Fv%2F)[^#\\&\\?\\n]*";

    val result = Regex(youtubeIdPattern).find(this)
    val id = result?.groupValues?.firstOrNull()

    return if (id?.length ?: 0 < 12) {
        // Running under the assumption that
        //  Youtube IDs will be 12 chars or fewer.
        //  (https://webapps.stackexchange.com/a/13856)
        id
    } else {
        null
    }
}

fun String.asIdInYoutubeUrl() = "https://www.youtube.com/watch?v=$this"

fun String.decode(): String = URLDecoder.decode(this, "UTF-8")

fun String.toItalicizedSpan(): SpannableStringBuilder {
    val spanBuilder = SpannableStringBuilder(this)
    spanBuilder.setSpan(StyleSpan(Typeface.ITALIC), 0, this.lastIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
    return spanBuilder
}