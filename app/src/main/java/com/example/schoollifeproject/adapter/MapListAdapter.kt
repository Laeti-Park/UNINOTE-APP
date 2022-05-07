package com.example.schoollifeproject.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.schoollifeproject.databinding.ItemContactsBinding
import com.example.schoollifeproject.model.MapContacts
import com.example.schoollifeproject.noticeActivity


class MapListAdapter(private val itemList: MutableList<MapContacts>) :
    RecyclerView.Adapter<MapListAdapter.ContactsMapViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactsMapViewHolder {
        val binding =
            ItemContactsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ContactsMapViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ContactsMapViewHolder, position: Int) {
        val item = itemList[position]
        holder.apply {
            bind(item)
        }
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    class ContactsMapViewHolder(private val binding: ItemContactsBinding) :
        RecyclerView.ViewHolder(binding.root) {
        //게시판에 등록될 text, listener
        fun bind(item: MapContacts) {
            binding.title.text = item.userID + "님의 로드맵"
            binding.writer.text = item.userID
            //게시글 내용확인 클릭리스너
            binding.rootView.setOnClickListener {
                val intent = Intent(itemView.context, noticeActivity::class.java).apply{
                    //게시글 눌렀을 때 엑티비티 출현 (new박동훈)

                }
                startActivity(itemView.context, intent, null)
            }
        }
    }
}