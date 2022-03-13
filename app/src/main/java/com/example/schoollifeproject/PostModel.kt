package com.example.schoollifeproject

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class PostModel(
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
    var error: String? = null


)
