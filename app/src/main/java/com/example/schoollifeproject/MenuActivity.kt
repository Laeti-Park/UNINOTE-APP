package com.example.schoollifeproject

import android.content.DialogInterface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
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
    private val annoContactslist: MutableList<AnnoContacts> = mutableListOf()
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

    private lateinit var userID: String
    private lateinit var userName: String
    private var loginCK: Int = 0

    private lateinit var binding: ActivityMenuBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMenuBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val api = APIS.create()
        binding.annoRecycler.adapter = annoAdapter
        binding.sugRecycler.adapter = sugAdapter

        userID = intent.getStringExtra("ID").toString()
        userName = intent.getStringExtra("name").toString()
        loginCK = intent.getIntExtra("loginCheck", 0)
        Log.d("안녕1", "ㅇ")
        api.notice_load(1)
            .enqueue(object : Callback<List<Notice>> {
                override fun onResponse(
                    call: Call<List<Notice>>,
                    response: Response<List<Notice>>
                ) {
                    Log.d("안녕2", "ㅇ")
                    for (i in response.body()!!) {
                        val contacts = (
                                AnnoContacts(
                                    i.getNoticeKey(),
                                    i.getNoticeTitle(),
                                    i.getNoticeWriter(),
                                    i.getNoticeDate(),
                                    i.getNoticeContent(),
                                    i.getNoticeAvailable()
                                )
                                )
                        annoContactslist.add(contacts)
                        annoAdapter.notifyDataSetChanged()
                    }
                }

                override fun onFailure(call: Call<List<Notice>>, t: Throwable) {
                    Log.d("안녕3", t.message.toString())
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
                        menuMainVisible(true)
                        true
                    }
                    R.id.mainMenu2 -> {
                        mindMapFragment.arguments = bundle
                        transaction.replace(R.id.frameLayout, mindMapFragment)
                            .commitAllowingStateLoss()
                        menuMainVisible(false)
                        true
                    }
                    R.id.mainMenu3 -> {
                        listFragment.arguments = bundle
                        transaction.replace(R.id.frameLayout, listFragment)
                            .commitAllowingStateLoss()
                        menuMainVisible(false)
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

    fun failDialog() {
        var dialog = AlertDialog.Builder(this)

        dialog.setTitle("오류")
        dialog.setMessage("비회원은 이용할 수 없는 기능입니다.")

        val dialog_listener = object : DialogInterface.OnClickListener {
            override fun onClick(dialog: DialogInterface?, which: Int) {
            }
        }

        dialog.setPositiveButton("확인", dialog_listener)
        dialog.show()
    }

    fun menuMainVisible(b: Boolean) {
        if (b) {
            binding.annoLayout.visibility = View.VISIBLE
            binding.freeLayout.visibility = View.VISIBLE
            binding.sugLayout.visibility = View.VISIBLE
            binding.infoLayout.visibility = View.VISIBLE
        } else {
            binding.annoLayout.visibility = View.GONE
            binding.freeLayout.visibility = View.GONE
            binding.sugLayout.visibility = View.GONE
            binding.infoLayout.visibility = View.GONE
        }
    }
}
