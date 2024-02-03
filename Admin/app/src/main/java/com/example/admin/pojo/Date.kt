package  com.example.admin.pojo

import android.annotation.SuppressLint
import java.text.SimpleDateFormat
import java.util.Date

class Date {

    companion object {
        @SuppressLint("SimpleDateFormat")
        private val simpleDateFormat = SimpleDateFormat("dd/M/yyyy hh:mm:ss")
        val currentDate: String
            get() {
                return simpleDateFormat.format(Date())
            }

    }
}