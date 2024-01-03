package com.tayadehritik.busapp.authentication

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.tayadehritik.busapp.R
import java.util.regex.Pattern

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [Login.newInstance] factory method to
 * create an instance of this fragment.
 */
class Login : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        val retview =  inflater.inflate(R.layout.fragment_authentication_login, container, false)
        val phoneTextLayout:TextInputLayout = retview.findViewById<TextInputLayout>(R.id.PhoneTextInputLayout)
        val phoneTextField: TextInputEditText = retview.findViewById<TextInputEditText>(R.id.PhoneTextInputEditText)
        val loginButton: Button = retview.findViewById<Button>(R.id.LoginButton)

        loginButton.setOnClickListener {
            val phoneNumber = phoneTextField.text.toString().trim()
            phoneTextLayout.error = null
            if (isValidPhoneNumber(phoneNumber)) {
                // Phone number is valid, proceed with verification
                (activity as Authentication).startPhoneNumberVerification(phoneNumber)
            } else {
                // Display an error message or handle invalid phone number
                phoneTextLayout.error = "Invalid phone number"

            }
        }


        return retview

    }

    private fun isValidPhoneNumber(phoneNumber: String): Boolean {
        val phoneRegex = "^[6-9]\\d{9}$" // Indian phone numbers start with 6, 7, 8, or 9 and are followed by 9 more digits
        val pattern = Pattern.compile(phoneRegex)
        return pattern.matcher(phoneNumber).matches()
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment AuthenticationHostFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            Login().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}