package com.example.schoollifeproject.model

/**
 * 게시글 정보를 저장하기 위한 Model
 * 작성자 : 이준영
 */
class NoteListContacts(
    var loginID: String,
    var noteID: Int,
    var noteTitle: String,
    var userID: String,
    var noteDate: String,
    var noteContent: String,
    var noteAvailable: Int
)