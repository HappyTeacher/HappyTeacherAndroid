package org.jnanaprabodhini.happyteacher.extension

fun CharSequence.getYoutubeUrlId(): String? {
    val youtubeIdPattern = "(?<=watch\\?v=|/videos/|embed\\/|youtu.be\\/|\\/v\\/|\\/e\\/|watch\\?v%3D|watch\\?feature=player_embedded&v=|%2Fvideos%2F|embed%\u200C\u200B2F|youtu.be%2F|%2Fv%2F)[^#\\&\\?\\n]*";

    val result = Regex(youtubeIdPattern).find(this)
    val id = result?.groupValues?.firstOrNull()

    if (id?.length != 12) {
        // Youtube IDs are 12 characters long
        return null
    }

    return id
}