package com.example.schoollifeproject.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class Notice(
    @Expose
    @SerializedName("noticeID")
    private var noticeID: Int,

    @Expose
    @SerializedName("noticeTitle")
    private var noticeTitle: String,

    @Expose
    @SerializedName("userID")
    private var userID: String,

    @Expose
    @SerializedName("noticeDate")
    private var noticeDate: String,
    @Expose
    @SerializedName("noticeContent")
    private var noticeContent: String,

    @Expose
    @SerializedName("noticeAvailable")
    private var noticeAvailable: Int,
) {

    fun getNoticeKey(): Int {
        return noticeID
    }

    fun getNoticeTitle(): String {
        return noticeTitle
    }

    fun getNoticeWriter(): String {
        return userID
    }

    fun getNoticeDate(): String {
        return noticeDate
    }

    fun getNoticeContent(): String {
        return noticeContent
    }

    fun getNoticeAvailable(): Int {
        return noticeAvailable
    }

}