package org.jnanaprabodhini.happyteacher.extension.taghandler

import android.text.Editable
import org.xml.sax.XMLReader

/**
 * A base implementation of the ListTagHandler. This class
 *  acts as the handler for the first level of nesting
 *  (outside of any list). From there, it delegates list
 *  tag handling to the other subclasses.
 */
class RootListTagHandler() : ListTagHandler() {

    override val tag: String = ""

    override fun handleTag(opening: Boolean, tag: String?, output: Editable?, xmlReader: XMLReader?) {
        super.handleTag(opening, tag, output, xmlReader)

        // At the root level, if we're closing a list,
        //  there must be a newline afterward for the span
        //  to work.
        if (!opening && (tag == "ol" || tag == "ul")
                && activeListHandler == null) {
            output?.append("\n")
        }
    }
}