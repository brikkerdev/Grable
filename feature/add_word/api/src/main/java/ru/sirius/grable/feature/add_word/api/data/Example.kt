package ru.sirius.grable.feature.add_word.api.data

import java.io.Serializable

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Example(
    val id: Int,
    val english: String,
    val russian: String
) : Serializable {
    override fun toString(): String = "$english — $russian"
}

