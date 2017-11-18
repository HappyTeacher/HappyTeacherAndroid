package org.jnanaprabodhini.happyteacherapp.util

/**
 * An ArrayList that calls given functions for list change events.
 */
class ObservableArrayList<T>(private val onPreAdd: (T) -> Unit, private val onPreClear: (List<T>) -> Unit): ArrayList<T>() {
    override fun add(index: Int, element: T) {
        onPreAdd(element)
        super.add(index, element)
    }

    override fun add(element: T): Boolean {
        onPreAdd(element)
        return super.add(element)
    }

    override fun addAll(elements: Collection<T>): Boolean {
        elements.forEach(onPreAdd)
        return super.addAll(elements)
    }

    override fun addAll(index: Int, elements: Collection<T>): Boolean {
        elements.forEach(onPreAdd)
        return super.addAll(index, elements)
    }

    override fun clear() {
        onPreClear(this)
        super.clear()
    }
}