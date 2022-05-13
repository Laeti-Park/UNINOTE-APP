package com.example.schoollifeproject.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 * JSON Type로 온 로드맵 정보를 저장하기 위한 Model
 * 작성자 : 박동훈
 */
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