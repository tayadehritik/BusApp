package com.tayadehritik.busapp.ui.authentication

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.tayadehritik.busapp.ui.MainActivity
import com.tayadehritik.busapp.R
import java.util.regex.Pattern

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [Verify.newInstance] factory method to
 * create an instance of this fragment.
 */
class Verify : Fragment() {
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
        val retview =  inflater.inflate(R.layout.fragment_authentication_verify, container, false)
        val verifcationCodeTextLayout:TextInputLayout = retview.findViewById<TextInputLayout>(R.id.VerificationCodeTextInputLayout)
        val verificationCodeTextField: TextInputEditText = retview.findViewById<TextInputEditText>(R.id.VerificationCodeTextInputEditText)
        val verifyButton: Button = retview.findViewById<Button>(R.id.VerifyButton)

        verifyButton.setOnClickListener {
            val authAct = (activity as MainActivity)
            val verificationCode = verificationCodeTextField.text.toString().trim()
            verifcationCodeTextLayout.error = null

            if (isValidVerificationCode(verificationCode)) {
                authAct.verifyPhoneNumberWithCode(authAct.storedVerificationId, verificationCode)
            } else {
                // Display an error message or handle invalid verification code
                verifcationCodeTextLayout.error = "Invalid OTP"
            }
        }

        return retview
    }


    private fun isValidVerificationCode(verificationCode: String): Boolean {
        val verificationCodeRegex = "^\\d{6}$" // Assuming a 6-digit verification code
        val pattern = Pattern.compile(verificationCodeRegex)
        return pattern.matcher(verificationCode).matches()
    }
    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment Login.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            Verify().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}