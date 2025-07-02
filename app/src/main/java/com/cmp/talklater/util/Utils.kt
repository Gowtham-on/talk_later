package com.cmp.talklater.util

import com.cmp.talklater.model.ContactInfo
import com.cmp.talklater.model.GroupedContactInfo

object Utils {

    fun getListByGrouping(list: List<ContactInfo>): List<GroupedContactInfo> {
        return list
            .groupBy { it.number to it.type }
            .values
            .map { GroupedContactInfo(it) }
    }
}
