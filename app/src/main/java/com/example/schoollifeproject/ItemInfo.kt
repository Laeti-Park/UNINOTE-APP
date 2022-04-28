package com.example.schoollifeproject

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class ItemInfo(@Expose
               @SerializedName("itemID")
               private var itemID: String,
               @Expose
               @SerializedName("itemTitle")
               private var title: String,
               @Expose
               @SerializedName("itemContent")
               private var content: String?,
               @Expose
               @SerializedName("itemNum")
               private var num: Int?) {
    fun getItemID(): String {
        return itemID
    }
    fun setItemID(itemID: String) {
        this.itemID = itemID
    }
    fun getTitle(): String {
        return title
    }
    fun setTitle(title: String) {
        this.title = title
    }
    fun getContent(): String? {
        return content
    }
    fun setContent(content: String) {
        this.content = content
    }
    fun getNum(): Int? {
        return num
    }
}