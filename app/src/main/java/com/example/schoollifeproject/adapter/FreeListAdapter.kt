package com.example.schoollifeproject.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.schoollifeproject.databinding.ContactsMainBoardBinding
import com.example.schoollifeproject.NoticeActivity
import com.example.schoollifeproject.model.FreeListModel

/**
 * 자유 게시판 RecyclerView Adapter
 * 작성자 : 이준영, 박동훈
 * */
class FreeListAdapter(private val itemList: MutableList<FreeListModel>) :
    RecyclerView.Adapter<FreeListAdapter.ContactsViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactsViewHolder {
        val binding =
            ContactsMainBoardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ContactsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ContactsViewHolder, position: Int) {
        val item = itemList[position]
        holder.apply {
            bind(item)
        }
    }

    override fun getItemCount(): Int {
        return itemList.size
    }


    class ContactsViewHolder(private val binding: ContactsMainBoardBinding) :
        RecyclerView.ViewHolder(binding.root) {
        //게시판에 등록될 text, listener
        fun bind(item: FreeListModel) {
            binding.title.text = item.getBbsTitle()
            //게시글 내용확인 클릭리스너
            binding.rootView.setOnClickListener {
                val intent = Intent(itemView.context, NoticeActivity::class.java).apply {
                    putExtra("key", item.getBbsKey())
                    putExtra("title", item.getBbsTitle())
                    putExtra("writer", item.getBbsWriter())
                    putExtra("date", item.getBbsDate())
                    putExtra("content", item.getBbsContent())

                }
                startActivity(itemView.context, intent, null)
            }
        }
    }
}