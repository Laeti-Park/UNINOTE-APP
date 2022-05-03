package com.example.schoollifeproject.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.gyso.treeview.model.NodeModel

class ItemInfo(@Expose
               @SerializedName("itemID")
               private var itemID: String,
               @Expose
               @SerializedName("itemContent")
               private var content: String,
               @Expose
               @SerializedName("itemCount")
               private var num: Int?,
               @Expose
               @SerializedName("noteContent")
               private var note: String?) {
    fun getItemID(): String {
        return itemID
    }
    fun setItemID(itemID: String) {
        this.itemID = itemID
    }
    fun getContent(): String {
        return content
    }
    fun setContent(content: String) {
        this.content = content
    }
    fun getNote(): String? {
        return note
    }
    fun setNote(note: String) {
        this.note = note
    }
    fun getNum(): Int? {
        return num
    }
}