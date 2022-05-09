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
import androidx.recyclerview.widget.RecyclerView
import com.example.schoollifeproject.R
import com.example.schoollifeproject.WriteNoticeActivity
import com.example.schoollifeproject.adapter.InfoFragmentAdapter
import com.example.schoollifeproject.databinding.FragmentInfoListBinding
import com.example.schoollifeproject.model.APIS
import com.example.schoollifeproject.model.InfoListModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * 공부게시판 Fragment
 * 작성자 : 박동훈
 */
class InfoListFragment : Fragment() {
    private val TAG = this.javaClass.toString()
    private var contactsList: MutableList<InfoListModel> = mutableListOf()
    private val adapter = InfoFragmentAdapter(contactsList)

    private lateinit var getResult: ActivityResultLauncher<Intent>
    private lateinit var binding: FragmentInfoListBinding

    private val api = APIS.create()
    private lateinit var userID: String
    private var countKey: Int = 0

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
        binding.recyclerView.addItemDecoration(dividerItemDecoration)
        binding.recyclerView.adapter = adapter
        binding.recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {})

        userID = arguments?.getString("ID").toString()

        //게시글 목록 호출
        posting()

        val addNote = binding.addNote

        //비회원 글작성버튼 삭제
        if (userID == "비회원") addNote.visibility = View.GONE
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
                    contactsList.add(contacts)
                    adapter.notifyDataSetChanged()
                }
            }

            override fun onFailure(call: Call<List<InfoListModel>>, t: Throwable) {

            }

        })
    }
}