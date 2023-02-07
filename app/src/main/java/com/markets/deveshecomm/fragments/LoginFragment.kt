package com.markets.deveshecomm.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.room.Room
import com.markets.deveshecomm.R
import com.markets.deveshecomm.Utils
import com.markets.deveshecomm.activities.DashboardActivity
import com.markets.deveshecomm.databases.DatabaseEcomm
import com.markets.deveshecomm.databinding.FragmentLoginBinding
import com.markets.deveshecomm.models.ModelUsers
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LoginFragment : Fragment() {


    // database components
    private lateinit var databaseEcomm: DatabaseEcomm
    private lateinit var user: ModelUsers

    // binding the view (fragment_login.xml)
    private lateinit var binding: FragmentLoginBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentLoginBinding.inflate(layoutInflater)

        // init database
        // init Database
        databaseEcomm =
            Room.databaseBuilder(binding.root.context, DatabaseEcomm::class.java, "ecomm")
                .fallbackToDestructiveMigration().build()

        binding.toolbarBackBtn.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.noAccountTv.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
        }

        binding.loginBtn.setOnClickListener {
            // Handle login Btn Click
            validateData()
        }

        return binding.root
    }

    private lateinit var email: String
    private lateinit var password: String

    private fun validateData() {
        CoroutineScope(Dispatchers.IO).launch {
            databaseEcomm.daoUsers().readUser().forEach {
                Log.e("TAG", "$it")
            }
        }

        // input data
        email = binding.emailEt.text.toString().trim()
        password = binding.passwordEt.text.toString()
        // validate data
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            // email pattern is invalid, show error
            binding.emailEt.error = "Invalid Email..."
            binding.emailEt.requestFocus()
        } else if (password.isEmpty()) {
            // password is not entered, show error
            binding.emailEt.error = "Enter Password"
            binding.emailEt.requestFocus()
        } else {
            // email password is valid and password is not empty, start login
            loginUser()
        }
    }

    private fun loginUser() {
        // login user using RoomDB
        databaseEcomm.daoUsers().authenticateUser(email, password).observe(viewLifecycleOwner) {
            if (it != null) {
                context?.startActivity(Intent(binding.root.context, DashboardActivity::class.java))
                activity?.finish()
            } else {
                Utils.toast(binding.root.context, "Please enter valid email and password...")
            }
        }

    }

}