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
import com.example.schoollifeproject.adapter.InfoFragmentAdapter
import com.example.schoollifeproject.databinding.FragmentInfoListBinding
import com.example.schoollifeproject.model.APIS
import com.example.schoollifeproject.model.InfoListModel
import com.example.schoollifeproject.model.NoteListContacts
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * 공부게시판 Fragment
 * 작성자 : 박동훈
 */
class InfoListFragment : Fragment() {
    private val TAG = this.javaClass.toString()
    private var contactsList: MutableList<NoteListContacts> = mutableListOf()
    private val adapter = InfoFragmentAdapter(contactsList)

    private lateinit var getResult2: ActivityResultLauncher<Intent>
    private lateinit var binding: FragmentInfoListBinding

    private val api = APIS.create()
    private lateinit var userID: String

    fun newInstance(userID: String): InfoListFragment {
        val args = Bundle()
        args.putString("userID", userID)

        val listFragment = InfoListFragment()
        listFragment.arguments = args

        return listFragment
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentInfoListBinding.inflate(inflater, container, false)

        val dividerItemDecoration = DividerItemDecoration(context, RecyclerView.VERTICAL)
        val manager = LinearLayoutManager(context)
        manager.reverseLayout = true
        manager.stackFromEnd = true

        binding.recyclerView.layoutManager = manager
        binding.recyclerView.addItemDecoration(dividerItemDecoration)
        binding.recyclerView.adapter = adapter
        binding.recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {})

        userID = arguments?.getString("userID").toString()

        //게시글 목록 호출
        posting()

        val addNote = binding.addNote

        //비회원 글작성버튼 삭제
        addNote.setOnClickListener {
            if (userID == "비회원") {
                Log.d("비회원글쓰기", "ㅂ")
            } else {
                val intent = Intent(context, WriteNoticeActivity::class.java)
                intent.apply {
                    putExtra("type", 2)
                    putExtra("ID", userID)
                }
                getResult2.launch(intent)
            }
        }

        //글작성 후 게시글 갱신
        getResult2 = registerForActivityResult(
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

    /**
     * RecyclerView에 포스팅할 아이템들 DB에서 호출
     * */
    private fun posting() {
        api.info_load(1).enqueue(object : Callback<List<InfoListModel>> {
            override fun onResponse(
                call: Call<List<InfoListModel>>,
                response: Response<List<InfoListModel>>
            ) {
                val list = mutableListOf<NoteListContacts>()
                for (i in response.body()!!) {
                    val contacts = (
                            NoteListContacts(
                                userID,
                                i.getStudyKey(),
                                i.getStudyTitle(),
                                i.getStudyWriter(),
                                i.getStudyDate(),
                                i.getStudyContent(),
                                i.getStudyAvailable()
                            )
                            )
                    list.add(contacts)
                    Log.d(TAG, "$list")
                }
                contactsList.clear()
                contactsList.addAll(list)
                adapter.notifyDataSetChanged()
            }

            override fun onFailure(call: Call<List<InfoListModel>>, t: Throwable) {
            }

        })
    }
}