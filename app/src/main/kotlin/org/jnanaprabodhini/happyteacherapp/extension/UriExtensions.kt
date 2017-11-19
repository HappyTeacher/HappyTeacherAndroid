package org.jnanaprabodhini.happyteacherapp.extension

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import java.io.File


/**
 * See https://developer.android.com/guide/topics/providers/document-provider.html
 */
fun Uri.getFileName(context: Context): String? {
    if (this.scheme == "content") {
        context.contentResolver.query(this, null, null, null, null)
                .use { cursor ->
                    if (cursor.moveToFirst()) {
                        return cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME))
                    }
                }
    }

    val unresolvedName = this.path
    val cut = unresolvedName.lastIndexOf(File.separator)

    return if (cut != -1) {
        (unresolvedName.substring(cut + 1))
    } else {
        unresolvedName
    }
}