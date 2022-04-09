package com.example.schoollifeproject

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.example.schoollifeproject.databinding.ActivityListBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ListActivity : AppCompatActivity() {

    //    샘플
    var contactsList:MutableList<Contacts> = mutableListOf()
    val api_notice = APIS_login.create()
    private lateinit var getResult: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityListBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val adapter = ContactsListAdapter(contactsList)
        binding.recyclerView.adapter = adapter


        val addNote = binding.addNote
        val id = intent.getStringExtra("ID").toString()
        var countKey = intent.getIntExtra("key", 0)
        //키 갯수 받아와서 반복문으로 contactsList에 값 넣기
        for (i in 1..countKey) {
            Log.d("for", countKey.toString())
            api_notice.notice_load(
                i
            ).enqueue(object : Callback<PostModel> {
                override fun onResponse(
                    call: Call<PostModel>,
                    response: Response<PostModel>
                ) {

                    val contacts =
                        Contacts(
                            response.body()?.noticeKey,
                            response.body()?.noticeTitle.toString(),
                            response.body()?.noticeName.toString(),
                            response.body()?.noticeDate.toString()
                        )
                    contactsList.add(contacts)
                    Log.d("onResponse", "성공!=" + i)

                    adapter.notifyDataSetChanged()
                }

                override fun onFailure(p0: Call<PostModel>, t: Throwable) {
                    Log.d("failedLoadNotice", t.message.toString())
                }
            })
        }
        getResult = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) {
            if (it.resultCode == RESULT_OK) {
                Log.d("리절트", "111")
                val title = it.data?.getStringExtra("Title")
                val notice = it.data?.getStringExtra("Notice")
                val date = it.data?.getStringExtra("Date")

                val contacts =
                    Contacts(
                        ++countKey,
                        title,
                        notice,
                        date
                    )
                contactsList.add(contacts)


            }
            adapter.notifyDataSetChanged()
        }

        addNote.setOnClickListener {

            val intent = Intent(this, WriteNoticeActivity::class.java)
            intent.putExtra("ID", id)
            getResult.launch(intent)
        }

    }


}