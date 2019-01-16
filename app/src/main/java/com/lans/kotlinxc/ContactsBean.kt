package com.lans.kotlinxc

/**
 * author:       lans
 * date:         2019/1/1210:17 AM
 * description:
 **/
class ContactsBean {
    var name: String? = null
    var phone: String? = null

    override fun toString(): String {
        return "Contacts{" +
                "name='" + name + '\''.toString() +
                ", phone='" + phone + '\''.toString() +
                '}'.toString()
    }
}