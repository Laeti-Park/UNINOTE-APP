package com.example.schoollifeproject.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.schoollifeproject.NoticeActivity
import com.example.schoollifeproject.databinding.ContactsMainBoardBinding
import com.example.schoollifeproject.model.NoticeListModel

/**
 * 공지게시판 MenuActivity RecyclerView Adapter
 * 작성자 : 이준영
 * */
class AnnoListAdapter(private val itemList: List<NoticeListModel>) :
    RecyclerView.Adapter<AnnoListAdapter.AnnoViewHolder>() {
    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AnnoViewHolder {
        val binding =
            ContactsMainBoardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AnnoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AnnoViewHolder, position: Int) {
        val item = itemList[position]
        holder.apply {
            bind(item)
        }
    }

    class AnnoViewHolder(private val binding: ContactsMainBoardBinding) :
        RecyclerView.ViewHolder(binding.root) {
        //메인 메뉴 공지사항에 등록될 text, listener
        fun bind(item: NoticeListModel) {
            binding.title.text = item.getNoticeTitle()
            //공지사항 내용 확인 클릭 리스너
            binding.rootView.setOnClickListener {
                val intent = Intent(itemView.context, NoticeActivity::class.java).apply {
                    putExtra("key", item.getNoticeKey())
                    putExtra("title", item.getNoticeTitle())
                    putExtra("writer", item.getNoticeWriter())
                    putExtra("date", item.getNoticeDate())
                    putExtra("content", item.getNoticeContent())
                }
                startActivity(itemView.context, intent, null)
            }
        }
    }
}
