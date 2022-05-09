package com.example.schoollifeproject.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import com.example.schoollifeproject.R
import com.example.schoollifeproject.adapter.MapFragmentAdapter
import com.example.schoollifeproject.databinding.FragmentMapListBinding
import com.example.schoollifeproject.model.APIS
import com.example.schoollifeproject.model.MapListModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * 추천 로드맵 게시판 Fragment
 * 작성자 : 이준영, 박동훈
 */
class MapListFragment : Fragment() {
    private val TAG = this.javaClass.toString()
    private var mapListList: MutableList<MapListModel> = mutableListOf()
    private val adapter = MapFragmentAdapter(mapListList)
    private lateinit var binding: FragmentMapListBinding
    private lateinit var userID: String
    val api = APIS.create()

    fun newInstance(userID: String): MapListFragment {
        val args = Bundle()
        args.putString("userID", userID)

        val mapListFragment = MapListFragment()
        mapListFragment.arguments = args

        return mapListFragment
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        userID = arguments?.getString("userID").toString()

        binding = FragmentMapListBinding.inflate(inflater, container, false)

        val dividerItemDecoration = DividerItemDecoration(context, RecyclerView.VERTICAL)
        binding.recyclerView.addItemDecoration(dividerItemDecoration)
        binding.recyclerView.adapter = adapter
        binding.recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {})

        //user 아이디
        api.map_list(
            1
        ).enqueue(object : Callback<List<MapListModel>> {
            override fun onResponse(
                call: Call<List<MapListModel>>, response: Response<List<MapListModel>>
            ) {
                val list = mutableListOf<MapListModel>()
                for (i in response.body()!!) {
                    val contacts = (
                            MapListModel(
                                i.getMapID(),
                                i.getMapHits(),
                                i.getMapRecommend()
                            )
                            )
                    list.add(contacts)
                    mapListList.add(contacts)
                }
                mapListList.clear()
                mapListList.addAll(list)
                adapter.notifyDataSetChanged()
            }

            override fun onFailure(call: Call<List<MapListModel>>, t: Throwable) {
            }

        })

        adapter.setOnMapListener { view, mapID ->
            val mindMapFragment = MindMapFragment()
            val transaction = activity?.supportFragmentManager?.beginTransaction()
            Log.d("$TAG", "userIDSend: ${userID}, $mapID")

            transaction?.replace(R.id.frameLayout, mindMapFragment.newInstance(userID, mapID))
                ?.commitAllowingStateLoss()
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

}