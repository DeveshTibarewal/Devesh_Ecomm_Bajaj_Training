package com.markets.deveshecomm.activities

import android.os.Bundle
import androidx.activity.OnBackPressedDispatcher
import androidx.appcompat.app.AppCompatActivity
import com.markets.deveshecomm.R
import com.markets.deveshecomm.Utils
import com.markets.deveshecomm.databinding.ActivityProductDetailsBinding
import com.squareup.picasso.Picasso

class ProductDetailsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProductDetailsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProductDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val id = intent.extras?.getString("PRODUCT_ID")?.toInt()
        val title = intent.extras?.getString("PRODUCT_TITLE")
        val description = intent.extras?.getString("PRODUCT_DESCRIPTION")
        val image = intent.extras?.getString("PRODUCT_IMAGE")

        binding.titleTv.text = title
        binding.descriptionTv.text = description
        // loading picture into product imageview
        try {
            Picasso.get().load(image).fit().centerCrop()
                .placeholder(R.drawable.ic_product).into(binding.profileIv)
        } catch (e: Exception) {
            Utils.toast(this, e.message)
            binding.profileIv.setImageResource(R.drawable.ic_product)
        }

        binding.toolbarBackBtn.setOnClickListener {
            onBackPressed()
        }

    }
}