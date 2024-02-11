package com.example.admin.ui

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
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
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityAdminInfoBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        setSupportActionBar(mBinding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        mBinding.SignInBtnRegister.setOnClickListener {
            if (check()) {
                val username = mBinding.SignInInputETUsername.text.toString()
                val password = mBinding.SignInInputETPassword.text.toString()
                val phone = mBinding.SignInInputETPhone.text.toString()
                val tempStorage = TempStorage.instance()

                val admin = Admin(username, password, phone)
                val firestore = Firestore(baseContext)
                val storage = Storage(baseContext)
                val auth = NormalAuth(baseContext)

                auth.signUp("${admin.username}@lokma.com", admin.password!!, {
                    firestore.upload(admin, Const.ADMIN_PATH, admin.username, {

                    }, {
                        Snackbar.make(mBinding.SignInInputETUsername,it,Snackbar.LENGTH_SHORT).show()
                    })
                    admin.successfulLogin()
                    val res = Restaurant(
                        tempStorage.restaurantName!!,
                        tempStorage.restaurantLogo!!,
                        tempStorage.restaurantAddress!!,
                        Menu(),
                        admin
                    )
                    res.id = Firestore.documentId
                    Log.i("shehabhesham","id: ${res.id}")
                    Log.i("shehabhesham","username: ${res.admin!!.username}")
                    storage.upload(Uri.parse(res.logo), Const.LOGO_PATH, {
                        res.logo = it

                        firestore.upload(res, Const.RESTAURANT_PATH, res.id!!, {
                            startActivity(SignInActivity.instanceWithClearStack(baseContext))
                            finish()
                        }, {})
                    }, {}, {})

                }, {})


            }
        }
    }

    private fun check(): Boolean {
        val username = mBinding.SignInInputETUsername.text.toString()
        val password = mBinding.SignInInputETPassword.text.toString()
        val phone = mBinding.SignInInputETPhone.text.toString()
        if (username.isEmpty()) {
            mBinding.SignInInputETUsername.error = "Enter your username"
            return false
        }
        if (password.isEmpty()) {
            mBinding.SignInInputETPassword.error = "Enter your password"
            return false
        } else if (password.length < 8) {
            mBinding.SignInInputETPassword.error =
                "password must be 8 letter at least"
            return false
        }
        if (phone.isEmpty()) {
            mBinding.SignInInputETPhone.error = "Enter your phone"
            return false
        } else if (phone.length != 11) {
            mBinding.SignInInputETPhone.error = "Enter corrent number"
            return false
        }
        return true
    }

    companion object {
        fun instance(context: Context): Intent {
            return Intent(context, AdminInfoActivity::class.java)
        }
    }
}