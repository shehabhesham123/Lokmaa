package com.example.admin.ui

import android.content.Context
import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.provider.MediaStore
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.admin.R
import com.example.admin.databinding.ActivitySignupBinding
import com.example.admin.utils.TempStorage

class SignUpActivity : AppCompatActivity() {
    private lateinit var mBinding: ActivitySignupBinding
    private lateinit var register: ActivityResultLauncher<Intent>
    private var tempStorage = TempStorage.instance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        setUpRegister()

        mBinding.SignUpCardViewUploadLogo.setOnClickListener {
            pickImage()
        }

        mBinding.SignUpButtonNext.setOnClickListener {
            if (checkResInfo()){
                startActivity(LocationActivity.instance(baseContext))
            }
        }

        updateUI()
    }

    private fun checkResInfo(): Boolean {
        val resName = mBinding.SignUpInputETName.text.toString()
        if (resName.isEmpty()) {
            Toast.makeText(
                baseContext,
                "Enter your restaurant name",
                Toast.LENGTH_SHORT
            ).show()
            return false
        }
        if (tempStorage.restaurantLogo == null) {
            Toast.makeText(
                baseContext,
                "upload your restaurant logo",
                Toast.LENGTH_SHORT
            ).show()
            return false
        }
        tempStorage.restaurantName = resName
        return true
    }

    private fun setUpRegister() {
        register = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            it.data?.run {
                val image = data
                mBinding.SignUpImageViewLogo.setImageURI(image)
                mBinding.SignUpUploadLogoText.visibility = View.INVISIBLE
                tempStorage.restaurantLogo = image.toString()
            }
        }
    }

    private fun pickImage() {
        val intent = Intent()
        intent.action = Intent.ACTION_PICK
        intent.data = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        register.launch(intent)

    }

    private fun updateUI() {
        val text = resources.getString(R.string.SignUp_tv_HaveAccount)
        val spannableString = SpannableStringBuilder(text)
        val color = ForegroundColorSpan(resources.getColor(R.color.primaryColor, null))
        val bold = StyleSpan(Typeface.BOLD)
        val clickable = object : ClickableSpan() {
            override fun onClick(widget: View) {
                startActivity(SignInActivity.instance(baseContext))
            }
        }
        spannableString.setSpan(color, 17, text.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        spannableString.setSpan(bold, 17, text.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        spannableString.setSpan(clickable, 17, text.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

        mBinding.SignUpLoTvHave.text = spannableString
        mBinding.SignUpLoTvHave.movementMethod = LinkMovementMethod()
    }

    companion object {
        fun instance(context: Context): Intent {
            return Intent(context, SignUpActivity::class.java)
        }
    }
}