package com.example.lokma.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.lokma.constant.Const
import com.example.lokma.databinding.ActivitySignUpBinding
import com.example.lokma.network.firebase.Firestore
import com.example.lokma.network.firebase.NormalAuth
import com.example.lokma.pojo.Client

class SignUpActivity : AppCompatActivity() {
    private lateinit var mBinding: ActivitySignUpBinding
    private lateinit var mAuth: NormalAuth
    private lateinit var mFirestore: Firestore
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        mAuth = NormalAuth(baseContext)
        mFirestore = Firestore(baseContext)

        mBinding.SignUpFragmentTextViewSignIn.setOnClickListener {
            val intent = LoginActivity.instance(baseContext)
            startActivity(intent)
            finish()
        }

        mBinding.SignUpFragmentButtonRegister.setOnClickListener {
            register()
        }
    }

    private fun register() {
        val client = getClient()
        client?.run {
            mAuth.signUp("$username@lokma.com", password!!, {
                mFirestore.upload(this, Const.CLIENT_PATH, {
                    val intent = CongratulationActivity.instance(baseContext)
                    startActivity(intent)
                    finish()
                }, {
                    Toast.makeText(baseContext, it, Toast.LENGTH_SHORT).show()
                })
            }, {
                Toast.makeText(baseContext, it, Toast.LENGTH_SHORT).show()
            })
        }
    }

    private fun getClient(): Client? {
        val username = mBinding.SignUpFragmentInputETUsername.text!!.trim().toString()
        val password = mBinding.SignUpFragmentInputETPassword.text!!.trim().toString()
        val phoneNumber = mBinding.SignUpFragmentInputETPhone.text!!.trim().toString()
        if (username.isEmpty()) {
            mBinding.SignUpEtUsername.error = "Enter valid username"
            return null
        }
        if (password.isEmpty()) {
            mBinding.SignUpEtPassword.error = "Enter valid password"
            return null
        }
        if (phoneNumber.isEmpty()) {
            mBinding.SignUpEtPhone.error = "Enter valid phone"
            return null
        }
        return Client(username, password, phoneNumber)
    }

    companion object {
        fun instance(context: Context): Intent {
            return Intent(context, SignUpActivity::class.java)
        }
    }
}