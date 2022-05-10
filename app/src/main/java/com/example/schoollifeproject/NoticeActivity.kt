package com.example.schoollifeproject

import android.app.Activity
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.view.isGone
import com.example.schoollifeproject.adapter.NoteReadListAdapter
import com.example.schoollifeproject.databinding.ActivityNoticeBinding
import com.example.schoollifeproject.model.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * 게시물 선택 실행 Activity
 * */

class NoticeActivity : AppCompatActivity() {
    private var readList: MutableList<NoteReadContacts> = mutableListOf()
    private val readAdapter = NoteReadListAdapter(readList)

    private lateinit var btnDelete: Button
    private lateinit var btnClose: Button
    private lateinit var btnUpdate: Button

    private lateinit var getResult: ActivityResultLauncher<Intent>

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityNoticeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val api = APIS.create()

        binding.noteReadRecyclerView.adapter = readAdapter

        btnDelete = binding.btnDelete
        btnClose = binding.btnClose
        btnUpdate = binding.btnUpdate

        val title = intent.getStringExtra("title").toString()
        val writer = intent.getStringExtra("writer").toString()
        val date = intent.getStringExtra("date").toString()
        val content = intent.getStringExtra("content").toString()
        val userID = intent.getStringExtra("userID").toString()
        val type = intent.getIntExtra("type", 1)
        val key = intent.getIntExtra("key", 99999)


        /**
         * 로그인아이디와 글쓴이가 같지 않을경우 글삭제 비활성화
         */
        if (writer != userID) {
            btnDelete.visibility = View.INVISIBLE
            btnUpdate.visibility = View.INVISIBLE
        }

        /**
         * 받은 값을 포스팅
         */
        val contact = (
                NoteReadContacts(
                    title,
                    writer,
                    date,
                    content
                )
                )
        readList.add(contact)
        readAdapter.notifyDataSetChanged()

        btnClose.setOnClickListener {
            finish()
        }

        /**
         * 글삭제 기능
         */
        btnDelete.setOnClickListener {
            api.note_delete(type ,key).enqueue(object : Callback<PostModel> {
                override fun onResponse(call: Call<PostModel>, response: Response<PostModel>) {
                    Log.d("성공: ", "글삭제")
                }

                override fun onFailure(call: Call<PostModel>, t: Throwable) {
                    Log.d("페일(노트): ", "${t.message}")
                }
            })
            Handler().postDelayed({
                finish()
            }, 2000)
        }

        btnUpdate.setOnClickListener {
            val intent = Intent(this.applicationContext, WriteNoticeActivity::class.java).apply {
                putExtra("edit", 1)
                putExtra("ID", userID)
                putExtra("type", type)
                putExtra("key", key)
                putExtra("thisTitle", title)
                putExtra("thisContent", content)
            }
            getResult.launch(intent)

        }

        getResult = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) {
            if (it.resultCode == RESULT_OK) {
                readList.clear()
                val contact = (
                        NoteReadContacts(
                            it.data?.getStringExtra("title").toString(),
                            writer,
                            date,
                            it.data?.getStringExtra("contents").toString()
                        )
                        )
                readList.add(contact)
                readAdapter.notifyDataSetChanged()
            }
        }


        //val views = 100
        //해당 글의 키를 가져와 표시


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