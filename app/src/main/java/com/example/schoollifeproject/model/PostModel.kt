package com.example.schoollifeproject.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.File

/**
 * 서버에서 return된 값이 저장되는 DATA 클래스
 * */
data class PostModel(
    @Expose
    @SerializedName("type")
    var type: Int? = null,
    @Expose
    @SerializedName("userID")
    var userID: String? = null,
    @Expose
    @SerializedName("userPassword")
    var userPassword: String? = null,
    @Expose
    @SerializedName("userName")
    var userName: String? = null,
    @Expose
    @SerializedName("error")
    var error: String? = null,
    @Expose
    @SerializedName("countBbsKey")
    var countBbsKey: Int? = null,
    @Expose
    @SerializedName("noticeTitle")
    var noticeTitle: String? = null,
    @Expose
    @SerializedName("noticeName")
    var noticeName: String? = null,
    @Expose
    @SerializedName("noticeDate")
    var noticeDate: String? = null,
    @Expose
    @SerializedName("noticeKey")
    var noticeKey: Int? = null,
    @Expose
    @SerializedName("noticeContents")
    var noticeContents: String? = null,
    @Expose
    @SerializedName("itemCount")
    var itemCount: Int? = null,
    @Expose
    @SerializedName("publicMap")
    var public: Int? = null,
    @Expose
    @SerializedName("mapPassword")
    var mapPassword: String? = null,
    @Expose
    @SerializedName("hits")
    var mapHit: Int? = null,
    @Expose
    @SerializedName("recommend")
    var mapRecommend: Int? = null,
    @Expose
    @SerializedName("countNoticeKey")
    var countNoticeKey: Int? = null,
    @Expose
    @SerializedName("key")
    var key: Int? = null
)
