package com.example.schoollifeproject

import android.app.Activity
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.annotation.RequiresApi
import com.example.schoollifeproject.databinding.ActivityMainBinding
import com.example.schoollifeproject.databinding.ActivityWriteNoticeBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.LocalDate

class WriteNoticeActivity : AppCompatActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityWriteNoticeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val api = APIS_login.create()
        val date: String = LocalDate.now().toString()


        val editTitle = binding.editTitle
        val editNotice = binding.editNotice
        val addNotice = binding.addNotice

        val intent = Intent(this, ListActivity::class.java)
        addNotice.setOnClickListener {
            api.notice_save(
                editTitle.text.toString(),
                intent.getStringExtra("ID").toString(),
                date,
                editNotice.toString()
            ).enqueue(object : Callback<PostModel> {
                override fun onResponse(p0: Call<PostModel>, p1: Response<PostModel>) {
                    Log.d("onResponse","저장성공")
                    intent.apply {
                        putExtra("Title", editTitle.toString())
                    }
                    intent.apply {
                        putExtra("Notice", editNotice.toString())
                    }
                    intent.apply {
                        putExtra("Date", date)
                    }
                }

                override fun onFailure(p0: Call<PostModel>, t: Throwable) {
                    Log.d("onFailure","저장실패 : " + t.message.toString())
                }

            })
            setResult(Activity.RESULT_OK, intent)
            finish()
        }

    }
}