package com.example.schoollifeproject.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class FileModel(
    @Expose
    @SerializedName("fileName")
    private val fileName: String,
    @Expose
    @SerializedName("fileRealName")
    private val fileRealName: String,
    ext: String
) {
    fun getFileName():String {
        return fileName
    }
    fun getFileRealName():String {
        return fileRealName
    }
}