package com.markets.deveshecomm.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.markets.deveshecomm.R
import com.markets.deveshecomm.Utils
import com.markets.deveshecomm.activities.DashboardActivity
import com.markets.deveshecomm.databases.DatabaseEcomm
import com.markets.deveshecomm.databinding.RowCartItemBinding
import com.markets.deveshecomm.models.ModelItems
import com.squareup.picasso.Picasso
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AdapterCartItems(var context: Context, var cartItemsList: ArrayList<ModelItems>) :
    RecyclerView.Adapter<AdapterCartItems.HolderCartItem>() {

    private lateinit var databaseEcomm: DatabaseEcomm
    private lateinit var item: ModelItems

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderCartItem {
        val binding = RowCartItemBinding.inflate(LayoutInflater.from(context), parent, false)
        return HolderCartItem(binding)
    }

    override fun getItemCount(): Int {
        return cartItemsList.size
    }

    override fun onBindViewHolder(holder: HolderCartItem, position: Int) {
        // init database
        databaseEcomm =
            Room.databaseBuilder(holder.binding.root.context, DatabaseEcomm::class.java, "ecomm")
                .fallbackToDestructiveMigration().build()
        item = ModelItems()

        holder.binding.removeBtn.setOnClickListener {
            val id = cartItemsList[position].id
            CoroutineScope(Dispatchers.IO).launch {
                databaseEcomm.daoItems().deleteItem(id)
            }
            // refresh List
            cartItemsList.removeAt(position)
            (context as DashboardActivity).refreshCartItemsDialog(cartItemsList)
            notifyItemRemoved(position)
            notifyDataSetChanged()
        }

        holder.binding.nameTv.text = cartItemsList[position].name
        holder.binding.quantityTv.text = cartItemsList[position].quantity

        // loading picture into product imageview
        try {
            Picasso.get().load(cartItemsList[position].image).fit().centerCrop()
                .placeholder(R.drawable.ic_product).into(holder.binding.profileIv)
        } catch (e: Exception) {
            Utils.toast(context, e.message)
            holder.binding.profileIv.setImageResource(R.drawable.ic_product)
        }

    }

    inner class HolderCartItem(var binding: RowCartItemBinding) :
        RecyclerView.ViewHolder(binding.root)

}