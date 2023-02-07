package com.markets.deveshecomm.fragments

import android.app.DatePickerDialog
import android.icu.text.SimpleDateFormat
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
import com.markets.deveshecomm.databases.DatabaseEcomm
import com.markets.deveshecomm.databinding.FragmentRegisterBinding
import com.markets.deveshecomm.models.ModelUsers
import com.markets.deveshecomm.utils.InterfaceApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

class RegisterFragment : Fragment() {

    // TAG for logging
    private val TAG: String = "REGISTER_TAG"

    // database components
    private lateinit var databaseEcomm: DatabaseEcomm
    private lateinit var user: ModelUsers

    // binding UI Views
    private lateinit var binding: FragmentRegisterBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentRegisterBinding.inflate(inflater)

        // init Database
        databaseEcomm =
            Room.databaseBuilder(binding.root.context, DatabaseEcomm::class.java, "ecomm")
                .fallbackToDestructiveMigration().build()

        binding.dobEt.setOnClickListener {
            // on below line we are getting
            // the instance of our calendar.
            val calendar = Calendar.getInstance()

            // on below line we are getting
            // our day, month and year.
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            // on below line we are creating a
            // variable for date picker dialog.
            val datePickerDialog = DatePickerDialog(
                // on below line we are passing context.
                binding.root.context, { _, sYear, monthOfYear, dayOfMonth ->
                    // on below line we are setting
                    // date to our edit text.
                    calendar.set(sYear, monthOfYear, dayOfMonth)
                    val dateFormatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                    val strDate: String = dateFormatter.format(calendar.time)
                    binding.dobEt.setText(strDate)
                    // email requests focus
                    binding.emailEt.requestFocus()
                },
                // on below line we are passing year, month
                // and day for the selected date in our date picker.
                year, month, day
            )
            // at last we are calling show
            // to display our date picker dialog.
            datePickerDialog.show()

        }

        // handle toolbarBackBtn click, go back
        binding.toolbarBackBtn.setOnClickListener {
            findNavController().popBackStack()
        }

        // handle haveAccountTv click, go to Login Fragment
        binding.haveAccountTv.setOnClickListener {
            findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
        }

        // handle registerBtn click, start user registration
        binding.registerBtn.setOnClickListener {
            validateData()
        }

        return binding.root
    }

    private lateinit var name: String
    private lateinit var phone: String
    private lateinit var dob: String
    private lateinit var email: String
    private lateinit var password: String
    private lateinit var cPassword: String

    private fun validateData() {
        // get data from UI
        name = binding.nameEt.text.toString().trim()
        phone = binding.phoneEt.text.toString().trim()
        dob = binding.dobEt.text.toString().trim()
        email = binding.emailEt.text.toString().trim()
        password = binding.passwordEt.text.toString()
        cPassword = binding.cPasswordEt.text.toString()

        if (name.isEmpty()) {
            // name is empty, show error
            binding.nameEt.error = "Enter your Name"
            binding.nameEt.requestFocus()
        } else if (!Patterns.PHONE.matcher(phone).matches()) {
            // invalid phone pattern, show error
            binding.phoneEt.error = "Enter valid phone number"
            binding.phoneEt.requestFocus()
        } else if (dob.isEmpty()) {
            // dob is empty, show error
            binding.dobEt.error = "Enter your Date of Birth"
            binding.dobEt.requestFocus()
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            // invalid email pattern, show error
            binding.emailEt.error = "Enter valid Email"
            binding.emailEt.requestFocus()
        } else if (password.isEmpty()) {
            // password is empty show error, show error
            binding.passwordEt.error = "Enter Password"
            binding.passwordEt.requestFocus()
        } else if (password.length < 6) {
            // password length is less than 6 show error, show error
            binding.passwordEt.error = "Enter valid Password having atleast 6 letter"
            binding.passwordEt.requestFocus()
        } else if (password != cPassword) {
            // password are not matching, show error
            binding.cPasswordEt.error = "Password doesn't match, Please check"
            binding.cPasswordEt.requestFocus()
        } else {
            registerData()
        }

    }

    private fun registerData() {
        CoroutineScope(Dispatchers.IO).launch {
            user = ModelUsers()
            user.name = name
            user.phone = phone
            user.dob = dob
            user.email = email
            user.password = password
            databaseEcomm.daoUsers().insertUser(user)

//            CoroutineScope(Dispatchers.IO).launch {
//                databaseEcomm.daoUsers().readUser().forEach {
//                    Log.i(TAG, "$it")
//                }
//            }

        }
        findNavController().navigate(R.id.action_registerFragment_to_loginFragment)

    }

}
