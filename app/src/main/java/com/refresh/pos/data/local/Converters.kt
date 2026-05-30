package com.refresh.pos.data.local

import androidx.room.TypeConverter

class Converters {

    @TypeConverter
    fun fromTimestamp(value: Long?): String? {
        return value?.toString()
    }

    @TypeConverter
    fun dateToTimestamp(date: String?): Long? {
        return date?.toLongOrNull()
    }
}
