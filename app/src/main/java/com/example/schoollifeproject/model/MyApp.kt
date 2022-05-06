package com.example.schoollifeproject.model

object MyApp {
    const val TAG: String = "로그"

    // * 서버에 있는 php 파일 위치
    //  ! 서버 IP 제외
    // ex) /파일명.php
    const val Select_url: String = "/login_select.php"
    const val Register_url: String = "/register_insert.php"
    const val notice_key_search_url: String = "/notice_key_search.php"
    const val notice_load_url: String = "/notice_load.php"
    const val notice_save_url: String = "/notice_insert.php"
    const val item_save_url: String = "/item_save.php"
    const val item_load_url: String = "/item_load.php"
    const val map_public_url: String = "/map_select.php"
    const val map_update_url: String = "/map_update.php"
    const val map_popular_url: String = "/map_popular.php"
    const val map_like_url: String = "/map_like.php"
    const val map_files_url: String = "/map_files.php"
    const val notice_open_url: String = "/notice_open.php"
}