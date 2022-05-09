package com.example.schoollifeproject.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class MapListModel (
    @Expose
    @SerializedName("userID")
    private var mapID: String,
    @Expose
    @SerializedName("hits")
    private var mapHits: Int,
    @Expose
    @SerializedName("recommend")
    private var mapRecommend: Int
){
    fun getMapID(): String{
        return mapID
    }
    fun getMapHits(): Int{
        return mapHits
    }
    fun getMapRecommend(): Int{
        return mapRecommend
    }
}