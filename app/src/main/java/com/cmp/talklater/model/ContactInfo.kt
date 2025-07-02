package com.cmp.talklater.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Entity(tableName = "contacts", primaryKeys = ["name", "number", "time"])
data class ContactInfo(
    val name: String,
    val number: String,
    val time: String,
    val type: Int,
    val notes: String?,
    var count: Int = 1
)


data class GroupedContactInfo(
    val listOfContact: List<ContactInfo> = listOf()
)