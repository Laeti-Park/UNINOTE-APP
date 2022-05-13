package com.example.schoollifeproject

import android.content.DialogInterface
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.schoollifeproject.adapter.AnnoListAdapter
import com.example.schoollifeproject.adapter.FreeListAdapter
import com.example.schoollifeproject.adapter.InfoListAdapter
import com.example.schoollifeproject.adapter.MapListAdapter
import com.example.schoollifeproject.databinding.ActivityMenuBinding
import com.example.schoollifeproject.fragment.*
import com.example.schoollifeproject.model.*
import com.example.schoollifeproject.shared.Shared
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.system.exitProcess

/**
 * MenuActivity
 * 작성자 : 이준영, 박동훈
 */
class MenuActivity : AppCompatActivity() {
    private val annoContactsList: MutableList<NoticeListModel> = mutableListOf()
    private val mapContactsList: MutableList<MapListModel> = mutableListOf()
    private val freeContactslist: MutableList<FreeListModel> = mutableListOf()
    private val infoContactsList: MutableList<InfoListModel> = mutableListOf()

    private val annoAdapter = AnnoListAdapter(annoContactsList)
    private val mapAdapter = MapListAdapter(mapContactsList)
    private val freeAdapter = FreeListAdapter(freeContactslist)
    private val infoAdapter = InfoListAdapter(infoContactsList)

    private var backWait: Long = 0
    private var loginCK: Int = 0
    private lateinit var userID: String
    private lateinit var userPW: String
    private lateinit var userName: String

    private lateinit var annoText: TextView
    private lateinit var sugText: TextView
    private lateinit var freeText: TextView
    private lateinit var infoText: TextView

    private lateinit var binding: ActivityMenuBinding
    private val api = APIS.create()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMenuBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val dividerItemDecoration = DividerItemDecoration(applicationContext, RecyclerView.VERTICAL)
        val freeManager = LinearLayoutManager(applicationContext)
        freeManager.reverseLayout = true
        freeManager.stackFromEnd = true
        val annoManager = LinearLayoutManager(applicationContext)
        annoManager.reverseLayout = true
        annoManager.stackFromEnd = true
        val infoManager = LinearLayoutManager(applicationContext)
        infoManager.reverseLayout = true
        infoManager.stackFromEnd = true

        binding.annoRecycler.layoutManager = annoManager
        binding.infoRecycler.layoutManager = infoManager
        binding.freeRecycler.layoutManager = freeManager

        binding.annoRecycler.addItemDecoration(dividerItemDecoration)
        binding.sugRecycler.addItemDecoration(dividerItemDecoration)
        binding.freeRecycler.addItemDecoration(dividerItemDecoration)
        binding.infoRecycler.addItemDecoration(dividerItemDecoration)



        binding.annoRecycler.adapter = annoAdapter
        binding.sugRecycler.adapter = mapAdapter
        binding.freeRecycler.adapter = freeAdapter
        binding.infoRecycler.adapter = infoAdapter

        userID = intent.getStringExtra("ID").toString()
        userPW = intent.getStringExtra("PW").toString()
        userName = intent.getStringExtra("name").toString()
        loginCK = intent.getIntExtra("loginCheck", 0)

        annoText = binding.annoPost
        sugText = binding.sugPost
        freeText = binding.freePost
        infoText = binding.infoPost

        /**
         * 로그인 백업
         */
        if (userID != "비회원") {
            Shared.prefs.setString("id", userID)
            Shared.prefs.setString("pw", userPW)
        } else {
            Shared.prefs.setString("id", "nothing")
            Shared.prefs.setString("pw", "nothing")
        }

        /**
         * 메인메뉴 게시판 제목 클릭 이벤트
         */
        annoText.setOnClickListener {
            val transaction = supportFragmentManager.beginTransaction()
            val annoListFragment = AnnoListFragment()
            transaction.replace(R.id.frameLayout, annoListFragment.newInstance(userID))
                .commitAllowingStateLoss()
            menuMainVisible(false)
        }
        sugText.setOnClickListener {
            val transaction = supportFragmentManager.beginTransaction()
            val mapListFragment = MapListFragment()
            transaction.replace(R.id.frameLayout, mapListFragment.newInstance(userID))
                .commitAllowingStateLoss()
            menuMainVisible(false)
        }
        freeText.setOnClickListener {
            val transaction = supportFragmentManager.beginTransaction()
            val freeListFragment = FreeListFragment()
            transaction.replace(R.id.frameLayout, freeListFragment.newInstance(userID))
                .commitAllowingStateLoss()
            menuMainVisible(false)
        }
        infoText.setOnClickListener {
            val transaction = supportFragmentManager.beginTransaction()
            val infoListFragment = InfoListFragment()
            transaction.replace(R.id.frameLayout, infoListFragment.newInstance(userID))
                .commitAllowingStateLoss()
            menuMainVisible(false)
        }
        mapAdapter.setOnMapItemListener { _, mapID ->
            val transaction = supportFragmentManager.beginTransaction()
            val mindMapFragment = MindMapFragment()
            transaction?.replace(R.id.frameLayout, mindMapFragment.newInstance(userID, mapID))
                ?.commitAllowingStateLoss()
            menuMainVisible(false)
        }

