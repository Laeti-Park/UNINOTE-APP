package com.example.schoollifeproject

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.schoollifeproject.databinding.ActivityListBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ListActivity : AppCompatActivity() {

    //    샘플
    var contactsList: List<Contacts> = listOf(
        Contacts("top", "khan", "20220104"),
        Contacts("jg", "canyon", "20220104"),
        Contacts("mid", "faker", "20220104"),
        Contacts("ad", "viper", "20220104"),
        Contacts("sup", "keria", "20220104")
    )
    val api_notice = APIS_login.create()
    var countKey: Int = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val adapter = ContactsListAdapter(contactsList)
        binding.recyclerView.adapter = adapter

        //키 갯수 받아와서 반복문으로 contactsList에 값 넣기

        api_notice.notice_key_search(
            1
        ).enqueue(object : Callback<PostModel> {
            override fun onResponse(
                call: Call<PostModel>,
                response: Response<PostModel>
            ) {

                if(!response.body().toString().isEmpty()) {
                    countKey = response.body()?.countKey!!
                }
                Log.d("키갯수", countKey.toString())
            }
            override fun onFailure(p0: Call<PostModel>, t: Throwable) {
                Log.d("failedSearchKey", t.message.toString())
            }
        })

        for(i in 1..2){
            Log.d("와일", countKey.toString())
            api_notice.notice_load(
                countKey
            ).enqueue(object : Callback<PostModel> {
                override fun onResponse(
                    call: Call<PostModel>,
                    response: Response<PostModel>
                ) {
                    Log.d("onResponse", "여기에 리스트오브")
                    contactsList = listOf(
                        Contacts(
                            response.body()?.noticeTitle.toString(),
                            response.body()?.noticeName.toString(),
                            response.body()?.noticeDate.toString()
                        )
                    )

                }
                override fun onFailure(p0: Call<PostModel>, t: Throwable) {
                    Log.d("failedLoadNotice", t.message.toString())
                }
            })
        }


    }
}