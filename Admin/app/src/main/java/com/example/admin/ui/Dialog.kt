package com.example.admin.ui

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import androidx.fragment.app.DialogFragment
import com.example.admin.databinding.DialogViewBinding

class Dialog(private var listener: CategoryListener) : DialogFragment() {


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val mBinding = DialogViewBinding.inflate(LayoutInflater.from(requireContext()))

        mBinding.done.setOnClickListener {
            val categoryName = mBinding.categoryName.text.toString()
            listener.onAddCategory(categoryName)
            dismiss()
        }
        mBinding.cancel.setOnClickListener {
            dismiss()
        }

        return AlertDialog.Builder(requireContext())
            .setView(mBinding.root)
            .create()
    }
}