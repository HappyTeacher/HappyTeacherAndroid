package org.jnanaprabodhini.happyteacher.extension.taghandler

import android.graphics.Typeface.BOLD
import android.text.Editable
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.StyleSpan
import org.jnanaprabodhini.happyteacher.extension.toLocaleString
import org.xml.sax.XMLReader


/**
 * A ListTagHandler for ordered lists.
 */
class OrderedListTagHandler(indentationLevel: Int = 0): ListTagHandler(indentationLevel) {

    override val TAG: String = "ol"
    private var listNumber: Int = 1

    override fun handleListItem(opening: Boolean, tag: String, output: Editable?, xmlReader: XMLReader?) {
        if (opening) {
            output?.append(getIndentationString())

            val numberPrefix = "${listNumber.toLocaleString()}. "

            // Make the number prefix BOLD:
            val spannableStringBuilder = SpannableStringBuilder(numberPrefix)
            val boldStyle = StyleSpan(BOLD)
            spannableStringBuilder.setSpan(boldStyle, 0, numberPrefix.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

            output?.append(spannableStringBuilder)

            listNumber++
        } else {
            output?.append("\n")
        }
    }
}