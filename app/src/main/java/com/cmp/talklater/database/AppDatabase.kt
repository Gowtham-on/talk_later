package com.cmp.talklater.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.cmp.talklater.model.ContactInfo

@Database(entities = [ContactInfo::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun contactDao(): ContactDao
}
