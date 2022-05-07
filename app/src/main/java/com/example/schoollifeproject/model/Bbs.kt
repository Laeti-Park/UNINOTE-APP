package com.example.schoollifeproject.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 * 게시글을 JSON파일 타입으로 호출하기위한 Class
 * */
class Bbs(
    @Expose
    @SerializedName("bbsID")
    private var bbsID: Int,

    @Expose
    @SerializedName("bbsTitle")
    private var bbsTitle: String,

    @Expose
    @SerializedName("userID")
    private var userID: String,

    @Expose
    @SerializedName("bbsDate")
    private var bbsDate: String,
    @Expose
    @SerializedName("bbsContent")
    private var bbsContent: String,

    ) {

    fun getBbsKey(): Int {
        return bbsID
    }

    fun getBbsTitle(): String {
        return bbsTitle
    }

    fun getBbsWriter(): String {
        return userID
    }

    fun getBbsDate(): String {
        return bbsDate
    }

    fun getBbsContent(): String {
        return bbsContent
    }

}