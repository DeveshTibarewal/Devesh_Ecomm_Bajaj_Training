package com.markets.deveshecomm.models

import android.media.Rating

data class ModelProduct(
    var id: String,
    var title: String,
    var price: String,
    var description: String,
    var category: String,
    var image: String,
    var rating: ModelRating
)


