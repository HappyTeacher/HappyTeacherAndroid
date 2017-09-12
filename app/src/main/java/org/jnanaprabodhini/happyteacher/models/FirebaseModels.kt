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
data class LessonHeader(var authorId: String = "",
                        var authorInstitution: String = "",
                        var authorLocation: String = "",
                        var authorName: String = "",
                        var dateEdited: Long = 0,
                        var name: String = "")