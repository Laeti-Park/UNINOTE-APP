package com.example.schoollifeproject

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*


interface APIS_login {
    @FormUrlEncoded
    @POST(MyApp.Select_url)
    @Headers(
        "accept: application/json",
        "content-type: application/x-www-form-urlencoded; charset=utf-8"
    )
    //post로 서버에 데이터를 보내는 메서드
    fun login_users(
        // 서버에 Post방식으로 보낼 떄 사용하는  파라미터의 키 값
        //ex)@Field('키') =>  $_POST['키']
        @Field("userID") userID: String
    ): Call<PostModel>

    @FormUrlEncoded
    @POST(MyApp.Register_url)
    fun register_users(
        @Field("createID") createID: String,
        @Field("createPassword") createPassword: String,
        @Field("createName") createName: String
    ): Call<PostModel>

    @FormUrlEncoded
    @POST(MyApp.notice_key_search_url)
    fun notice_key_search(
        @Field("dum") dum: Int
    ): Call<PostModel>

    @FormUrlEncoded
    @POST(MyApp.notice_load_url)
    fun notice_load(
        @Field("countKey") countKey: Int
    ): Call<PostModel>

    @FormUrlEncoded
    @POST(MyApp.notice_save_url)
    fun notice_save(
        @Field("noticeTitle") noticeTitle: String,
        @Field("userID") userID: String,
        @Field("date") date: String,
        @Field("noticeContents") noticeContents: String
        ): Call<PostModel>

    @FormUrlEncoded
    @POST(MyApp.notice_open_url)
    fun notice_open(
        @Field("key") key: Int
    ):Call<PostModel>

    @FormUrlEncoded
    @POST(MyApp.item_save_url)
    fun item_save(
        @Field("itemID") itemID: String,
        @Field("userID") userID: String,
        @Field("itemNum") itemNum: Int,
        @Field("itemTitle") itemTitle: String,
        @Field("itemContent") itemContent: String?,
        //@Field("itemX") itemX: String,
        //@Field("itemY") itemY: String,
        @Field("mode") mode: String,
    ): Call<PostModel>

    @FormUrlEncoded
    @POST(MyApp.item_load_url)
    fun item_load(
        @Field("userID") userID: String
    ): Call<List<ItemInfo>>

    companion object { // static 처럼 공유객체로 사용가능함. 모든 인스턴스가 공유하는 객체로서 동작함.
        //서버 IP만 입력해주세요~
        private const val BASE_URL = "http://192.168.0.9"
        fun create(): APIS_login {
            val gson: Gson = GsonBuilder().setLenient().create();
            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()
                .create(APIS_login::class.java)
        }
    }

}
