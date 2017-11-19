package org.jnanaprabodhini.happyteacherapp.extension.taghandler

import android.graphics.Typeface.BOLD
import android.text.Editable
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.StyleSpan
import org.jnanaprabodhini.happyteacherapp.extension.toLocaleString
import org.xml.sax.XMLReader


/**
 * A ListTagHandler for ordered lists.
 */
class OrderedListTagHandler(indentationLevel: Int = 0): ListTagHandler(indentationLevel) {

    override val tag: String = "ol"
    private var listNumber: Int = 1
    private val numberPrefix: String
        get() = "${listNumber.toLocaleString()}. "

    private var stringStart = 0

    override fun handleListItem(opening: Boolean, tag: String, output: Editable?, xmlReader: XMLReader?) {
        if (opening) {
            // Make the number prefix BOLD:
            val spannableStringBuilder = SpannableStringBuilder(numberPrefix)
            val boldStyle = StyleSpan(BOLD)

            spannableStringBuilder.setSpan(boldStyle, 0, numberPrefix.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

            stringStart = output?.length ?: 0
            output?.append(spannableStringBuilder)
            listNumber++
        } else {
            if (output?.get(output.lastIndex) != '\n') output?.append('\n')

            output?.setSpan(getIndentationSpan(), stringStart, output.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        }
    }
}