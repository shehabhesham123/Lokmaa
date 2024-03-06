package com.example.lokma.ui

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import com.example.lokma.R
import com.example.lokma.databinding.ActivityIntroBinding
import com.example.lokma.pojo.Intro

class IntroActivity : AppCompatActivity(), ViewHolder {
    private lateinit var mBinding: ActivityIntroBinding
    private lateinit var mPreferencesEditor: SharedPreferences.Editor
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityIntroBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        mPreferencesEditor = getPreferences(Context.MODE_PRIVATE).edit()

        mBinding.viewPager.adapter = Adapter2(getIntros(), this)
        mBinding.viewPager.isUserInputEnabled = false
        mBinding.MotivationalFragmentButtonContinue.setOnClickListener {
            if (mBinding.viewPager.currentItem == 0) {
                mBinding.viewPager.currentItem = 1
                mBinding.MotivationalFragmentButtonContinue.isEnabled = false
            }
        }

        mBinding.MotivationalFragmentButtonSignIn.setOnClickListener {
            mPreferencesEditor.putBoolean(SplashActivity.IS_FIRST_OPEN, false)
            mPreferencesEditor.apply()
            val intent = LoginActivity.instance(baseContext)
            startActivity(intent)
            finish()
        }
    }

    private fun getIntros(): MutableList<Intro> {
        return mutableListOf(
            Intro(
                ResourcesCompat.getDrawable(resources, R.drawable.motivational1, null)!!,
                ResourcesCompat.getDrawable(resources, R.drawable.motivational2, null)!!,
                ResourcesCompat.getDrawable(resources, R.drawable.motivational3, null)!!,
                ResourcesCompat.getDrawable(resources, R.drawable.motivational4, null)!!,
                resources.getString(R.string.sentence1),
                resources.getString(R.string.sentence2),
            ),
            Intro(
                ResourcesCompat.getDrawable(resources, R.drawable.sec_motivational1, null)!!,
                ResourcesCompat.getDrawable(resources, R.drawable.sec_motivational2, null)!!,
                ResourcesCompat.getDrawable(resources, R.drawable.sec_motivational3, null)!!,
                ResourcesCompat.getDrawable(resources, R.drawable.motivational4, null)!!,
                resources.getString(R.string.sentence3),
                resources.getString(R.string.sentence4),
            )
        )
    }

    companion object {
        fun instance(context: Context): Intent {
            return Intent(context, IntroActivity::class.java)
        }
    }

    class VH(viewItem: View) : ViewHolder2(viewItem) {
        private val mainImage = viewItem.findViewById<ImageView>(R.id.mainImage)
        private val subImage1 = viewItem.findViewById<ImageView>(R.id.subImage1)
        private val subImage2 = viewItem.findViewById<ImageView>(R.id.subImage2)
        private val subImage3 = viewItem.findViewById<ImageView>(R.id.subImage3)
        private val title = viewItem.findViewById<TextView>(R.id.title)
        private val message = viewItem.findViewById<TextView>(R.id.message)

        override fun bind(item: Any,position:Int) {
            val intro = item as Intro
            putImages(intro)
            title.text = intro.title
            message.text = intro.message
        }

        private fun putImages(intro: Intro) {
            mainImage.setImageDrawable(intro.mainImage)
            subImage1.setImageDrawable(intro.subImage1)
            subImage2.setImageDrawable(intro.subImage2)
            subImage3.setImageDrawable(intro.subImage3)
        }
    }

    override fun setOnCreateViewHolder(parent: ViewGroup): ViewHolder2 {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.intro_layout, parent, false)
        return VH(view)
    }
}

