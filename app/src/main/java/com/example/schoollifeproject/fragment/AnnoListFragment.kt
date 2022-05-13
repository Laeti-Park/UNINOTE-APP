package com.example.schoollifeproject.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.schoollifeproject.R
import com.example.schoollifeproject.WriteNoticeActivity
import com.example.schoollifeproject.adapter.AnnoFragmentAdapter
import com.example.schoollifeproject.databinding.FragmentAnnoListBinding
import com.example.schoollifeproject.model.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * 공부게시판 Fragment
 * 작성자 : 박동훈
 */
class AnnoListFragment : Fragment() {
    private val TAG = this.javaClass.toString()
    private var annoList: MutableList<NoteListContacts> = mutableListOf()
    private val adapter = AnnoFragmentAdapter(annoList)

    private lateinit var getResult0: ActivityResultLauncher<Intent>
    private lateinit var binding: FragmentAnnoListBinding

    private val api = APIS.create()
    private lateinit var userID: String
    private var countKey: Int = 0

    fun newInstance(userID: String): AnnoListFragment {
        val args = Bundle()
        args.putString("userID", userID)

        val annoFragment = AnnoListFragment()
        annoFragment.arguments = args

        return annoFragment
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAnnoListBinding.inflate(inflater, container, false)

        val dividerItemDecoration = DividerItemDecoration(context, RecyclerView.VERTICAL)

        val manager = LinearLayoutManager(context)
        manager.reverseLayout = true
        manager.stackFromEnd = true

        binding.recyclerView.layoutManager = manager
        binding.recyclerView.addItemDecoration(dividerItemDecoration)
        binding.recyclerView.adapter = adapter
        binding.recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {})

        userID = arguments?.getString("userID").toString()

        posting()

        val addNote = binding.addNote

        if (userID == "Admin") {
            addNote.visibility = View.VISIBLE
        } else {
            addNote.visibility = View.GONE
        }
        //글작성 버튼 클릭
        addNote.setOnClickListener {
            if (userID == "비회원") {
            } else {
                val intent = Intent(context, WriteNoticeActivity::class.java)
                intent.apply {
                    putExtra("type", 0)
                    putExtra("ID", userID)
                }
                getResult0.launch(intent)
            }
        }
        getResult0 = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) {
            if (it.resultCode == AppCompatActivity.RESULT_OK) {
                posting()
            }
            adapter.notifyDataSetChanged()
        }
        //게시글 목록 호출


        binding.annoView.setOnClickListener {
            val annoListFragment = AnnoListFragment()
            val transaction = activity?.supportFragmentManager?.beginTransaction()
            Log.d("$TAG", "userIDSend: ${userID}")

            transaction?.replace(R.id.frameLayout, annoListFragment.newInstance(userID))
                ?.commitAllowingStateLoss()
        }

        binding.freeView.setOnClickListener {
            val freeListFragment = FreeListFragment()
            val transaction = activity?.supportFragmentManager?.beginTransaction()
            Log.d("$TAG", "userIDSend: ${userID}")

            transaction?.replace(R.id.frameLayout, freeListFragment.newInstance(userID))
                ?.commitAllowingStateLoss()
        }

        binding.sugView.setOnClickListener {
            val mapListFragment = MapListFragment()
            val transaction = activity?.supportFragmentManager?.beginTransaction()
            Log.d("$TAG", "userIDSend: ${userID}")

            transaction?.replace(R.id.frameLayout, mapListFragment.newInstance(userID))
                ?.commitAllowingStateLoss()
        }

        binding.infoView.setOnClickListener {
            val infoListFragment = InfoListFragment()
            val transaction = activity?.supportFragmentManager?.beginTransaction()
            Log.d("$TAG", "userIDSend: $userID")

            transaction?.replace(R.id.frameLayout, infoListFragment.newInstance(userID))
                ?.commitAllowingStateLoss()
        }

        return binding.root
    }

    /**
     * RecyclerView에 포스팅할 아이템들 DB에서 호출
     * */
    private fun posting() {
        api.notice_load(
            1       //type 0 = 일반 포스팅, type 1 = 공지 포스팅
        ).enqueue(object : Callback<List<NoticeListModel>> {
            override fun onResponse(
                call: Call<List<NoticeListModel>>,
                response: Response<List<NoticeListModel>>
            ) {
                val list = mutableListOf<NoteListContacts>()
                //공지사항의 개수만큼 호출, 연결
                for (i in response.body()!!) {
                    val contacts = (
                            NoteListContacts(
                                userID,
                                i.getNoticeKey(),
                                i.getNoticeTitle(),
                                i.getNoticeWriter(),
                                i.getNoticeDate(),
                                i.getNoticeContent(),
                                i.getNoticeAvailable()
                            )
                            )
                    list.add(contacts)
                    countKey++
                }
                annoList.clear()
                annoList.addAll(list)
                adapter.notifyDataSetChanged()
            }

            override fun onFailure(call: Call<List<NoticeListModel>>, t: Throwable) {}

        })
    }
}