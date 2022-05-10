package com.example.schoollifeproject.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class NoteListContacts(
    var loginID: String,
    var noteID: Int,
    var noteTitle: String,
    var userID: String,
    var noteDate: String,
    var noteContent: String,
    var noteAvailable: Int
) {
}