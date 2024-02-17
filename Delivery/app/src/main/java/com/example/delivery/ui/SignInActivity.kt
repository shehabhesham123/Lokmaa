package com.example.delivery.ui

import android.content.Context
import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.delivery.R
import com.example.delivery.backend.firebase.NormalAuth
import com.example.delivery.databinding.ActivitySignInBinding
import com.example.delivery.pojo.Delivery
import com.example.delivery.utils.TempStorage

class SignInActivity : AppCompatActivity() {
    private lateinit var mBinding: ActivitySignInBinding
    private lateinit var mAuth: NormalAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivitySignInBinding.inflate(layoutInflater)
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
            TempStorage.instance().delivery = Delivery(username, "")
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

    private fun signIn(delivery: Delivery) {
        signButtonVisibility(false)
        mAuth.signIn("${delivery.username}@lokma.com", delivery.password!!,
            {
                // onSuccess
                delivery.successfulLogin()
                TempStorage.instance().delivery = delivery
                startActivity(MainActivity.instance(baseContext))
            }, {
                // onFailure
                signButtonVisibility(true)
                Toast.makeText(baseContext, it, Toast.LENGTH_SHORT).show()
            })
    }

    private fun checkAdminInfo(): Delivery? {
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
        return Delivery(usernameText, passwordText)
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