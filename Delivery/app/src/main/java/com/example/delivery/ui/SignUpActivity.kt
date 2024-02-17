package com.example.delivery.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.delivery.utils.Const
import com.example.delivery.backend.firebase.Firestore
import com.example.delivery.backend.firebase.NormalAuth
import com.example.delivery.databinding.ActivitySignUpBinding
import com.example.delivery.pojo.Delivery
import com.google.android.material.snackbar.Snackbar

class SignUpActivity : AppCompatActivity() {
    private lateinit var mBinding: ActivitySignUpBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        setSupportActionBar(mBinding.toolbar)
        supportActionBar?.title = "Sign up"

        mBinding.SignInBtnRegister.setOnClickListener {
            check()?.run {
                register(this)
            }
        }
    }

    private fun check(): Delivery? {
        val username = mBinding.SignInInputETUsername.text.toString()
        val password = mBinding.SignInInputETPassword.text.toString()
        val phone = mBinding.SignInInputETPhone.text.toString()
        if (username.isEmpty()) {
            mBinding.SignInInputETUsername.error = "Enter your username"
            return null
        }
        if (password.isEmpty()) {
            mBinding.SignInInputETPassword.error = "Enter your password"
            return null
        } else if (password.length < 8) {
            mBinding.SignInInputETPassword.error =
                "password must be 8 letter at least"
            return null
        }
        if (phone.isEmpty()) {
            mBinding.SignInInputETPhone.error = "Enter your phone"
            return null
        } else if (phone.length != 11) {
            mBinding.SignInInputETPhone.error = "Enter corrent number"
            return null
        }
        return Delivery(username, password, phone)
    }

    private fun registerButtonVisibility(isVisible: Boolean) {
        if (!isVisible) {
            mBinding.SignInBtnRegister.visibility = View.GONE
            mBinding.LottieAnimation.visibility = View.VISIBLE
        } else {
            mBinding.SignInBtnRegister.visibility = View.VISIBLE
            mBinding.LottieAnimation.visibility = View.GONE
        }
    }

    private fun register(delivery: Delivery) {
        registerButtonVisibility(false)

        val firestore = Firestore(baseContext)
        val auth = NormalAuth(baseContext)

        auth.signUp("${delivery.username}@lokma.com", delivery.password!!,
            {
                // Auth onSuccess
                firestore.upload(delivery, Const.DELIVERY_PATH, delivery.username,
                    {
                        // Firestore onSuccess
                        startActivity(SignInActivity.instanceWithClearStack(baseContext))
                        finish()
                    },
                    {
                        // Firestore onFailure
                        Snackbar.make(
                            mBinding.SignInInputETUsername,
                            it,
                            Snackbar.LENGTH_SHORT
                        ).show()
                        registerButtonVisibility(true)
                    })

            }, {
                registerButtonVisibility(true)
            })
    }

    companion object {
        fun instance(context: Context): Intent {
            return Intent(context, SignUpActivity::class.java)
        }
    }
}