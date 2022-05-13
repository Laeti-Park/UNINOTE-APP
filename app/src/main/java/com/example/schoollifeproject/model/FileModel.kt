package com.example.schoollifeproject.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 * JSON Type로 온 파일 정보를 저장하기 위한 Model
 * 작성자 : 박동훈
 */
class FileModel(
    @Expose
    @SerializedName("fileName")
    private val fileName: String,
    @Expose
    @SerializedName("fileRealName")
    private val fileRealName: String,
    private val ext: String
) {
    fun getFileName():String {
        return fileName
    }
    fun getFileRealName():String {
        return fileRealName
    }
    fun getExt(): String {
        return ext
    }
}