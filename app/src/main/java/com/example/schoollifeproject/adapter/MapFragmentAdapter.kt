package com.example.schoollifeproject.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.schoollifeproject.databinding.ContactsListItemBinding
import com.example.schoollifeproject.model.MapListModel

/**
 * 추천 로드맵 Fragment RecyclerView Adapter
 * 작성자 : 박동훈
 * */
class MapFragmentAdapter(private val itemList: MutableList<MapListModel>) :
    RecyclerView.Adapter<MapFragmentAdapter.ContactsMapViewHolder>() {

    private lateinit var mapListener: OnMapClickListener

    fun setOnMapListener(mapListener: (View, String) -> Unit) {
        this.mapListener = object : OnMapClickListener {
            override fun onMapClick(view: View, mapID: String) {
                mapListener(view, mapID)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactsMapViewHolder {
        val binding =
            ContactsListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ContactsMapViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ContactsMapViewHolder, position: Int) {
        val item = itemList[position]
        holder.apply {
            bind(item)
        }

        holder.itemView.setOnClickListener {
            mapListener.onMapClick(holder.itemView, item.getMapID())
        }
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    class ContactsMapViewHolder(private val binding: ContactsListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        //게시판에 등록될 text, listener
        fun bind(item: MapListModel) {
            binding.title.text = "${item.getMapID()}님의 로드맵"
            binding.writer.text = "${item.getMapID()}"
            binding.date.text = "추천 ${item.getMapRecommend()}"
        }
    }

    interface OnMapClickListener {
        fun onMapClick(view: View, mapID: String)
    }
}