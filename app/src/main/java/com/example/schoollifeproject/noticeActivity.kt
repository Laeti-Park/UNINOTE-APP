package com.example.schoollifeproject

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.annotation.RequiresApi
import com.example.schoollifeproject.adapter.NoteCommentListAdapter
import com.example.schoollifeproject.adapter.NoteReadListAdapter
import com.example.schoollifeproject.databinding.ActivityNoticeBinding
import com.example.schoollifeproject.model.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

class noticeActivity : AppCompatActivity() {
    private var readContactsList: MutableList<NoteReadContacts> = mutableListOf()
    //private var commentContactsList: MutableList<NoteCommentContacts> = mutableListOf()
    private val readAdapter = NoteReadListAdapter(readContactsList)
    //private val commentAdapter = NoteCommentListAdapter(commentContactsList)

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityNoticeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        /*val current = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("HH시 mm분")
        val formatted = current.format(formatter)
*/
        val api = APIS_login.create()
        binding.noteReadRecyclerView.adapter = readAdapter
        //binding.commentRecyclerView.adapter = commentAdapter

        val key = intent.getIntExtra("key", 0)
        val title = intent.getStringExtra("title")
        val writer = intent.getStringExtra("writer")
        //val views = 100

        //해당 글의 키를 가져와 표시
        api.notice_open(
            key
        ).enqueue(object : Callback<PostModel> {
            override fun onResponse(call: Call<PostModel>, response: Response<PostModel>) {
                //작성되어있던 댓글 불러오기
                //작성자 표시
                //작성일 표시
                val contacts =
                    NoteReadContacts(
                        title,
                        response.body()?.noticeName,
                        response.body()?.noticeContents
                    )
                readContactsList.add(contacts)
                readAdapter.notifyDataSetChanged()
            }

            override fun onFailure(call: Call<PostModel>, t: Throwable) {
                Log.d("onFailure", t.message.toString())
            }
        })


        //버튼 기능으로 댓글 작성 및 저장 최신화
        /*val btn_commitComment = binding.commentBtn
        var commentBlank = false

        btn_commitComment.setOnClickListener {
            val comment = binding.commentEdit.text.toString()
            Log.d("댓글", "눌림 : $comment")

            if (comment.isBlank()) {
                commentBlank = true
            }

            if (!commentBlank) {
                val contacts =
                    NoteCommentContacts(
                        writer,
                        formatted,
                        comment
                    )
                commentContactsList.add(contacts)
                commentAdapter.notifyDataSetChanged()
            }
        }*/

    }
}