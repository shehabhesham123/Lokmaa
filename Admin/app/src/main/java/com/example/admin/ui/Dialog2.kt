package com.example.admin.ui

import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.admin.R
import com.example.admin.databinding.DialogView2Binding
import com.example.admin.pojo.AddMealListener
import com.example.admin.pojo.Meal
import com.example.admin.utils.TempStorage
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.android.material.textfield.TextInputEditText
import com.squareup.picasso.Picasso

class Dialog2(private var listener: AddMealListener) : DialogFragment(), ViewHolder {

    private lateinit var mBinding: DialogView2Binding
    private lateinit var mContentRegister: ActivityResultLauncher<Intent>

    private lateinit var meal: Meal

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mContentRegister =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                meal.image = it.data?.data.toString()
                mBinding.OneFoodImageViewFoodImage.setImageURI(Uri.parse(meal.image))
                mBinding.SignUpUploadLogoText.visibility = View.INVISIBLE
            }
        meal = TempStorage.instance().meal ?: Meal()
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        mBinding = DialogView2Binding.inflate(LayoutInflater.from(requireContext()))

        setUpRecyclerView()
        updateUI()

        mBinding.OneFoodImageViewFoodImage.setOnClickListener {
            pickImage()
        }

        mBinding.adf.extendedFloatingActionButton.setOnClickListener {
            checkType()?.run {
                meal.types.add(this)
                mBinding.RecyclerView.adapter?.notifyItemInserted(meal.types.size - 1)
                mBinding.adf.typePrice.setText("")
                mBinding.adf.TypeName.setText("")
            }
        }

        mBinding.done.setOnClickListener {
            checkMeal()?.run {
                listener.onClickDone(this)
                dismiss()
            }
        }

        mBinding.cancel.setOnClickListener {
            dismiss()
        }

        return AlertDialog.Builder(requireContext())
            .setView(mBinding.root)
            .create()
    }

    private fun setUpRecyclerView() {
        mBinding.RecyclerView.layoutManager = LinearLayoutManager(requireContext())
        mBinding.RecyclerView.adapter = Adapter2(meal.types, this)
    }

    private fun updateUI() {
        try {
            // if image or name is with no value --> will throw exception
            mBinding.SignUpInputETName.setText(meal.name)
            mBinding.SignUpInputETName.isEnabled = false
            Picasso.get().load(meal.image).into(mBinding.OneFoodImageViewFoodImage)
            mBinding.OneFoodImageViewFoodImage.isEnabled = false
            mBinding.SignUpUploadLogoText.visibility = View.GONE
            mBinding.done.visibility = View.GONE
            mBinding.adf.root.visibility = View.GONE
        } catch (_: Exception) {
        }
    }

    private fun checkType(): Meal.Type? {
        val typeName = mBinding.adf.TypeName.text.toString()
        val typePrice = mBinding.adf.typePrice.text.toString()
        if (typeName.isEmpty()) {
            mBinding.adf.TypeName.error = "Enter size"
            return null
        }
        if (typePrice.isEmpty()) {
            mBinding.adf.typePrice.error = "Enter price"
            return null
        }
        return Meal.Type(typeName, typePrice.toFloat())
    }

    private fun checkMeal(): Meal? {
        val name = mBinding.SignUpInputETName.text.toString()
        if (name.isEmpty()) {
            mBinding.SignUpInputETName.error = "Enter meal name"
            return null
        }
        if (meal.image.isEmpty() || meal.image.isBlank()) {
            Toast.makeText(requireContext(), "Upload meal image", Toast.LENGTH_SHORT).show()
            return null
        }
        meal.name = name
        return meal
    }

    private fun pickImage() {
        val intent = Intent()
        intent.action = Intent.ACTION_PICK
        intent.data = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        mContentRegister.launch(intent)
    }

    override fun setOnCreateViewHolder(parent: ViewGroup): ViewHolder2 {
        val view =
            LayoutInflater.from(requireContext()).inflate(R.layout.type_layout, parent, false)
        return TypeViewHolder(view)
    }

    class TypeViewHolder(viewItem: View) : ViewHolder2(viewItem) {
        private val name = itemView.findViewById<TextInputEditText>(R.id.TypeName)
        private val price = itemView.findViewById<TextInputEditText>(R.id.typePrice)
        private val done =
            itemView.findViewById<ExtendedFloatingActionButton>(R.id.extendedFloatingActionButton)

        init {
            done.visibility = View.GONE
            name.isEnabled = false
            price.isEnabled = false
        }

        override fun bind(item: Any) {
            val type = item as Meal.Type
            name.setText(type.name)
            price.setText(type.price.toString())
        }
    }

}