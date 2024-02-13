package com.example.admin.ui

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.admin.backend.firebase.Firestore
import com.example.admin.backend.firebase.NormalAuth
import com.example.admin.backend.firebase.Storage
import com.example.admin.databinding.ActivityAdminInfoBinding
import com.example.admin.pojo.Admin
import com.example.admin.pojo.Menu
import com.example.admin.pojo.Restaurant
import com.example.admin.utils.Const
import com.example.admin.utils.TempStorage
import com.google.android.material.snackbar.Snackbar

class AdminInfoActivity : AppCompatActivity() {
    private lateinit var mBinding: ActivityAdminInfoBinding
    private lateinit var mTempStorage: TempStorage

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityAdminInfoBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        mTempStorage = TempStorage.instance()

        setSupportActionBar(mBinding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Sign up"

        mBinding.SignInBtnRegister.setOnClickListener {
            check()?.run {
                register(this)
            }
        }
    }

    private fun check(): Admin? {
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
        return Admin(username, password, phone)
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

    private fun register(admin: Admin) {
        registerButtonVisibility(false)

        val firestore = Firestore(baseContext)
        val storage = Storage(baseContext)
        val auth = NormalAuth(baseContext)

        auth.signUp("${admin.username}@lokma.com", admin.password!!,
            {
                // Auth onSuccess

                firestore.upload(admin, Const.ADMIN_PATH, admin.username,
                    {
                        // Firestore onSuccess
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

                admin.successfulLogin()

                val res = Restaurant(
                    mTempStorage.restaurantName!!,
                    mTempStorage.restaurantLogo!!,
                    mTempStorage.restaurantAddress!!,
                    Menu(),
                    admin
                )

                res.id = Firestore.documentId()

                storage.upload(Uri.parse(res.logo), Const.LOGO_PATH,
                    {
                        // Storage onSuccess
                        res.logo = it
                        firestore.upload(res, Const.RESTAURANT_PATH, res.id!!,
                            {
                                // Firestore onSuccess
                                startActivity(SignInActivity.instanceWithClearStack(baseContext))
                                finish()
                            }, {
                                registerButtonVisibility(true)
                            })
                    }, {
                        registerButtonVisibility(true)
                    }, {})
            }, {
                registerButtonVisibility(true)
            })
    }

    companion object {
        fun instance(context: Context): Intent {
            return Intent(context, AdminInfoActivity::class.java)
        }
    }
}