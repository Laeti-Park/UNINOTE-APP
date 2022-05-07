package com.example.schoollifeproject.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class MapModel (
    @Expose
    @SerializedName("userID")
    private var mapID: String
){
    fun getMapID(): String{
        return mapID
    }
}