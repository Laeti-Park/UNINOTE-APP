package com.example.schoollifeproject

class ItemInfo(private var itemID: String, private var title: String, private var content: String?) {
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
}