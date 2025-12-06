package ru.sirius.grable.common

import androidx.room.TypeConverter
import java.sql.Timestamp

class Converters {

    @TypeConverter
    fun toTimestamp(value: Long?): Timestamp? =
        value?.let { Timestamp(it) }

    @TypeConverter
    fun fromTimestamp(timestamp: Timestamp?): Long? =
        timestamp?.time
}