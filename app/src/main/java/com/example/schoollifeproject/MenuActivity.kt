package com.example.schoollifeproject

import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import com.example.schoollifeproject.adapter.AnnoListAdapter
import com.example.schoollifeproject.adapter.FreeListAdapter
import com.example.schoollifeproject.adapter.InfoListAdapter
import com.example.schoollifeproject.adapter.MapListAdapter
import com.example.schoollifeproject.databinding.ActivityMenuBinding
import com.example.schoollifeproject.fragment.*
import com.example.schoollifeproject.model.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * 로그인 후 메뉴 Activity
 * 작성자 : 이준영, 박동훈
 */

// TODO : menu-자유게시판, contacts_main_board.xml, Fragment-공지사항/공부게시판, 디자인
class MenuActivity : AppCompatActivity() {
    private val TAG = this.javaClass.toString()
    private val annoContactsList: MutableList<NoticeListModel> = mutableListOf()
    private val mapContactsList: MutableList<MapListModel> = mutableListOf()
    private val freeContactslist: MutableList<FreeListModel> = mutableListOf()
    private val infoContactsList: MutableList<InfoListModel> = mutableListOf()

    private val annoAdapter = AnnoListAdapter(annoContactsList)
    private val mapAdapter = MapListAdapter(mapContactsList)
    private val freeAdapter = FreeListAdapter(freeContactslist)
    private val infoAdapter = InfoListAdapter(infoContactsList)

    private lateinit var userID: String
    private lateinit var userName: String
    private var loginCK: Int = 0

    private lateinit var binding: ActivityMenuBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMenuBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val api = APIS.create()

        val dividerItemDecoration = DividerItemDecoration(applicationContext, RecyclerView.VERTICAL)
        binding.annoRecycler.addItemDecoration(dividerItemDecoration)
        binding.sugRecycler.addItemDecoration(dividerItemDecoration)
        binding.freeRecycler.addItemDecoration(dividerItemDecoration)
        binding.infoRecycler.addItemDecoration(dividerItemDecoration)

        binding.annoRecycler.adapter = annoAdapter
        binding.sugRecycler.adapter = mapAdapter
        binding.freeRecycler.adapter = freeAdapter
        binding.infoRecycler.adapter = infoAdapter

        userID = intent.getStringExtra("ID").toString()
        userName = intent.getStringExtra("name").toString()
        loginCK = intent.getIntExtra("loginCheck", 0)

        val annoText = binding.annoPost
        val sugText = binding.sugPost
        val freeText = binding.freePost
        val infoText = binding.infoPost

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
        mapAdapter.setOnMapItemListener { view, mapID ->
            val mindMapFragment = MindMapFragment()
            val transaction = supportFragmentManager.beginTransaction()
            Log.d("$TAG", "userIDSend: ${userID}, $mapID")

            menuMainVisible(false)

            transaction?.replace(R.id.frameLayout, mindMapFragment.newInstance(userID, mapID))
                ?.commitAllowingStateLoss()
        }

        /**
         * 메인메뉴의 공지사항 DB 불러오기
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
            val mapListFragment = MapListFragment()
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
                        if (userID == "비회원")
                            failDialog()
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
}