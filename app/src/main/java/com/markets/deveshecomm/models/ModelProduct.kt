package com.markets.deveshecomm.models

data class ModelProduct(
    var id: String,
    var title: String,
    var price: String,
    var description: String,
    var category: String,
    var image: String,
    var rating: ModelRating
)


