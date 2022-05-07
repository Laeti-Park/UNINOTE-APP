package com.example.schoollifeproject.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.schoollifeproject.WriteNoticeActivity
import com.example.schoollifeproject.adapter.ContactsListAdapter
import com.example.schoollifeproject.databinding.FragmentListBinding
import com.example.schoollifeproject.model.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

/**
 * 게시판 화면 전환 Fragment
 */
class ListFragment : Fragment() {
    private var contactsList: MutableList<Contacts> = mutableListOf()
    private val adapter = ContactsListAdapter(contactsList)

    private lateinit var getResult: ActivityResultLauncher<Intent>
    private lateinit var binding: FragmentListBinding

    private val api = APIS.create()
    private var id: String? = arguments?.getString("ID").toString()
    private var countKey: Int = 0


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentListBinding.inflate(inflater, container, false)
        binding.recyclerView.adapter = adapter
        binding.recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {})

        //게시글 목록 호출
        posting()


        val addNote = binding.addNote

        //비회원 글작성버튼 삭제
        if (id == "비회원") addNote.visibility = View.GONE
        else addNote.visibility = View.VISIBLE

        //글작성 버튼 클릭
        addNote.setOnClickListener {
            val intent = Intent(context, WriteNoticeActivity::class.java)
            intent.apply {
                putExtra("ID", id)
            }
            getResult.launch(intent)
        }

        //글작성 후 게시글 갱신
        getResult = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) {
            if (it.resultCode == AppCompatActivity.RESULT_OK) {
                posting()
            }
            adapter.notifyDataSetChanged()
        }

        return binding.root
    }

    /**
     * RecyclerView에 포스팅할 아이템들 DB에서 호출
     * */
    private fun posting() {
        api.bbs_load(
            0   //type 0 = 일반 포스팅, type 1 = 공지 포스팅
        ).enqueue(object : Callback<List<Bbs>> {
            override fun onResponse(call: Call<List<Bbs>>, response: Response<List<Bbs>>) {
                val list = mutableListOf<Contacts>()
                //아이템 개수만큼 호출, 연결
                for (i in response.body()!!) {
                    val contacts = (
                            Contacts(
                                i.getBbsKey(),
                                i.getBbsTitle(),
                                i.getBbsWriter(),
                                i.getBbsDate(),
                                i.getBbsContent()
                            )
                            )
                    list.add(contacts)
                    countKey++

                }
                contactsList.clear()
                contactsList.addAll(list)
                adapter.notifyDataSetChanged()

            }

            override fun onFailure(call: Call<List<Bbs>>, t: Throwable) {}
        })
    }

}