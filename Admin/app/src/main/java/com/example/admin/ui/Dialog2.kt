package com.example.admin.ui

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.admin.R
import com.example.admin.databinding.DialogView2Binding
import com.example.admin.pojo.AddMealListener
import com.example.admin.pojo.Meal
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.android.material.textfield.TextInputEditText

class Dialog2 constructor(private var listener: AddMealListener) : DialogFragment() {

    private lateinit var register: ActivityResultLauncher<Intent>
    private lateinit var mBinding: DialogView2Binding
    private var types = mutableListOf<Meal.Type>()
    private var image: String? = null
    override fun onAttach(context: Context) {
        super.onAttach(context)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        register = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            image = it.data?.data.toString()
            mBinding.OneFoodImageViewFoodImage.setImageURI(Uri.parse(image))
            mBinding.SignUpUploadLogoText.visibility = View.INVISIBLE
        }

    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        mBinding = DialogView2Binding.inflate(LayoutInflater.from(requireContext()))
        mBinding.OneFoodImageViewFoodImage.setOnClickListener {
            pickImage()
        }
        mBinding.RcyclerView.layoutManager =
            LinearLayoutManager(requireContext())
        mBinding.RcyclerView.adapter = TypeAdapter(types)
        mBinding.adf.extendedFloatingActionButton.setOnClickListener {
            val typeName =mBinding.adf.TypeName.text.toString()
            val typePrice =mBinding.adf.typePrice.text.toString().toFloat()
            types.add(Meal.Type(typeName,typePrice))
            mBinding.RcyclerView.adapter?.notifyItemInserted(types.size-1)
        }

        mBinding.done.setOnClickListener {
            val name = mBinding.SignUpInputETName.text.toString()
            if (name.isNotEmpty()) {
                image?.let {
                    listener.onClickDone(Meal(name, it, types))
                    dismiss()
                }
            }
        }
        mBinding.cancel.setOnClickListener {
            dismiss()
        }

        return AlertDialog.Builder(requireContext())
            .setView(mBinding.root)
            .create()
    }

    private fun pickImage() {
        val intent = Intent()
        intent.action = Intent.ACTION_PICK
        intent.data = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        register.launch(intent)

    }

    class TypeAdapter(var types: MutableList<Meal.Type>) : RecyclerView.Adapter<TypeAdapter.VH>() {
        class VH(view: View) : RecyclerView.ViewHolder(view) {
            val name = view.findViewById<TextInputEditText>(R.id.TypeName)
            val price = view.findViewById<TextInputEditText>(R.id.typePrice)
            val done =
                view.findViewById<ExtendedFloatingActionButton>(R.id.extendedFloatingActionButton)

            init {
                done.visibility = View.GONE
            }
            fun bind(type: Meal.Type) {
                name.setText(type.name)
                price.setText(type.price.toString())
            }


            fun getType(): Meal.Type {
                return Meal.Type(name.text.toString(), price.text.toString().toFloat())
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
            val view =
                LayoutInflater.from(parent.context).inflate(R.layout.type_layout, parent, false)
            return VH(view)
        }

        override fun getItemCount(): Int {
            return types.size
        }

        override fun onBindViewHolder(holder: VH, position: Int) {
                holder.bind(types[position])
        }
    }
}