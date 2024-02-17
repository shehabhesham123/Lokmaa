package com.example.lokma.ui

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.lokma.R
import com.example.lokma.databinding.FragmentSplashBinding
import com.example.lokma.firebase.Auth
import com.example.lokma.pojo.constant.Constant
import com.example.lokma.pojo.listener.SplashListener

class SplashFragment : Fragment() {

    private lateinit var listener: SplashListener
    private lateinit var binding: FragmentSplashBinding
    private lateinit var auth: Auth

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is SplashListener) listener = context
        else throw Exception("You must implements from SplashListener (SplashFragment:24)")

        // initialize auth variable
        auth = Auth(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_splash, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentSplashBinding.bind(view)
        // get current user
        val userEmail = auth.currentUser()
        // visible progress bar after 1 sec
        Handler().postDelayed({
            binding.SplashFragmentAnimationView.visibility = View.VISIBLE
        }, 1000)
        // after 2 sec destroy this fragment
        Handler().postDelayed({
            listener.splashFragmentIsEnded(userEmail)
        }, 2500)
    }
}