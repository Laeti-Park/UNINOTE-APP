package com.example.schoollifeproject.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.example.schoollifeproject.R
import com.example.schoollifeproject.adapter.MapListAdapter
import com.example.schoollifeproject.databinding.FragmentSettingBinding
import com.example.schoollifeproject.model.APIS
import com.example.schoollifeproject.model.MapContacts
import com.example.schoollifeproject.model.MapModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * 맵 게시판 Fragment
 * */
class MapListFragment : Fragment() {
    private val TAG = this.javaClass.toString()
    private var contactsList: MutableList<MapContacts> = mutableListOf()
    private val adapter = MapListAdapter(contactsList)
    private lateinit var binding: FragmentSettingBinding
    private var id: String? = arguments?.getString("ID").toString()
    val api = APIS.create()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSettingBinding.inflate(inflater, container, false)
        binding.recyclerView.adapter = adapter
        binding.recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {})

        //user 아이디
        api.map_list(
            1
        ).enqueue(object : Callback<List<MapModel>> {
            override fun onResponse(call: Call<List<MapModel>>, response: Response<List<MapModel>>) {
                val list = mutableListOf<MapContacts>()
                for (i in response.body()!!) {
                    val contacts = (
                                MapContacts(
                                    i.getMapID()
                                )
                            )
                    list.add(contacts)
                    contactsList.add(contacts)
                }
                contactsList.clear()
                contactsList.addAll(list)
                adapter.notifyDataSetChanged()
            }

            override fun onFailure(call: Call<List<MapModel>>, t: Throwable) {
            }

        })

        adapter.setOnMapListener { view, mapID ->
            val bundle = Bundle()
            val mindMapFragment = MindMapFragment()
            bundle.putString("ID", id)
            bundle.putString("mapID", mapID)
            val transaction = activity?.supportFragmentManager?.beginTransaction()

            mindMapFragment.arguments = bundle
            Log.d("$TAG", "userIDSend: ${id}, ${mapID}")

            transaction?.replace(R.id.frameLayout, mindMapFragment.newInstance(id.toString(), mapID))?.commitAllowingStateLoss()
        }

        return binding.root
    }

}