package com.markets.deveshecomm.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
class ModelUsers {

    @PrimaryKey(autoGenerate = true)
    var id: Int = 0

    var name: String = ""

    var phone: String = ""

    var dob: String = ""

    var email: String = ""

    var password: String = ""

    override fun toString(): String {
        return "ModelUsers(id=$id, name='$name', phone='$phone', dob='$dob', email='$email', password='$password')"
    }


}