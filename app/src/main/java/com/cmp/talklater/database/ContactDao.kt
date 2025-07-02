package com.cmp.talklater.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import com.cmp.talklater.model.ContactInfo
import kotlinx.coroutines.flow.Flow

@Dao
interface ContactDao {
    @Query("SELECT * FROM contacts ORDER BY time DESC")
    fun getAllContacts(): Flow<List<ContactInfo>>

    @Upsert()
    suspend fun insertContact(contact: ContactInfo)

    @Delete
    suspend fun deleteContact(contact: ContactInfo)

    @Query("DELETE FROM contacts")
    suspend fun deleteAllContacts()
}