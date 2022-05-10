package com.example.schoollifeproject

import android.app.Activity
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.example.schoollifeproject.databinding.ActivityWriteNoticeBinding
import com.example.schoollifeproject.model.APIS
import com.example.schoollifeproject.model.PostModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat

/**
 * 글작성 클릭 실행 Activity
 * */

class WriteNoticeActivity : AppCompatActivity() {


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityWriteNoticeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val api = APIS.create()
        val current = System.currentTimeMillis()
        val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        val date: String = formatter.format(current)
        val addNotice = binding.addNotice
        val btnCancel = binding.btnCancel

        var type = intent.getIntExtra("type", 99999)
        var key = 0
        var userID = intent.getStringExtra("ID").toString()



        var editType = intent.getIntExtra("edit", 0) //수정인지 체크
        if (editType == 1) {
            addNotice.text = "수정"
            key = intent.getIntExtra("key", 99999)
            val thisTitle = intent.getStringExtra("thisTitle").toString()
            val thisContents = intent.getStringExtra("thisContents").toString()
            binding.editTitle.setText(thisTitle)
            binding.editNotice.setText(thisContents)
        }


        btnCancel.setOnClickListener {
            finish()
        }

        addNotice.setOnClickListener {
            val editTitle = binding.editTitle.text.toString()
            val editContents = binding.editNotice.text.toString()
            if(binding.editTitle.text.isBlank() || binding.editNotice.text.isBlank()) editType = 2
            if (editType == 1) {
                api.notice_update(
                    type,
                    key,
                    editTitle,
                    editContents
                ).enqueue(object : Callback<PostModel> {
                    override fun onResponse(call: Call<PostModel>, response: Response<PostModel>) {
                    }

                    override fun onFailure(call: Call<PostModel>, t: Throwable) {
                    }

                })
                Handler().postDelayed({
                    intent.apply {
                        putExtra("title", editTitle)
                        putExtra("contents", editContents)
                    }
                    setResult(Activity.RESULT_OK, intent)
                    finish()
                }, 1000)
            } else if(editType == 0){
                api.notice_save(
                    type,
                    editTitle,
                    userID,
                    date,
                    editContents
                ).enqueue(object : Callback<PostModel> {
                    override fun onResponse(call: Call<PostModel>, response: Response<PostModel>) {
                    }

                    override fun onFailure(p0: Call<PostModel>, t: Throwable) {
                    }

                })
                Handler().postDelayed({
                    setResult(Activity.RESULT_OK, intent)
                    finish()
                }, 1000)
            }else{
                Toast.makeText(this, "빈공간이 없도록 작성해주세요.", Toast.LENGTH_SHORT).show()
            }
        }
    }
}