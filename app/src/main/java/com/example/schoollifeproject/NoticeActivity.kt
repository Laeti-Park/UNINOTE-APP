package com.example.schoollifeproject

import android.content.DialogInterface
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.Button
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.schoollifeproject.adapter.NoteReadListAdapter
import com.example.schoollifeproject.databinding.ActivityNoticeBinding
import com.example.schoollifeproject.model.APIS
import com.example.schoollifeproject.model.NoteReadContacts
import com.example.schoollifeproject.model.PostModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * 게시물 선택 실행 Activity
 * 작성자 : 이준영
 **/
class NoticeActivity : AppCompatActivity() {
    private var readList: MutableList<NoteReadContacts> = mutableListOf()
    private val readAdapter = NoteReadListAdapter(readList)


    private var type = 1
    private var key = 9999
    private lateinit var btnDelete: Button
    private lateinit var btnClose: Button
    private lateinit var btnUpdate: Button
    val api = APIS.create()
    private lateinit var getResult: ActivityResultLauncher<Intent>

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityNoticeBinding.inflate(layoutInflater)
        setContentView(binding.root)



        binding.noteReadRecyclerView.adapter = readAdapter

        btnDelete = binding.btnDelete
        btnClose = binding.btnClose
        btnUpdate = binding.btnUpdate

        val title = intent.getStringExtra("title").toString()
        val writer = intent.getStringExtra("writer").toString()
        val date = intent.getStringExtra("date").toString()
        val content = intent.getStringExtra("content").toString()
        val userID = intent.getStringExtra("userID").toString()
        type = intent.getIntExtra("type", 9999)
        key = intent.getIntExtra("key", 99999)
        Log.d("??","$type")

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
            dialog()
        }

        btnUpdate.setOnClickListener {
            val intent = Intent(this.applicationContext, WriteNoticeActivity::class.java).apply {
                putExtra("edit", 1)
                putExtra("ID", userID)
                putExtra("type", intent.getIntExtra("type", 99999))
                putExtra("key", key)
                putExtra("title", title)
                putExtra("content", content)
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
                            it.data?.getStringExtra("content").toString()
                        )
                        )
                readList.add(contact)
                readAdapter.notifyDataSetChanged()
            }
        }
    }
    fun dialog() {
        val dialog = AlertDialog.Builder(this)


        dialog.setTitle("삭제")
        dialog.setMessage("삭제하시겠습니까?")


        val dialog_litener = DialogInterface.OnClickListener { _, _ ->
            api.note_delete(type, key).enqueue(object : Callback<PostModel> {
                override fun onResponse(call: Call<PostModel>, response: Response<PostModel>) {
                    Log.d("성공: ", "notice_delete_success")
                }

                override fun onFailure(call: Call<PostModel>, t: Throwable) {
                    Log.d("페일(노트): ", "notice_delete_fail ${t.message}")
                }
            })
            Handler(Looper.getMainLooper()).postDelayed({
                finish()
            }, 2000)
        }
        val dialog_litenerC = DialogInterface.OnClickListener { _, _ ->

        }
        dialog.setPositiveButton("확인", dialog_litener)
        dialog.setNegativeButton("취소", dialog_litenerC)
        dialog.show()
    }
}