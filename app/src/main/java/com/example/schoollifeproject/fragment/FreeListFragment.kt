package com.example.schoollifeproject.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.schoollifeproject.R
import com.example.schoollifeproject.WriteNoticeActivity
import com.example.schoollifeproject.adapter.FreeFragmentAdapter
import com.example.schoollifeproject.databinding.FragmentFreeListBinding
import com.example.schoollifeproject.model.APIS
import com.example.schoollifeproject.model.FreeListModel
import com.example.schoollifeproject.model.NoteListContacts
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * 자유게시판 Fragment
 * 작성자 : 이준영, 박동훈
 */
class FreeListFragment : Fragment() {
    private val TAG = this.javaClass.toString()
    private var contactsList: MutableList<NoteListContacts> = mutableListOf()
    private val adapter = FreeFragmentAdapter(contactsList)

    private lateinit var getResult: ActivityResultLauncher<Intent>
    private lateinit var binding: FragmentFreeListBinding

    private val api = APIS.create()
    private lateinit var userID: String

    fun newInstance(userID: String): FreeListFragment {
        val args = Bundle()
        args.putString("userID", userID)

        val listFragment = FreeListFragment()
        listFragment.arguments = args

        return listFragment
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFreeListBinding.inflate(inflater, container, false)

        val dividerItemDecoration = DividerItemDecoration(context, RecyclerView.VERTICAL)

        val manager = LinearLayoutManager(context)
        manager.reverseLayout = true
        manager.stackFromEnd = true

        binding.recyclerView.layoutManager = manager
        binding.recyclerView.addItemDecoration(dividerItemDecoration)
        binding.recyclerView.adapter = adapter
        binding.recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {})

        userID = arguments?.getString("userID").toString()
        Log.d("아이디: ", "$userID")
        //게시글 목록 호출
        posting()

        val addNote = binding.addNote


        /**
         * 글작성 버튼
         * 비회원은 사용불가
         */
        addNote.setOnClickListener {
            if (userID == "비회원") {
                Toast.makeText(this.context, "비회원은 이용이 불가능합니다.", Toast.LENGTH_SHORT).show()
            } else {
                val intent = Intent(context, WriteNoticeActivity::class.java)
                intent.apply {
                    putExtra("type", 1)
                    putExtra("ID", userID)
                }
                getResult.launch(intent)
            }
        }

        /**
         * 글작성 후 리턴받은 result를 실행
         */
        getResult = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) {
            if (it.resultCode == AppCompatActivity.RESULT_OK) {
                posting()
            }

            adapter.notifyDataSetChanged()
        }

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

    override fun onResume() {
        super.onResume()
        posting()
    }

    /**
     * RecyclerView에 포스팅할 아이템들 DB에서 호출
     * */
    private fun posting() {
        api.bbs_load(
            0   //type 0 = 일반 포스팅, type 1 = 공지 포스팅
        ).enqueue(object : Callback<List<FreeListModel>> {
            override fun onResponse(
                call: Call<List<FreeListModel>>, response: Response<List<FreeListModel>>
            ) {
                val list = mutableListOf<NoteListContacts>()
                //아이템 개수만큼 호출, 연결
                for (i in response.body()!!) {
                    if (i.getBbsAvailable() == 1) {
                        val contacts = (
                                NoteListContacts(
                                    userID,
                                    i.getBbsKey(),
                                    i.getBbsTitle(),
                                    i.getBbsWriter(),
                                    i.getBbsDate(),
                                    i.getBbsContent(),
                                    i.getBbsAvailable()
                                )
                                )
                        list.add(contacts)
                    }
                }

                contactsList.clear()
                contactsList.addAll(list)
                adapter.notifyDataSetChanged()
            }

            override fun onFailure(call: Call<List<FreeListModel>>, t: Throwable) {}
        })
    }
}