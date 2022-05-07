package com.example.schoollifeproject

import android.content.DialogInterface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import com.example.schoollifeproject.adapter.AnnoListAdapter
import com.example.schoollifeproject.adapter.SugListAdapter
import com.example.schoollifeproject.databinding.ActivityMenuBinding
import com.example.schoollifeproject.fragment.ListFragment
import com.example.schoollifeproject.fragment.MindMapFragment
import com.example.schoollifeproject.fragment.MapListFragment
import com.example.schoollifeproject.model.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * 로그인 후 메뉴 Activity
 * */

class MenuActivity : AppCompatActivity() {
    private val annoContactslist: MutableList<AnnoContacts> = mutableListOf()
    private val sugContactslist: List<SugContacts> = listOf(
        SugContacts("1번글", 3),
        SugContacts("2번글", 2),
        SugContacts("3번글", 1)
    )

    /*
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
        /**
         * 메인메뉴의 공지사항 DB 불러오기
         * */
        api.notice_load(
            1       //type 0 = 일반 포스팅, type 1 = 공지 포스팅
        ).enqueue(object : Callback<List<Notice>> {
            override fun onResponse(
                call: Call<List<Notice>>,
                response: Response<List<Notice>>
            ) {
                //공지사항의 개수만큼 호출, 연결
                for (i in response.body()!!) {
                    val contacts = (
                            AnnoContacts(
                                i.getNoticeKey(),
                                i.getNoticeTitle(),
                                i.getNoticeWriter(),
                                i.getNoticeDate(),
                                i.getNoticeContent()
                            )
                            )
                    annoContactslist.add(contacts)
                    annoAdapter.notifyDataSetChanged()
                }
            }

            override fun onFailure(call: Call<List<Notice>>, t: Throwable) {}

        })
        /**
         * 프래그먼트 하단바
         * 메뉴1 - 메인화면
         * 메뉴2 - 로드맵제작(비회원 이용불가)
         * 메뉴3 - 게시판
         * 메뉴4 - 로드맵게시판
         * */
        binding.bottomNavigationView.run {
            val listFragment = ListFragment()
            val mindMapFragment = MindMapFragment()
            val settingFragment = MapListFragment()
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
                        if (userID == "비회원")
                            failDialog()
                        else {
                            bundle.putString("mapID", userID)
                            mindMapFragment.arguments = bundle
                            transaction.replace(R.id.frameLayout, mindMapFragment)
                                .commitAllowingStateLoss()
                            menuMainVisible(false)
                        }
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
                        listFragment.arguments = bundle
                        transaction.replace(R.id.frameLayout, settingFragment)
                            .commitAllowingStateLoss()
                        menuMainVisible(false)
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

    private fun failDialog() {
        var dialog = AlertDialog.Builder(this)
        dialog.setTitle("오류")
        dialog.setMessage("비회원은 이용할 수 없는 기능입니다.")
        val dialog_listener = DialogInterface.OnClickListener { dialog, which -> }
        dialog.setPositiveButton("확인", dialog_listener)
        dialog.show()
    }

    private fun menuMainVisible(b: Boolean) {
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
