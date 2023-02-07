package com.markets.deveshecomm.adapters

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.markets.deveshecomm.R
import com.markets.deveshecomm.Utils
import com.markets.deveshecomm.activities.ProductDetailsActivity
import com.markets.deveshecomm.databases.DatabaseEcomm
import com.markets.deveshecomm.databinding.RowProductBinding
import com.markets.deveshecomm.models.ModelItems
import com.markets.deveshecomm.models.ModelProduct
import com.markets.deveshecomm.utils.RetrofitClient
import com.squareup.picasso.Picasso
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AdapterProducts(var context: Context, var productsList: ArrayList<ModelProduct>) :
    RecyclerView.Adapter<AdapterProducts.HolderProduct>() {

    private lateinit var databaseEcomm: DatabaseEcomm
    private lateinit var item: ModelItems

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderProduct {
        val binding = RowProductBinding.inflate(LayoutInflater.from(context), parent, false)
        return HolderProduct(binding)
    }

    override fun getItemCount(): Int {
        return productsList.size
    }

    private var quantity: Int = 0
    private var finalCost: Double = 0.0
    private var cost: Double = 0.0

    override fun onBindViewHolder(holder: HolderProduct, position: Int) {
        // init database
        databaseEcomm =
            Room.databaseBuilder(holder.binding.root.context, DatabaseEcomm::class.java, "ecomm")
                .fallbackToDestructiveMigration().build()
        item = ModelItems()

        CoroutineScope(Dispatchers.IO).launch {
            val tempItem: ModelItems? =
                databaseEcomm.daoItems().readItem(productsList[position].id.toInt())

            if (tempItem != null) {
                holder.binding.addToCartBtn.visibility = View.GONE
                holder.binding.qtyLl.visibility = View.VISIBLE

                holder.binding.qtyTv.text = tempItem.quantity
            }
        }
        // set data to views
        cost = productsList[position].price.toDouble()
        holder.binding.titleTv.text = productsList[position].title
        holder.binding.priceTv.text = (buildString {
            append("₹")
            append(productsList[position].price)
        })
        holder.binding.ratingRb.rating = productsList[position].rating.rate.toFloat()
        holder.binding.countTv.text = (buildString {
            append("(")
            append(productsList[position].rating.count)
            append(")")
        })
        // loading picture into product imageview
        try {
            Picasso.get().load(productsList[position].image).fit().centerCrop()
                .placeholder(R.drawable.ic_product).into(holder.binding.profileIv)
            holder.binding.profilePb.visibility = View.GONE
        } catch (e: Exception) {
            Utils.toast(context, e.message)
            holder.binding.profileIv.setImageResource(R.drawable.ic_product)
        }

        holder.itemView.setOnClickListener {
            val id = productsList[position].id.toInt()
            Log.i("ID", "$id")


            context.startActivity(Intent(context, ProductDetailsActivity::class.java).apply {
                putExtra("PRODUCT_ID", id)
                putExtra("PRODUCT_TITLE", productsList[position].title)
                putExtra("PRODUCT_DESCRIPTION", productsList[position].description)
                putExtra("PRODUCT_IMAGE", productsList[position].image)
            })
            val getData = RetrofitClient.retrofit.getProduct(id)

            CoroutineScope(Dispatchers.IO).launch {
                getData.enqueue(object : Callback<ModelProduct> {
                    override fun onResponse(
                        call: Call<ModelProduct>, response: Response<ModelProduct>
                    ) {
                        Log.i("ADAPTER_CLICK_ON_SUCCESS", "${response.body()}")
                        Utils.toast(context, "${response.body()}")
                    }

                    override fun onFailure(call: Call<ModelProduct>, t: Throwable) {
                        Log.i("DASHBOARD_ACTIVITY_ON_FAILURE", "$t $call")
                    }
                })
            }
        }
        quantity = 1

        holder.binding.addToCartBtn.setOnClickListener {
            finalCost += cost
            quantity = 1
            it.visibility = View.GONE
            holder.binding.qtyLl.visibility = View.VISIBLE

            addToCart(
                productsList[position].id,
                productsList[position].title,
                "$finalCost",
                "$cost",
                "$quantity",
                productsList[position].image
            )
        }

        holder.binding.addBtn.setOnClickListener {
            finalCost += cost
            quantity += 1

            holder.binding.qtyTv.text = "$quantity"

            addToCart(
                productsList[position].id,
                productsList[position].title,
                "$finalCost",
                "$cost",
                "$quantity",
                productsList[position].image
            )
        }

        holder.binding.removeBtn.setOnClickListener {
            if (quantity > 1) {
                finalCost -= cost
                quantity -= 1

                holder.binding.qtyTv.text = "$quantity"

                addToCart(
                    productsList[position].id,
                    productsList[position].title,
                    "$finalCost",
                    "$cost",
                    "$quantity",
                    productsList[position].image
                )
            } else if (quantity == 1) {
                holder.binding.qtyLl.visibility = View.GONE
                holder.binding.addToCartBtn.visibility = View.VISIBLE

                CoroutineScope(Dispatchers.IO).launch {
                    databaseEcomm.daoItems().deleteItem(productsList[position].id.toInt())
                }
            }
        }
    }

    private fun addToCart(
        pId: String,
        title: String,
        price: String,
        priceEach: String,
        quantity: String,
        image: String
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            item.apply {
                this.id = pId.toInt()
                this.pId = pId
                this.name = title
                this.price = price
                this.priceEach = priceEach
                this.quantity = quantity
                this.image = image
            }
            databaseEcomm.daoItems().insertItems(item)
        }

        CoroutineScope(Dispatchers.IO).launch {
            val list = databaseEcomm.daoItems().readAllItems()
            list.forEach {
                Log.i("CART_ITEM", "$it")
            }
        }
    }

    inner class HolderProduct(var binding: RowProductBinding) :
        RecyclerView.ViewHolder(binding.root)

}