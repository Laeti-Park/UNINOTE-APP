package com.example.schoollifeproject.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class InfoListModel(
    @Expose
    @SerializedName("studyboardID")
    private var studyID: Int,
    @Expose
    @SerializedName("studyboardTitle")
    private var studyTitle: String,
    @Expose
    @SerializedName("userID")
    private var userID: String,
    @Expose
    @SerializedName("studyboardDate")
    private var studyDate: String,
    @Expose
    @SerializedName("studyboardContent")
    private var studyContent: String,
    @Expose
    @SerializedName("studyboardAvailable")
    private var studyAvailable: Int,
) {
    fun getStudyKey(): Int {
        return studyID
    }
    fun getStudyTitle(): String {
        return studyTitle
    }
    fun getStudyWriter(): String {
        return userID
    }
    fun getStudyDate(): String {
        return studyDate
    }
    fun getStudyContent(): String {
        return studyContent
    }
    fun getStudyAvailable(): Int{
        return studyAvailable
    }
}