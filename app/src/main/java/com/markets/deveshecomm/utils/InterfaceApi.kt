package com.markets.deveshecomm.utils

import com.markets.deveshecomm.models.ModelProduct
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface InterfaceApi {

    @GET("products")
    fun getAllProducts(): Call<List<ModelProduct>>

    @GET("products/{id}")
    fun getProduct(@Path(value = "id") id: Int): Call<ModelProduct>
}