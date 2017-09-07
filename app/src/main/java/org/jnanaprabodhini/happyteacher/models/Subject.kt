package org.jnanaprabodhini.happyteacher.models

/**
 * Created by grahamearley on 9/7/17.
 */

data class Subject(var isActive: Boolean = false, var names: Map<String, String> = emptyMap<String, String>())