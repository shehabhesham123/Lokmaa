package com.example.lokma.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.lokma.databinding.ActivityLoginBinding
import com.example.lokma.network.firebase.NormalAuth
import com.example.lokma.pojo.Client

class LoginActivity : AppCompatActivity() {
    private lateinit var mBinding: ActivityLoginBinding
    private lateinit var mAuth: NormalAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        mAuth = NormalAuth(baseContext)
        val currentUser = mAuth.getCurrentUser()
        currentUser?.let {
            val intent = RestaurantsActivity.instance(baseContext)
            startActivity(intent)
            finish()
        }

        mBinding.SignInFragmentTextViewSignUp.setOnClickListener {
            val intent = SignUpActivity.instance(baseContext)
            startActivity(intent)
            finish()
        }

        mBinding.SignInFragmentButtonLogin.setOnClickListener {
            login()
        }
    }

    private fun login() {
        val client = getClient()
        client?.run {
            // login
            mAuth.signIn("$username@lokma.com", password!!, {
                val intent = RestaurantsActivity.instance(baseContext)
                startActivity(intent)
                finish()
            }, {})
        }
    }

    private fun getClient(): Client? {
        val userName = mBinding.SignInFragmentInputETUsername.text.toString()
        val password = mBinding.SignInFragmentInputETPassword.text.toString()
        if (userName.isEmpty()) {
            mBinding.editTextTextEmailAddress.error = "Enter valid username"
            return null
        }
        if (password.isEmpty()) {
            mBinding.editTextTextPassword.error = "Enter valid password"
            return null
        }
        return Client(userName, password)
    }

    companion object {
        fun instance(context: Context): Intent {
            return Intent(context, LoginActivity::class.java)
        }
    }
}