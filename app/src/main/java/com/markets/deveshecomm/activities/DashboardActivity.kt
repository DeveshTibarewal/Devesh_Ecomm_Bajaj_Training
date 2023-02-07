package com.markets.deveshecomm.activities

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.room.Room
import com.markets.deveshecomm.Utils
import com.markets.deveshecomm.adapters.AdapterCartItems
import com.markets.deveshecomm.adapters.AdapterProducts
import com.markets.deveshecomm.databases.DatabaseEcomm
import com.markets.deveshecomm.databinding.ActivityDashboardBinding
import com.markets.deveshecomm.databinding.DialogCartBinding
import com.markets.deveshecomm.models.ModelItems
import com.markets.deveshecomm.models.ModelProduct
import com.markets.deveshecomm.utils.RetrofitClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DashboardActivity : AppCompatActivity() {

    // main binding
    private lateinit var binding: ActivityDashboardBinding

    // database declaration
    private lateinit var databaseEcomm: DatabaseEcomm

    // adapters and list declaration for products
    private lateinit var adapterProducts: AdapterProducts
    private lateinit var productArrayList: ArrayList<ModelProduct>

    // adapters and list declaration for carts
    private lateinit var adapterCartItems: AdapterCartItems
    private lateinit var cartItemsList: ArrayList<ModelItems>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initDatabase()

        loadAllProducts()

        binding.toolbarRefreshBtn.setOnClickListener {
            Log.i("TOOLBAR_REFRESH_BUTTON_CLICK", "CLICKED")
            loadAllProducts()
        }

        binding.refreshBtn.setOnClickListener {
            Log.i("REFRESH_BUTTON_CLICK", "CLICKED")
            loadAllProducts()
        }

        binding.cartFab.setOnClickListener {
            showCartDialog()
        }

    }

    private fun initDatabase() {
        // init Database
        databaseEcomm =
            Room.databaseBuilder(binding.root.context, DatabaseEcomm::class.java, "ecomm")
                .fallbackToDestructiveMigration().build()
    }

    private lateinit var dialogCartBinding: DialogCartBinding
    private lateinit var refreshItemsList: ArrayList<Int>

    private fun showCartDialog() {
        // init cart list
        cartItemsList = ArrayList()
        refreshItemsList = ArrayList()

        // init binding for CustomDialog
        dialogCartBinding = DialogCartBinding.inflate(LayoutInflater.from(this))

        // dialog
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        // setting its view
        builder.setView(dialogCartBinding.root)

        CoroutineScope(Dispatchers.IO).launch {
            showCartItemsProgressBar()
            val listItems: ArrayList<ModelItems>? =
                databaseEcomm.daoItems().readAllItems() as ArrayList<ModelItems>

            if (listItems?.isEmpty() == true) {
                showCartItemsEmptyLayout()
            } else {
                listItems?.forEach {
                    cartItemsList.add(it)
                    refreshItemsList.add(it.id)
                }
                // setup adapter
                adapterCartItems = AdapterCartItems(this@DashboardActivity, cartItemsList)

                // set adapter to recycler view
                dialogCartBinding.cartItemsRv.adapter = adapterCartItems
                showCartItemsListLayout()
            }

        }

        val dialog = builder.create()

        dialogCartBinding.closeBtn.setOnClickListener {
            refreshAllProducts()
            dialog.dismiss()
        }

        dialogCartBinding.removeAllBtn.setOnClickListener {
            showCartItemsProgressBar()
            CoroutineScope(Dispatchers.IO).launch {
                databaseEcomm.daoItems().deleteAllItems()
                val listItems: ArrayList<ModelItems>? =
                    databaseEcomm.daoItems().readAllItems() as ArrayList<ModelItems>

                if (listItems?.isEmpty() == true) {
                    showCartItemsEmptyLayout()
                } else {
                    listItems?.forEach {
                        refreshItemsList.add(it.id)
                    }
                    showCartItemsListLayout()
                }
                dialog.show()
            }
        }

        dialog.setOnDismissListener {
            refreshAllProducts()
        }

        dialogCartBinding.checkOutBtn.setOnClickListener {
            dialog.dismiss()
            showCheckOutDialog()
        }

        dialog.show()
    }

    private fun showCheckOutDialog() {
        AlertDialog.Builder(this).apply {
            setTitle("Checkout Dialog Box")
            setMessage("This Dialog box will contain payment and delivery address modules and process")
            setPositiveButton("OK", DialogInterface.OnClickListener { _, _ ->
                Utils.toast(this@DashboardActivity, "Checkout Done!")
            })
        }.create().show()
    }

    fun refreshCartItemsDialog(list: ArrayList<ModelItems>?) {
        refreshItemsList = ArrayList()
        if (list?.isEmpty() == true) {
            showCartItemsEmptyLayout()
        } else {
            list?.forEach {
                refreshItemsList.add(it.id)
            }
            showCartItemsListLayout()
        }
    }

    private fun showCartItemsListLayout() {
        dialogCartBinding.emptyCartRl.visibility = View.GONE
        dialogCartBinding.cartItemsPb.visibility = View.GONE
        dialogCartBinding.removeAllBtn.visibility = View.VISIBLE
        dialogCartBinding.cartItemsRv.visibility = View.VISIBLE
        dialogCartBinding.checkOutBtn.visibility = View.VISIBLE
    }

    private fun showCartItemsEmptyLayout() {
        dialogCartBinding.checkOutBtn.visibility = View.GONE
        dialogCartBinding.cartItemsRv.visibility = View.GONE
        dialogCartBinding.removeAllBtn.visibility = View.GONE
        dialogCartBinding.cartItemsPb.visibility = View.GONE
        dialogCartBinding.emptyCartRl.visibility = View.VISIBLE
    }

    private fun showCartItemsProgressBar() {
        dialogCartBinding.checkOutBtn.visibility = View.GONE
        dialogCartBinding.cartItemsRv.visibility = View.GONE
        dialogCartBinding.emptyCartRl.visibility = View.GONE
        dialogCartBinding.removeAllBtn.visibility = View.GONE
        dialogCartBinding.cartItemsPb.visibility = View.VISIBLE
    }

    private fun showRecyclerViewLayout() {
        binding.recyclerPb.visibility = View.GONE
        binding.noInternetRl.visibility = View.GONE
        binding.productsRv.visibility = View.VISIBLE
        binding.cartFab.visibility = View.VISIBLE
    }

    private fun showNoInternetLayout() {
        binding.recyclerPb.visibility = View.GONE
        binding.noInternetRl.visibility = View.VISIBLE
        binding.productsRv.visibility = View.GONE
        binding.cartFab.visibility = View.GONE
    }

    private fun showProgressBarLayout() {
        binding.recyclerPb.visibility = View.VISIBLE
        binding.noInternetRl.visibility = View.GONE
        binding.productsRv.visibility = View.GONE
        binding.cartFab.visibility = View.GONE
    }

    private fun refreshAllProducts() {
        refreshItemsList.forEach {
            Log.i("MY_PRODUCT", "$it")
        }
    }

    private fun loadAllProducts() {
        showProgressBarLayout()
        val getData = RetrofitClient.retrofit.getAllProducts()
        CoroutineScope(Dispatchers.IO).launch {
            getData.enqueue(object : Callback<List<ModelProduct>> {
                override fun onResponse(
                    call: Call<List<ModelProduct>>, response: Response<List<ModelProduct>>
                ) {
                    productArrayList = response.body() as ArrayList<ModelProduct>
                    Log.i("DASHBOARD_ACTIVITY_ON_SUCCESS", "$productArrayList")

                    // init adapters and list
                    adapterProducts = AdapterProducts(this@DashboardActivity, productArrayList)
                    binding.productsRv.adapter = adapterProducts
                    showRecyclerViewLayout()
                }

                override fun onFailure(call: Call<List<ModelProduct>>, t: Throwable) {
                    Log.i("DASHBOARD_ACTIVITY_ON_FAILURE", "$t $call")
                    showNoInternetLayout()
                }
            })
        }
    }
}