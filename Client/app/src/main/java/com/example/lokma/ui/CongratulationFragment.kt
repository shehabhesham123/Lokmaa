package com.example.lokma.ui

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.lokma.R
import com.example.lokma.databinding.FragmentCongratulationBinding
import com.example.lokma.pojo.listener.CongratulationListener

class CongratulationFragment : Fragment() {

    private lateinit var binding: FragmentCongratulationBinding
    private lateinit var listener: CongratulationListener

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is CongratulationListener) listener = context
        else throw Exception("You must implements from CongratulationListener")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_congratulation, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentCongratulationBinding.bind(requireView())

        binding.CongratulationBtnGetStarted.setOnClickListener {
            listener.setOnClickOnGetStarted()
        }
    }

}