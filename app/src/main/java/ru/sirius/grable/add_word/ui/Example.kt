package ru.sirius.grable.add_word.ui

data class Example(
    val id : Int,
    val english: String,
    val russian: String
) {
    override fun toString(): String = "$english — $russian"
}