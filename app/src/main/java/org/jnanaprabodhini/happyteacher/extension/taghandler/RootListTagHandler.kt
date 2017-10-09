package org.jnanaprabodhini.happyteacher.extension.taghandler

import android.text.Editable
import android.text.Html
import org.xml.sax.XMLReader

/**
 * A base implementation of the ListTagHandler. This class
 *  acts as the handler for the first level of nesting
 *  (outside of any list). From there, it delegates list
 *  tag handling to the other subclasses.
 */
class RootListTagHandler : ListTagHandler() {

    override val TAG: String = ""

    override fun handleTag(opening: Boolean, tag: String?, output: Editable?, xmlReader: XMLReader?) {
        super.handleTag(opening, tag, output, xmlReader)

        if (!opening && (tag == "ol" || tag == "ul")
                && activeListHandler == null) {
            output?.append("\n")
        }
    }

    override fun handleListItem(opening: Boolean, tag: String, output: Editable?, xmlReader: XMLReader?) {
        // Root level should not receive <li> tags outside of <ul> or <ol>, but if we do, do nothing.
    }
}