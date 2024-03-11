package com.example.lokma.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
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
            mBinding.SignUpFragmentButtonRegister.visibility = View.GONE
            mBinding.SignUpFragmentAnimationView.visibility = View.VISIBLE
            mAuth.signUp("$username@lokma.com", password!!, {
                mFirestore.upload(this, Const.CLIENT_PATH, {
                    mBinding.SignUpFragmentButtonRegister.visibility = View.VISIBLE
                    mBinding.SignUpFragmentAnimationView.visibility = View.GONE
                    val intent = CongratulationActivity.instance(baseContext)
                    startActivity(intent)
                    finish()
                }, {
                    mBinding.SignUpFragmentButtonRegister.visibility = View.VISIBLE
                    mBinding.SignUpFragmentAnimationView.visibility = View.GONE
                    Toast.makeText(baseContext, it, Toast.LENGTH_SHORT).show()
                })
            }, {
                mBinding.SignUpFragmentButtonRegister.visibility = View.VISIBLE
                mBinding.SignUpFragmentAnimationView.visibility = View.GONE
                Toast.makeText(baseContext, it, Toast.LENGTH_SHORT).show()
            })
        }
    }

    private fun getClient(): Client? {
        val username = mBinding.SignUpFragmentInputETUsername.text!!.trim().toString()
        val password = mBinding.SignUpFragmentInputETPassword.text!!.trim().toString()
        val phoneNumber = mBinding.SignUpFragmentInputETPhone.text!!.trim().toString()
        if (username.isEmpty()) {
            mBinding.SignUpEtUsername.isErrorEnabled = true
            mBinding.SignUpEtUsername.error = "Enter valid username"
            return null
        }else mBinding.SignUpEtUsername.isErrorEnabled = false
        if (password.isEmpty() || password.length < 8) {
            mBinding.SignUpEtPassword.isErrorEnabled = true
            mBinding.SignUpEtPassword.error = "Enter valid password"
            return null
        }else mBinding.SignUpEtPassword.isErrorEnabled = false
        if (phoneNumber.isEmpty() || phoneNumber.length != 11) {
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