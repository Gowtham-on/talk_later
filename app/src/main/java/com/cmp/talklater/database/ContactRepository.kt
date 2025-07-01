package com.cmp.talklater.database

import com.cmp.talklater.model.ContactInfo
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ContactRepository @Inject constructor(
    private val contactDao: ContactDao
) {
    suspend fun addContact(contact: ContactInfo) {
        contactDao.insertContact(contact)
    }

    suspend fun deleteContact(contact: ContactInfo) {
        contactDao.deleteContact(contact)
    }

    fun getAllContacts(): Flow<List<ContactInfo>> {
        return contactDao.getAllContacts()
    }
}