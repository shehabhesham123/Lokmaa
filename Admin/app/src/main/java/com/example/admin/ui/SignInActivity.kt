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
import com.example.admin.utils.TempStorage

class SignInActivity : AppCompatActivity() {
    private lateinit var mBinding: ActivitySigninBinding
    private lateinit var mAuth: NormalAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivitySigninBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        setSupportActionBar(mBinding.toolbar)
        supportActionBar?.title = "Sign in"

        mAuth = NormalAuth(baseContext)

        updateUI()
        mBinding.SignIn.setOnClickListener {
            val admin = checkAdminInfo()
            if (admin != null) {
                signIn(admin)
            }
        }

    }

    override fun onResume() {
        super.onResume()

        val username = mAuth.getCurrentUser()
        if (username != null) {
            TempStorage.instance().admin = Admin(username, "")
            startActivity(MainActivity.instance(baseContext))
        }
    }

    private fun signButtonVisibility(isVisible: Boolean) {
        if (!isVisible) {
            mBinding.SignIn.visibility = View.GONE
            mBinding.LottieAnimation.visibility = View.VISIBLE
        } else {
            mBinding.SignIn.visibility = View.VISIBLE
            mBinding.LottieAnimation.visibility = View.GONE
        }
    }

    private fun signIn(admin: Admin) {
        signButtonVisibility(false)
        mAuth.signIn("${admin.username}@lokma.com", admin.password!!,
            {
                // onSuccess
                admin.successfulLogin()
                TempStorage.instance().admin = admin
                startActivity(MainActivity.instance(baseContext))
            }, {
                // onFailure
                signButtonVisibility(true)
                Toast.makeText(baseContext, it, Toast.LENGTH_SHORT).show()
            })
    }

    private fun checkAdminInfo(): Admin? {
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

        fun instanceWithClearStack(context: Context): Intent {
            val intent = Intent(context, SignInActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            return intent
        }
    }
}