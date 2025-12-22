package ru.sirius.grable.add_word.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Example(
    val id : Int,
    val english: String,
    val russian: String
) : Parcelable {
    override fun toString(): String = "$english — $russian"
}