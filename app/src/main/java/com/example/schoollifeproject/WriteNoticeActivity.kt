package com.example.schoollifeproject

import android.app.Activity
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.annotation.RequiresApi
import com.example.schoollifeproject.databinding.ActivityWriteNoticeBinding
import com.example.schoollifeproject.model.APIS
import com.example.schoollifeproject.model.PostModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.LocalDate

/**
 * 글작성 Fab 클릭 실행 Activity
 * */

class WriteNoticeActivity : AppCompatActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityWriteNoticeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val api = APIS.create()
        val date: String = LocalDate.now().toString()
        val addNotice = binding.addNotice
        addNotice.setOnClickListener {
            val editTitle = binding.editTitle.text.toString()
            val editContents = binding.editNotice.text.toString()
            val available = 0

            api.notice_save(
                editTitle,
                intent.getStringExtra("ID").toString(),
                date,
                editContents
            ).enqueue(object : Callback<PostModel> {
                override fun onResponse(call: Call<PostModel>, response: Response<PostModel>) {
                    Log.d("dbTestNoBody", response.toString())
                    Log.d(
                        "onResponse",
                        "저장성공: ${response.body()?.key}, ${intent.getStringExtra("ID")},$editTitle, $editContents, $date, $available"
                    )
                }

                override fun onFailure(p0: Call<PostModel>, t: Throwable) {
                    Log.d("onFailure", "저장실패 : " + t.message.toString())
                }

            })
            setResult(Activity.RESULT_OK, intent)
            finish()
        }

    }
}