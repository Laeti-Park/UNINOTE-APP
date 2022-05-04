package com.example.schoollifeproject

import android.content.DialogInterface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AlertDialog
import com.example.schoollifeproject.adapter.AnnoListAdapter
import com.example.schoollifeproject.adapter.SugListAdapter
import com.example.schoollifeproject.databinding.ActivityMenuBinding
import com.example.schoollifeproject.fragment.ListFragment
import com.example.schoollifeproject.fragment.MindMapFragment
import com.example.schoollifeproject.model.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class MenuActivity : AppCompatActivity() {
    private val annoContactslist: List<AnnoContacts> = listOf(
        AnnoContacts("공지사항1"),
        AnnoContacts("공지사항2"),
        AnnoContacts("공지사항3")
    )
    private val sugContactslist: List<SugContacts> = listOf(
        SugContacts("1번글", 3),
        SugContacts("2번글", 2),
        SugContacts("3번글", 1)
    )

    /*
        private var annoContactsList: MutableList<AnnoContacts> = mutableListOf()
        private var contactsList: MutableList<Contacts> = mutableListOf()
        private var contactsList: MutableList<Contacts> = mutableListOf()
        private var contactsList: MutableList<Contacts> = mutableListOf()
    */
    private val annoAdapter = AnnoListAdapter(annoContactslist)
    private val sugAdapter = SugListAdapter(sugContactslist)

    private var countKey: Int = 0
    private lateinit var userID: String

    private var loginCK: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMenuBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val api = APIS_login.create()

        binding.annoRecycler.adapter = annoAdapter
        binding.sugRecycvler.adapter = sugAdapter

        userID = intent.getStringExtra("ID").toString()
        loginCK = intent.getIntExtra("loginCheck", 0)
        api.notice_key_search(1)
            .enqueue(object : Callback<PostModel> {
                override fun onResponse(
                    call: Call<PostModel>,
                    response: Response<PostModel>
                ) {

                    if (!response.body().toString().isEmpty()) {
                        countKey = response.body()?.countKey!!
                    }
                    Log.d("키갯수", countKey.toString())
                }

                override fun onFailure(p0: Call<PostModel>, t: Throwable) {
                    Log.d("failedSearchKey", t.message.toString())
                }
            })

        binding.bottomNavigationView.run {

            val listFragment = ListFragment()
            val mindMapFragment = MindMapFragment()
            var bundle = Bundle()
            bundle.putString("ID", userID)

            setOnItemSelectedListener { item ->
                val transaction = supportFragmentManager.beginTransaction()
                val frameLayout = supportFragmentManager.findFragmentById(R.id.frameLayout)
                when (item.itemId) {
                    R.id.mainMenu1 -> {
                        if (frameLayout != null) {
                            removeFragment()
                        }
                        true
                    }
                    R.id.mainMenu2 -> {
                        if (loginCK != 1) {
                            mindMapFragment.arguments = bundle
                            transaction.replace(R.id.frameLayout, mindMapFragment)
                                .commitAllowingStateLoss()
                        }
                        else{
                            failDialog()
                        }
                        true
                    }
                    R.id.mainMenu3 -> {
                        bundle.putInt("countKey", countKey)
                        listFragment.arguments = bundle
                        transaction.replace(R.id.frameLayout, listFragment)
                            .commitAllowingStateLoss()
                        true
                    }
                    else -> {
                        true
                    }
                }
            }
        }
    }

    private fun removeFragment() {
        val transaction = supportFragmentManager.beginTransaction()
        val frameLayout = supportFragmentManager.findFragmentById(R.id.frameLayout)
        transaction.remove(frameLayout!!)
        transaction.commit()
    }

    fun failDialog(){
        var dialog = AlertDialog.Builder(this)

        dialog.setTitle("오류")
        dialog.setMessage("비회원은 이용할 수 없는 기능입니다.")

        val dialog_listener = object: DialogInterface.OnClickListener{
            override fun onClick(dialog: DialogInterface?, which: Int) {
            }
        }

        dialog.setPositiveButton("확인", dialog_listener)
        dialog.show()
    }
}
