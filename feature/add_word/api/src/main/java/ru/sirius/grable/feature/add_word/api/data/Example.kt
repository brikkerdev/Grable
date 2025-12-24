package ru.sirius.grable.feature.add_word.api.data

import java.io.Serializable

data class Example(
    val id: Int,
    val english: String,
    val russian: String
) : Serializable {
    override fun toString(): String = "$english — $russian"
}

