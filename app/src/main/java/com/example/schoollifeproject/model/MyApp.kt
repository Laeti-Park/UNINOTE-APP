package com.example.schoollifeproject.model

/**
 * 서버연결을 위한 PHP path object
 * 작성자 : 이준영, 박동훈
 */
object MyApp {
    const val TAG: String = "로그"

    const val note_update_url:String = "/php/note/note_update.php"
    const val delete_info_uri:String = "/php/user/delete_info.php"
    const val logout_url: String = "/php/user/logout.php"
    const val login_url: String = "/php/user/login.php"
    const val Register_url: String = "/php/user/register.php"
    const val bbs_load_url: String = "/php/note/bbs_load.php"
    const val notice_load_url: String = "/php/note/notice_load.php"
    const val note_delete_url: String = "/php/note/note_delete.php"
    const val info_load_url: String = "/php/note/info_load.php"
    const val note_write_url: String = "/php/note/note_write.php"
    const val item_save_url: String = "/php/item/item_save.php"
    const val item_load_url: String = "/php/item/item_load.php"
    const val map_public_url: String = "/php/map/map_select.php"
    const val map_update_url: String = "/php/map/map_update.php"
    const val map_popular_url: String = "/php/map/map_popular.php"
    const val map_like_url: String = "/php/map/map_like.php"
    const val map_list_url: String = "/php/map/map_list.php"
    const val item_file_save_url: String = "/php/item/item_file_save.php"
    const val item_file_load_url: String = "/php/item/item_file_load.php"
    const val item_file_del_url: String = "/php/item/item_file_del.php"
}