package com.example.lokma.pojo.listener

import com.example.lokma.pojo.model.Location
import java.io.Serializable

interface LocationListener:Serializable {
    fun setOnClickOnConfirmation(location: Location)
}