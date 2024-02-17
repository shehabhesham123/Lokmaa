package com.example.lokma.ui

import android.content.IntentFilter
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.lokma.R
import com.example.lokma.pojo.broadcast.NetworkConnection
import com.example.lokma.pojo.constant.Constant
import com.example.lokma.pojo.listener.*
import com.example.lokma.pojo.model.*
import kotlin.math.log

class MainActivity : AppCompatActivity(),
    SplashListener,
    MotivationalListener,
    SignInListener,
    ArrowBackListener,
    SignUpListener,
    RestaurantListener,
    CongratulationListener,
    HomeListener,
    LocationListener,
    CartListener2,
    AddToCartListener
/*VerificationListener,
ForgetPasswordListener,

*/ {

    // register the broadcast after Motivational fragment
    private lateinit var networkConnectionBroadcast: NetworkConnection
    private lateinit var stack: Stack
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var editSharedPreferences: SharedPreferences.Editor

    private var userEmail: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        stack = Stack()
        sharedPreferences = getSharedPreferences(Constant.sharedPreferencesName, MODE_PRIVATE)
        editSharedPreferences = sharedPreferences.edit()

        // معملتهاش ف ال onStart عشان لو عملناها هناك كل ما يطلع ويخش هيستدعى دى
        goToFragment(SplashFragment(), false)

    }

    override fun onStart() {
        super.onStart()
        registerBroadcast()
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterBroadcast()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == PERMISSION_REQ && grantResults.isNotEmpty() && grantResults.size >= 2 &&
            grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED
        ) {
            val locationFragment = stack.getRunningFragment()
            if (locationFragment is LocationFragment) locationFragment.permissionsIsAllowed()
        }

    }

    override fun onBackPressed() {
        //
        try {
            val fragment = stack.backPress()
            goToFragment(fragment, false)    // false --> because this fragment added before
        } catch (e: NullPointerException) {
            //Toast.makeText(baseContext, e.message, Toast.LENGTH_SHORT).show()
            val dialog = AlertDialog.Builder(this)
                .setMessage("Do you want to exit?")
                .setPositiveButton("Yes") { _, _ ->
                    super.onBackPressed()
                }
                .setNegativeButton("No") { _, _ ->

                }
            dialog.create().show()
        }
    }

    // SplashListener
    override fun splashFragmentIsEnded(userEmail: String?) {
        this.userEmail = userEmail
        Log.i("aaa","${this.userEmail}")
        if (userEmail.isNullOrEmpty()) {
            // two properties
            // 1- first open --> go to MotivationalFragment()
            // 2- make sign out ---> go to loginFragment()
            if (isFirstOpen())
                goToFragment(MotivationalFragment(), true)
            else {
                goToFragment(SignInFragment(), true)
            }
        } else {
            // already login
            stack.clearStack()
            goToFragment(AllRestaurantFragment(), true)
        }
    }

    // MotivationalListener
    override fun setOnClickOnContinue(nextFragment: Fragment) {
        if (nextFragment is SignInFragment) stack.clearStack()
        goToFragment(nextFragment, true)
    }

    override fun setOnClickOnSignIn() {
        goToFragment(SignInFragment(), true)
    }

    //SignInListener
    override fun setOnClickOnForgetPassword(email:String) {
        //goToFragment(ForgetPasswordFragment(), true)
    }

    override fun setOnClickOnLogin(userEmail: String?) {
        // user is done
        this.userEmail = userEmail
        stack.clearStack()
        goToFragment(AllRestaurantFragment(), true)
    }

    override fun setOnClickOnSignUp() {
        goToFragment(SignUpFragment(), true)
    }

    override fun setOnClickOnSignWithGoogle() {

    }

    override fun setOnClickOnSignWithApple() {

    }

    // SignUpListener
    override fun setOnClickRegister(userEmail   : String?) {
        // user is
        this.userEmail = userEmail
        stack.clearStack()
        goToFragment(CongratulationFragment(), false)
    }

    override fun setOnClickSignIn() {
        // return to signIn fragment
        val fragment = stack.backPress()
        goToFragment(fragment, false)
    }

    //ArrowBackListener
    override fun setOnClickOnArrowBack() {
        try {
            val fragment = stack.backPress()
            goToFragment(fragment, false)    // false --> because this fragment added before
        } catch (e: NullPointerException) {

        }
    }

    override fun setOnClickOnArrowBack(parentFragment: Stack.FragmentName) {
        try {
            val fragment = stack.returnToParent(parentFragment)
            goToFragment(fragment, false)    // false --> because this fragment added before
        } catch (e: NullPointerException) {

        }
    }

    // RestaurantListener
    override fun setOnClickOnRestaurant(restaurant: Restaurant) {
        //goToFragment(HomeFragment.instance(restaurant, userEmail), true)
        goToFragment(OneRestaurantFragment.instance(restaurant, this.userEmail!!), true)
        //goToFragment(OneRestaurantFragment(), true)
    }

    // CongratulationListener
    override fun setOnClickOnGetStarted() {
        stack.clearStack()
        goToFragment(AllRestaurantFragment(), true)
    }

    // HomeListener
    override fun setOnClickOnLocation() {
        goToFragment(LocationFragment.instance(userEmail), true)
    }

    override fun setOnClickOnDiscount(food: Food) {

    }

    override fun setOnClickOnFood(food: Food) {

    }

    private fun isFirstOpen(): Boolean {
        val isFirstOpen = sharedPreferences.getBoolean(Constant.firstOpenKey, true)
        if (isFirstOpen) {
            editSharedPreferences.putBoolean(Constant.firstOpenKey, false)
            editSharedPreferences.apply()
        }
        return isFirstOpen
        //return true
    }

    private fun goToFragment(fragment: Fragment, isAddToStack: Boolean) {
        try {
            val transaction = supportFragmentManager.beginTransaction()
            transaction.replace(R.id.MainActivity_FrameLayout, fragment)
            if (isAddToStack) stack.addToBackStack(fragment)
            transaction.commit()
        } catch (e: Exception) {
            Toast.makeText(baseContext, "Error:(${e.message})", Toast.LENGTH_SHORT).show()
        }
    }

    private fun registerBroadcast() {
        networkConnectionBroadcast =
            NetworkConnection(findViewById(R.id.MainActivity_TextView_NetworkState)) {
                val runningFragment = stack.getRunningFragment()
                if (runningFragment is LocationFragment) {
                    runningFragment.refresh()
                }
                if (runningFragment is AllRestaurantFragment) {
                    runningFragment.refresh()
                }

            }
        val intentFilter = IntentFilter("android.net.conn.CONNECTIVITY_CHANGE")
        registerReceiver(networkConnectionBroadcast, intentFilter)
    }

    private fun unregisterBroadcast() {
        unregisterReceiver(networkConnectionBroadcast)
    }

    override fun setOnClickOnConfirmation(location: Location) {
        stack.backPress()
        val runningFragment = stack.getRunningFragment() as OneRestaurantFragment
        runningFragment.refresh()
        goToFragment(runningFragment, false)
    }

    override fun setOnClickOnChangeLocation() {
        goToFragment(LocationFragment.instance(userEmail), true)
    }

    override fun restore(): MutableList<Item> {
        return this.cartItems
    }

    private var cartItems = mutableListOf<Item>()
    override fun setOnClickOnAddToCart(item: Item) {
        this.cartItems.add(item)
        for (i in this.cartItems){
            Log.i("aaa","${i.food.name}")
        }
    }

/*


    

    // VerificationListener
    override fun setOnClickOnContinue() {

    }

    // ForgetPasswordListener
    override fun setOnClickOnContinueForgetPassword(str: String, isPhone: Boolean) {
        goToFragment(VerificationEmailFragment.instance(str, isPhone), true)
    }





    








*/
}