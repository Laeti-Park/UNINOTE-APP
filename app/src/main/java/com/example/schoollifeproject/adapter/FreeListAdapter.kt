package com.example.schoollifeproject.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.schoollifeproject.model.AnnoContacts
import com.example.schoollifeproject.databinding.ContactsAnnoBinding
import com.example.schoollifeproject.NoticeActivity
import com.example.schoollifeproject.model.Bbs
import com.example.schoollifeproject.model.Notice

/**
 * 메인메뉴 공지 RecyclerView Adapter
 * */

class FreeListAdapter(private val itemList: List<Bbs>) :
    RecyclerView.Adapter<FreeListAdapter.FreeViewHolder>() {
    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FreeViewHolder {
        val binding =
            ContactsAnnoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FreeViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FreeViewHolder, position: Int) {
        val item = itemList[position]
        holder.apply {
            bind(item)
        }
    }

    class FreeViewHolder(private val binding: ContactsAnnoBinding) :
        RecyclerView.ViewHolder(binding.root) {
        //메인 메뉴 공지사항에 등록될 text, listener
        fun bind(item: Bbs) {
            binding.title.text = item.getBbsTitle()
            //공지사항 내용 확인 클릭 리스너
            binding.rView.setOnClickListener {
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
