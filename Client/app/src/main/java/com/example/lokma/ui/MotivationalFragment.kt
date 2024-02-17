package com.example.lokma.ui

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.lokma.R
import com.example.lokma.databinding.FragmentMotivationalBinding
import com.example.lokma.pojo.listener.MotivationalListener

class MotivationalFragment : Fragment() {

    private lateinit var binding: FragmentMotivationalBinding
    private lateinit var listener: MotivationalListener

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is MotivationalListener) listener = context
        else throw Exception("you must implements from MotivationalListener (MotivationalFragment:19)")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_motivational, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentMotivationalBinding.bind(requireView())

        binding.MotivationalFragmentButtonContinue.setOnClickListener {
            listener.setOnClickOnContinue(SecMotivationalFragment())
        }
        binding.MotivationalFragmentButtonSignIn.setOnClickListener {
            listener.setOnClickOnSignIn()
        }
    }
}

