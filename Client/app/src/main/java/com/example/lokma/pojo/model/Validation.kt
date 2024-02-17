package com.example.lokma.pojo.model

import android.content.Context
import android.net.Uri
import android.util.Patterns
import com.example.lokma.pojo.constant.Constant
import java.util.regex.Matcher
import java.util.regex.Pattern


class Validation {
    companion object {
        fun checkEmail(email: String): Boolean {
            return (email.isNotEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches())
        }

        fun checkPassword(password: String): Boolean {
            return (password.isNotEmpty() && password.trim().length >= 8)
        }

        fun checkName(name: String?): Boolean {
            name?.let {
                for (i in name)
                    if (i !in ('a'..'z') && i !in ('A'..'Z') && i !in ('0'..'9') && i != '_') return false
                return true
            }
            return false
        }

        private fun passwordIsValid(password: String): Boolean {
            val pattern: Pattern
            val passwordPattern =
                "^(?=.*[A-Za-z])(?=.*\\\\d)(?=.*[$@$!%*#?&])[A-Za-z\\\\d$@$!%*#?&]{8,}$"
            pattern = Pattern.compile(passwordPattern)
            val matcher: Matcher = pattern.matcher(password)

            return matcher.matches()

        }

        fun imageIsValid(image: Uri): Uri {
            if (image != Uri.parse("null")) return image
            throw ValidationException("Restaurant image is null")
        }

        fun stringIsValid(string: String): String {
            if (string.isNotBlank() && string.isNotEmpty() && string != "null") return string
            throw ValidationException("Restaurant name or type is null")
        }

        fun ratingIsValid(rating: Float): Float {
            if (rating in 0.0..5.0) return rating
            throw ValidationException("Restaurant rating is not valid")
        }

        fun timeIsValid(time: Float): Float {
            if (time in 15.0..120.0) return time
            throw ValidationException("Restaurant avg delivery time is not valid !in 15 .. 60")
        }

        fun costIsValid(cost: Float): Float {
            if (cost in 0.0..100.0) return cost
            throw ValidationException("Restaurant avg delivery cost is not valid !in 0 .. 100")
        }

        fun isOnline(context: Context): Boolean {
            return context.getSharedPreferences(
                Constant.sharedPreferencesName,
                Context.MODE_PRIVATE
            ).getBoolean(Constant.networkConnectionKey, false)
        }

        class ValidationException(message: String) : Exception(message)
    }
}