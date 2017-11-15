package org.jnanaprabodhini.happyteacher.extension

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