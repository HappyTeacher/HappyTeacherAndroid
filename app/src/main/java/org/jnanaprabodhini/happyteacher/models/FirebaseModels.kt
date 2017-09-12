package org.jnanaprabodhini.happyteacher.models

/**
 * Created by grahamearley on 9/7/17.
 */

data class Subject(var isActive: Boolean = false,
                   var names: Map<String, String> = emptyMap<String, String>())

data class Topic(var isActive: Boolean = false,
                 var names: Map<String, String> = emptyMap<String, String>(),
                 var subject: String = "")

// TODO: Update with actual object structure when complete:
data class LessonHeader(var author_id: String = "",
                        var author_institution: String = "",
                        var author_location: String = "",
                        var author_name: String = "",
                        var dateEdited: Long = 0,
                        var name: String = "")