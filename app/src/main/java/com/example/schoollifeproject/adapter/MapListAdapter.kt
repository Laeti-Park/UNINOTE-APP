package com.example.schoollifeproject.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.schoollifeproject.databinding.ContactsMainBoardBinding
import com.example.schoollifeproject.model.MapListModel

/**
 * 메인메뉴 추천맵 RecyclerView Adapter
 * 작성자 : 이준영, 박동훈
 * */
class MapListAdapter(private val itemList: List<MapListModel>) :
    RecyclerView.Adapter<MapListAdapter.SugViewHolder>() {

    private lateinit var mapItemListener: OnMapItemClickListener

    fun setOnMapItemListener(mapItemListener: (View, String) -> Unit) {
        this.mapItemListener = object : OnMapItemClickListener {
            override fun onMapClick(view: View, mapID: String) {
                mapItemListener(view, mapID)
            }
        }
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SugViewHolder {
        val binding =
            ContactsMainBoardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SugViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SugViewHolder, position: Int) {
        val item = itemList[position]
        holder.apply {
            bind(item)
        }

        holder.itemView.setOnClickListener {
            mapItemListener.onMapClick(holder.itemView, item.getMapID())
        }
    }


    class SugViewHolder(private val binding: ContactsMainBoardBinding) :
        RecyclerView.ViewHolder(binding.root) {
        //추천맵에 등록될 text, listener
        fun bind(item: MapListModel) {
            binding.title.text = "${item.getMapID()}님의 로드맵"
        }
    }

    interface OnMapItemClickListener {
        fun onMapClick(view: View, mapID: String)
    }
}