        /**
         * 메인메뉴 게시판 DB 불러오기
         * notice = 공지사항
         * bbs = 자유게시판
         * info = 스터디
         * maplist = 추천로드맵
         * */
        //type 0 = 일반 포스팅, type 1 = 공지 포스팅
        api.notice_load(1).enqueue(
            object : Callback<List<NoticeListModel>> {
                override fun onResponse(
                    call: Call<List<NoticeListModel>>,
                    response: Response<List<NoticeListModel>>
                ) {
                    //공지사항의 개수만큼 호출, 연결
                    for (i in response.body()!!) {
                        val contacts = (
                                NoticeListModel(
                                    i.getNoticeKey(),
                                    i.getNoticeTitle(),
                                    i.getNoticeWriter(),
                                    i.getNoticeDate(),
                                    i.getNoticeContent(),
                                    i.getNoticeAvailable()
                                )
                                )
                        annoContactsList.add(contacts)
                        annoAdapter.notifyDataSetChanged()
                    }
                }

                override fun onFailure(call: Call<List<NoticeListModel>>, t: Throwable) {}

            })

        api.bbs_load(1).enqueue(object : Callback<List<FreeListModel>> {
            override fun onResponse(
                call: Call<List<FreeListModel>>,
                response: Response<List<FreeListModel>>
            ) {
                for (i in response.body()!!) {
                    if (i.getBbsAvailable() == 1) {
                        val contacts = (
                                FreeListModel(
                                    i.getBbsKey(),
                                    i.getBbsTitle(),
                                    i.getBbsWriter(),
                                    i.getBbsDate(),
                                    i.getBbsContent(),
                                    i.getBbsAvailable()
                                )
                                )
                        freeContactslist.add(contacts)
                        freeAdapter.notifyDataSetChanged()
                    }
                }
            }

            override fun onFailure(call: Call<List<FreeListModel>>, t: Throwable) {

            }

        })

        api.info_load(1).enqueue(object : Callback<List<InfoListModel>> {
            override fun onResponse(
                call: Call<List<InfoListModel>>,
                response: Response<List<InfoListModel>>
            ) {
                for (i in response.body()!!) {
                    val contacts = (
                            InfoListModel(
                                i.getStudyKey(),
                                i.getStudyTitle(),
                                i.getStudyWriter(),
                                i.getStudyDate(),
                                i.getStudyContent(),
                                i.getStudyAvailable()
                            )
                            )
                    infoContactsList.add(contacts)
                    infoAdapter.notifyDataSetChanged()
                }
            }

            override fun onFailure(call: Call<List<InfoListModel>>, t: Throwable) {

            }

        })

        api.map_list(1).enqueue(
            object : Callback<List<MapListModel>> {
                override fun onResponse(
                    call: Call<List<MapListModel>>,
                    response: Response<List<MapListModel>>
                ) {
                    for (i in response.body()!!) {
                        val contacts = (
                                MapListModel(
                                    i.getMapID(),
                                    i.getMapHits(),
                                    i.getMapRecommend()
                                )
                                )
                        mapContactsList.add(contacts)
                        mapAdapter.notifyDataSetChanged()
                    }
                }

                override fun onFailure(call: Call<List<MapListModel>>, t: Throwable) {
                }

            })

        /**
         * 프래그먼트 하단바
         * 메뉴1 - 메인화면
         * 메뉴2 - 로드맵제작(비회원 이용불가)
         * 메뉴3 - 게시판
         * 메뉴4 - 로드맵게시판
         * */
        binding.bottomNavigationView.run {
            val mindMapFragment = MindMapFragment()
            val freeListFragment = FreeListFragment()
            val settingsFragment = SettingsFragment()

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
                        if (userID == "비회원") failDialog()
                        else {
                            transaction.replace(
                                R.id.frameLayout,
                                mindMapFragment.newInstance(userID, userID)
                            )
                                .commitAllowingStateLoss()
                            menuMainVisible(false)
                        }
                        true
                    }
                    R.id.mainMenu3 -> {
                        transaction.replace(R.id.frameLayout, freeListFragment.newInstance(userID))
                            .commitAllowingStateLoss()
                        menuMainVisible(false)
                        true
                    }
                    else -> {
                        transaction.replace(R.id.frameLayout, settingsFragment.newInstance(userID))
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
            binding.logo.visibility = View.VISIBLE
        } else {
            binding.annoLayout.visibility = View.GONE
            binding.freeLayout.visibility = View.GONE
            binding.sugLayout.visibility = View.GONE
            binding.infoLayout.visibility = View.GONE
            binding.infoLayout.visibility = View.GONE
            binding.logo.visibility = View.GONE
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        /**
         * 정상적인 종료시 로그인 정보 삭제
         */
        if (userID != "비회원") {
            val api = APIS.create()
            Shared.prefs.setString("id", "nothing")
            Shared.prefs.setString("pw", "nothing")

            api.logout(userID).enqueue(object : Callback<PostModel> {
                override fun onResponse(call: Call<PostModel>, response: Response<PostModel>) {
                }

                override fun onFailure(call: Call<PostModel>, t: Throwable) {
                }
            })
        }
    }

    override fun onBackPressed() {
        if (System.currentTimeMillis() - backWait < 1000) {
            finishAffinity()
            exitProcess(0)
        }
        backWait = System.currentTimeMillis()
        Toast.makeText(this, "한번 더 입력시 종료됩니다.", Toast.LENGTH_SHORT).show()
    }
}