package com.example.lokma.ui

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.example.lokma.R
import com.example.lokma.databinding.FragmentLocationBinding
import com.example.lokma.firebase.Firestore
import com.example.lokma.pojo.constant.Constant
import com.example.lokma.pojo.listener.LocationListener
import com.example.lokma.pojo.model.GoogleMap
import com.example.lokma.pojo.model.Location
import com.example.lokma.pojo.model.User
import com.example.lokma.pojo.model.Validation
import com.google.android.material.snackbar.Snackbar

const val PERMISSION_REQ = 125
private const val USER_KEY = "User"
private const val LISTENER_KEY = "ListenerKey"

class LocationFragment private constructor() : Fragment() {

    private lateinit var binding: FragmentLocationBinding
    private var location: Location? = null       // last location client save it
    private var googleMap: GoogleMap? = null

    private var userEmail: String? = null
    private var user: User? = null
    private lateinit var firestore: Firestore

    private lateinit var listener: LocationListener

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is LocationListener) listener = context
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.apply {
            userEmail = getString(USER_KEY)
        }
        firestore = Firestore(requireContext())
        getUserData()

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_location, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentLocationBinding.bind(view)


        if (!GoogleMap.checkPermission(requireContext())) {
            showRequirePermissionLayout()
        } else {
            permissionsIsAllowed()
        }

        binding.LocationFragmentButtonRequirePermission.setOnClickListener {
            requirePermissions()
        }

        binding.LocationFragmentButtonChangeLocation.setOnClickListener {
            if (requireContext().getSharedPreferences(
                    Constant.sharedPreferencesName,
                    Context.MODE_PRIVATE
                ).getBoolean(Constant.networkConnectionKey, false)
            )
                changeLocation()
        }

        binding.LocationFragmentButtonConfirmation.setOnClickListener {
            if (Validation.isOnline(requireContext())) {
                Log.i("aaa", "$location")
                val data = mutableMapOf<String, Map<String, Any>>()
                data["Address"] =
                    mapOf("Lat" to location!!.latitude, "Long" to location!!.longitude)
                firestore.update(data, Constant.userPath(userEmail!!), {
                    listener.setOnClickOnConfirmation(location!!)
                    onDestroy()
                }, {
                    Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
                })
            }

        }
        binding.LocationFragmentImageViewBack.setOnClickListener {

        }
    }

    fun refresh() {
        if (!GoogleMap.checkPermission(requireContext())) {
            showRequirePermissionLayout()
        } else {
            permissionsIsAllowed()
        }
    }

    private fun getCurrentLocation() {
        try {
            googleMap?.getCurrentLocation { address, location ->
                putAddressInViews(address)
                this.location = location
                locationLoading(false)
            }
        } catch (e: IllegalAccessException) {
            locationLoading(false)
            showRequirePermissionLayout()
        } catch (e: Exception) {
            locationLoading(false)
            Toast.makeText(
                requireContext(),
                "It may be an error occurred or you are not connected to the Internet",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun getDefaultLocation() {

        val defaultLocation = Location.defaultLocation
        location = Location(
            defaultLocation.latitude,
            defaultLocation.longitude
        )
        location?.let {
            googleMap?.moveToLocation(it.getLocation()) { address ->
                putAddressInViews(address)
            }
        }

    }

    private fun putLocationData(location: Location) {
        location.let {
            googleMap?.moveToLocation(it.getLocation()) { address ->
                putAddressInViews(address)
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun putAddressInViews(address: Location.Address) {
        binding.LocationFragmentTextViewShortAddress.text = address.shortAddress
        binding.LocationFragmentTextViewLongAddress.text = address.longAddress
    }

    private fun changeLocation() {
        locationLoading(true)
        getCurrentLocation()
    }

    private fun getUserData() {
        userEmail?.let {
            firestore.downloadOneDocument(Constant.userPath(it), { firestoreDate ->
                firestoreDate.data?.let { data ->
                    user = User.fromMapToObj(firestoreDate.id, data)
                    // user don't have addresses
                    // so add default address
                    if (user?.address == null)
                        user?.address = Location(27.186398, 31.168523)
                    this.location = user?.address
                }
                showLocationLayout()
                createGoogleMapObjectAndMarkLocation()
            }, {
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
            })
        }

    }

    private fun showRequirePermissionLayout() {
        binding.LocationFragmentConstraintLayoutLocationLayout.visibility = View.GONE
        binding.LocationFragmentConstraintLayoutRequirePermission.visibility = View.VISIBLE
    }

    private fun showLocationLayout() {
        binding.LocationFragmentConstraintLayoutLocationLayout.visibility = View.VISIBLE
        binding.LocationFragmentConstraintLayoutRequirePermission.visibility = View.GONE
    }

    private fun locationLoading(isEnable: Boolean) {
        binding.LocationFragmentButtonChangeLocation.isEnabled = !isEnable
        if (isEnable) binding.LocationFragmentProgressBarChangeLocation.visibility = View.VISIBLE
        else binding.LocationFragmentProgressBarChangeLocation.visibility = View.GONE
    }

    private fun requirePermissions() {
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ),
                PERMISSION_REQ
            )
        }
    }

    // will call from mainActivity when allow permission
    fun permissionsIsAllowed() {
        try {

            getUserData()

        } catch (e: Exception) {
            Snackbar.make(
                requireView(),
                "It may be an error occurred or you are not connected to the Internet",
                Snackbar.LENGTH_SHORT
            ).show()
        }

    }

    private fun createGoogleMapObjectAndMarkLocation() {
        googleMap = GoogleMap(
            requireContext(),
            childFragmentManager,
            R.id.LocationFragment_Fragment_GoogleMap
        )

        try {
            if (user!!.address == null) {
                getDefaultLocation()
            } else {
                putLocationData(user!!.address!!)
            }
        } catch (e: Exception) {
            Toast.makeText(
                requireContext(),
                "It may be an error occurred or you are not connected to the Internet",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    companion object {
        fun instance(userEmail: String?): LocationFragment {
            val bundle = Bundle()
            bundle.putString(USER_KEY, userEmail)
            val fragment = LocationFragment()
            fragment.arguments = bundle
            return fragment
        }
    }
}

