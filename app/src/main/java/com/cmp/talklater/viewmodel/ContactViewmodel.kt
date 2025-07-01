package com.cmp.talklater.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cmp.talklater.database.ContactDao
import com.cmp.talklater.database.ContactRepository
import com.cmp.talklater.model.ContactInfo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ContactViewmodel @Inject constructor(
    private val repository: ContactRepository
): ViewModel() {

    val contacts = repository.getAllContacts()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())


    fun addContact(contact: ContactInfo) {
        viewModelScope.launch {
            repository.addContact(contact)
        }
    }

    fun deleteContact(contact: ContactInfo) {
        viewModelScope.launch {
            repository.deleteContact(contact)
        }
    }
}