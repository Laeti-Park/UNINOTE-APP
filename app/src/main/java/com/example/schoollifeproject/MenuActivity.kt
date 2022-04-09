package com.example.schoollifeproject

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.schoollifeproject.databinding.ActivityMenuBinding
import androidx.annotation.NonNull
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class MenuActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMenuBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val api = APIS_login.create()
        var countKey: Int = 0
        val id = intent.getStringExtra("ID").toString()


        val btnMap = binding.btnMap
        val btnNotice = binding.btnNotice
        val tvNick = binding.tvNick

        tvNick.setText(intent.getStringExtra("name")+"님 환영합니다")

        api.notice_key_search(1 )
            .enqueue(object : Callback<PostModel> {
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


        btnNotice.setOnClickListener {
            val intent = Intent(this, ListActivity::class.java)
            intent.putExtra("ID", id)
            intent.putExtra("key", countKey)
            startActivity(intent)
        }

        btnMap.setOnClickListener {
            val intent = Intent(this, MindMapActivity::class.java)
            intent.putExtra("ID", id)
            startActivity(intent)
        }

    }

}