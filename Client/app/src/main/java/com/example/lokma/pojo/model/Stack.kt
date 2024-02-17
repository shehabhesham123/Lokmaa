package com.example.lokma.pojo.model

import androidx.fragment.app.Fragment
import com.example.lokma.pojo.model.Stack.FragmentName.Companion.fromFragmentToFragmentName
import com.example.lokma.ui.SplashFragment
import java.util.Stack

class Stack {

    private var stack = Stack<Pair<FragmentName, Fragment>>()

    enum class FragmentName {
        SPLASH,
        MOTIVATIONAL,
        SEC_MOTIVATIONAL,
        SIGN_IN,
        NULL;

        companion object {
            fun fromFragmentToFragmentName(fragment: Fragment): FragmentName {
                return when (fragment) {
                    is SplashFragment -> FragmentName.SPLASH
                    /*is MotivationalFragment -> FragmentName.MOTIVATIONAL
                    is SecMotivationalFragment -> FragmentName.SEC_MOTIVATIONAL
                    is SignInFragment -> FragmentName.SIGN_IN*/
                    else -> FragmentName.NULL
                }
            }
        }
    }


    fun addToBackStack(fragment: Fragment) {
        stack.push(Pair(fromFragmentToFragmentName(fragment), fragment))
    }

    fun clearStack() {
        stack.clear()
    }

    fun getRunningFragment(): Fragment? {
        if (stack.isNotEmpty())
            return stack.peek().second
        return null
    }

    fun backPress(): Fragment {
        if (stack.isNotEmpty()) stack.pop()
        if (stack.isNotEmpty()) return stack.peek().second
        else throw NullPointerException("Stack is null")
    }

    fun returnToParent(fragmentName: FragmentName): Fragment {
        while (stack.isNotEmpty()) {
            val fragment = stack.pop()
            if (fragment.first == fragmentName) {
                stack.add(fragment)
                return fragment.second
            }
        }
        throw NullPointerException("Stack is null")
    }
}