package com.markets.deveshecomm.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "items")
class ModelItems {
    @PrimaryKey
    var id: Int = 0

    var pId: String = ""

    var name: String = ""

    var priceEach: String = ""

    var price: String = ""

    var quantity: String = ""

    var image: String = ""

    override fun toString(): String {
        return "ModelItems(id=$id, pId='$pId', name='$name', priceEach='$priceEach', price='$price', quantity='$quantity', image='$image')"
    }


}