package com.example.admin.ui

import android.content.Context
import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.admin.R
import com.example.admin.backend.firebase.NormalAuth
import com.example.admin.databinding.ActivitySigninBinding
import com.example.admin.pojo.Admin

class SignInActivity : AppCompatActivity() {
    private lateinit var mBinding: ActivitySigninBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivitySigninBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        mBinding.SignInBtnSignIn.setOnClickListener {
            val admin = checkUserInfo()
            if (admin != null) {
                val auth = NormalAuth(baseContext)
                auth.signIn("${admin.username}@lokma.com", admin.password!!, {
                    // go to main activity
                }, {
                    Toast.makeText(baseContext, it, Toast.LENGTH_SHORT).show()
                })
            }
        }
        updateUI()
    }

    fun checkUserInfo(): Admin? {
        val usernameText = mBinding.SignInInputETUsername.text.toString()
        val passwordText = mBinding.SignInInputETPassword.text.toString()
        if (usernameText.isEmpty() && usernameText.isBlank()) {
            mBinding.SignInInputETUsername.error = resources.getString(R.string.usernameError)
            return null
        }
        if (passwordText.isEmpty() && passwordText.isBlank()) {
            mBinding.SignInInputETPassword.error = resources.getString(R.string.passwordError)
            return null
        } else if (passwordText.length < 8) {
            mBinding.SignInInputETPassword.error = resources.getString(R.string.passwordError2)
            return null
        }
        return Admin(usernameText, passwordText)
    }

    private fun updateUI() {
        val createNewAccount = resources.getString(R.string.SignIn_CreateNewAccount)
        val spannableString = SpannableStringBuilder(createNewAccount)
        val textColor = ForegroundColorSpan(resources.getColor(R.color.primaryColor, null))
        val bold = StyleSpan(Typeface.BOLD)
        val clickable = object : ClickableSpan() {
            override fun onClick(widget: View) {
                val intent = SignUpActivity.instance(baseContext)
                startActivity(intent)
            }
        }
        spannableString.setSpan(
            textColor,
            23,
            createNewAccount.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        spannableString.setSpan(
            clickable,
            23,
            createNewAccount.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        spannableString.setSpan(
            bold,
            23,
            createNewAccount.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        mBinding.SignInCreateNewAccount.text = spannableString
        mBinding.SignInCreateNewAccount.movementMethod = LinkMovementMethod()
    }

    companion object {
        fun instance(context: Context): Intent {
            return Intent(context, SignInActivity::class.java)
        }
    }
}