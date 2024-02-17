package com.example.lokma.ui

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.lokma.R
import com.example.lokma.databinding.FragmentSignInBinding
import com.example.lokma.firebase.Auth
import com.example.lokma.pojo.constant.Constant
import com.example.lokma.pojo.listener.SignInListener
import com.example.lokma.pojo.model.Keyboard
import com.example.lokma.pojo.model.User
import com.example.lokma.pojo.model.Validation
import com.google.android.material.snackbar.Snackbar

class SignInFragment : Fragment() {

    private lateinit var binding: FragmentSignInBinding
    private lateinit var listener: SignInListener
    private lateinit var auth: Auth
    private var inProcess = false

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is SignInListener) listener = context
        else throw Exception("You must implements from SignInListener (SignInFragment:23)")

        // initialize auth
        auth = Auth(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_sign_in, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentSignInBinding.bind(requireView())

        binding.SignInFragmentButtonLogin.setOnClickListener {
            val stat = requireContext().getSharedPreferences(Constant.sharedPreferencesName, Context.MODE_PRIVATE).getBoolean(Constant.networkConnectionKey,false)
            if (!inProcess) {
                // close keyboard
                Keyboard.closeKeyboard(requireActivity())
                // check data and connect with database
                // and if all done --> call listener.setOnClickOnLogin(user) with correct user data
                val email = binding.SignInFragmentInputETEmail.text.toString().trim()
                val password = binding.SignInFragmentInputETPassword.text.toString().trim()
                val user = User(null, email, password,null)
                if (check(user)) {
                    inProcess()
                    auth.login(user.email, user.password!!, { firebaseUser ->
                        processIsEnd()
                        listener.setOnClickOnLogin(firebaseUser?.email)
                    }, {
                        processIsEnd()
                        Snackbar.make(requireView(), it, Snackbar.LENGTH_SHORT).show()
                    })
                }
            }
        }

        binding.SignInFragmentTextViewForgetPassword.setOnClickListener {
            if (!inProcess) {
                val email = binding.SignInFragmentInputETEmail.text.toString().trim()
                if(Patterns.EMAIL_ADDRESS.matcher(email).matches())
                    listener.setOnClickOnForgetPassword(email)
                else binding.SignInFragmentInputETEmail.error = "Invalid email address"
            }
        }

        binding.SignInFragmentTextViewSignUp.setOnClickListener {
            if (!inProcess)
                listener.setOnClickOnSignUp()
        }

        binding.SignInFragmentLinearLayoutSignWithGoogle.setOnClickListener {
            if (!inProcess) {
                // auth with google
                listener.setOnClickOnSignWithGoogle()
            }
        }

        binding.SignInFragmentLinearLayoutSignWithApple.setOnClickListener {
            if (!inProcess) {
                // auth with apple
                listener.setOnClickOnSignWithApple()
            }
        }
    }

    private fun check(user: User): Boolean {
        val emailIsValid = Validation.checkEmail(user.email)
        if (!emailIsValid) {
            binding.editTextTextEmailAddress.isErrorEnabled = true
            binding.editTextTextEmailAddress.error = "Invalid Email"
        } else binding.editTextTextEmailAddress.isErrorEnabled = false
        val passwordIsValid = Validation.checkPassword(user.password!!)
        if (!passwordIsValid) {
            binding.editTextTextPassword.isErrorEnabled = true
            binding.editTextTextPassword.error = "password must be 8 letters at least "
        } else binding.editTextTextPassword.isErrorEnabled = false
        return emailIsValid && passwordIsValid
    }

    private fun inProcess() {
        inProcess = true
        binding.SignInFragmentButtonLogin.visibility = View.INVISIBLE
        binding.SignInFragmentAnimationView.visibility = View.VISIBLE
    }

    private fun processIsEnd() {
        inProcess = false
        binding.SignInFragmentButtonLogin.visibility = View.VISIBLE
        binding.SignInFragmentAnimationView.visibility = View.GONE
    }
}
