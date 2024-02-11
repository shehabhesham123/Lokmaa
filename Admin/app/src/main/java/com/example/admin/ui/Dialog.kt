package com.example.admin.ui

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import androidx.fragment.app.DialogFragment
import com.example.admin.AddCategoryListener
import com.example.admin.R
import com.example.admin.databinding.DialogViewBinding

class Dialog : DialogFragment() {

    private lateinit var listener: AddCategoryListener
    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is AddCategoryListener) listener = context
        else throw Exception()
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val b = DialogViewBinding.inflate(LayoutInflater.from(requireContext()))
        b.done.setOnClickListener {
            val categoryName = b.categoryName.text.toString()
            listener.setOnPositionClickListener(categoryName)
            dismiss()
        }

        return AlertDialog.Builder(requireContext())
            .setView(b.root)
            .create()
    }
}