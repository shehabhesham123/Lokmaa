package com.example.lokma.ui

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.lokma.R
import com.example.lokma.databinding.FragmentSignUpBinding
import com.example.lokma.firebase.Auth
import com.example.lokma.pojo.constant.Constant
import com.example.lokma.pojo.listener.ArrowBackListener
import com.example.lokma.pojo.listener.SignUpListener
import com.example.lokma.pojo.model.Keyboard
import com.example.lokma.pojo.model.User
import com.example.lokma.pojo.model.Validation
import com.google.android.material.snackbar.Snackbar

class SignUpFragment : Fragment() {

    private lateinit var listener: SignUpListener
    private lateinit var arrowBackListener: ArrowBackListener
    private lateinit var auth: Auth
    private lateinit var binding: FragmentSignUpBinding

    private var inProcess = false


    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is SignUpListener) listener = context
        else throw Exception("You must implements from SignUpListener (SignUpFragment:19)")
        if (context is ArrowBackListener) arrowBackListener = context
        else throw Exception("You must implements from ArrowBackListener (SignUpFragment:33)")
        // initialize auth
        auth = Auth(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_sign_up, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentSignUpBinding.bind(requireView())

        binding.SignUpFragmentButtonRegister.setOnClickListener {
            // check data and connect with database
            // and if all done --> call listener.setOnClickRegister(userEmail) with correct user data
            Keyboard.closeKeyboard(requireActivity())
            val name = binding.SignUpFragmentInputETName.text.toString().trim()
            val email = binding.SignUpFragmentInputETEmail.text.toString().trim()
            val password = binding.SignUpFragmentInputETPassword.text.toString().trim()
            val authUser = Auth.User(email, password, mutableMapOf("Name" to name), null)
            if (check(User(name, email, password,null))) {
                inProcess()
                auth.createNewUser(authUser, { firebaseUser ->
                    processIsEnd()
                    listener.setOnClickRegister(email)
                }, {
                    processIsEnd()
                    Snackbar.make(requireView(), it, Snackbar.LENGTH_SHORT).show()
                })
            }
        }

        binding.SignUpFragmentTextViewSignIn.setOnClickListener {
            listener.setOnClickSignIn()
        }

        binding.SignUpFragmentImageViewArrowBack.setOnClickListener {
            arrowBackListener.setOnClickOnArrowBack()
        }

    }

    private fun check(user: User): Boolean {
        val nameIsValid = Validation.checkName(user.name?.trim())
        if (!nameIsValid) {
            binding.SignUpEtName.isErrorEnabled = true
            binding.SignUpEtName.error = "valid characters A-Z a-z 0-9 _"
        } else binding.SignUpEtName.isErrorEnabled = false

        val emailIsValid = Validation.checkEmail(user.email)
        if (!emailIsValid) {
            binding.SignUpEtEmail.isErrorEnabled = true
            binding.SignUpEtEmail.error = "Invalid Email"
        } else binding.SignUpEtEmail.isErrorEnabled = false

        val passwordIsValid = Validation.checkPassword(user.password!!)
        if (!passwordIsValid) {
            binding.SignUpEtPassword.isErrorEnabled = true
            binding.SignUpEtPassword.error = "password must be 8 letters at least "
        } else binding.SignUpEtPassword.isErrorEnabled = false
        return nameIsValid && emailIsValid && passwordIsValid
    }

    private fun inProcess() {
        inProcess = true
        binding.SignUpFragmentButtonRegister.visibility = View.INVISIBLE
        binding.SignUpFragmentAnimationView.visibility = View.VISIBLE
    }

    private fun processIsEnd() {
        inProcess = false
        binding.SignUpFragmentButtonRegister.visibility = View.VISIBLE
        binding.SignUpFragmentAnimationView.visibility = View.GONE
    }

}