package com.example.schoollifeproject.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.schoollifeproject.databinding.ContactsListItemBinding
import com.example.schoollifeproject.NoticeActivity
import com.example.schoollifeproject.model.NoteListContacts

/**
 * 공지게시판 프래그먼트 RecyclerView Adapter
 * 작성자 : 박동훈
 * */
class AnnoFragmentAdapter(private val itemList: List<NoteListContacts>) :
    RecyclerView.Adapter<AnnoFragmentAdapter.AnnoViewHolder>() {
    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AnnoViewHolder {
        val binding =
            ContactsListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AnnoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AnnoViewHolder, position: Int) {
        val item = itemList[position]
        holder.apply {
            bind(item)
        }
    }

    class AnnoViewHolder(private val binding: ContactsListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        //메인 메뉴 공지사항에 등록될 text, listener
        fun bind(item: NoteListContacts) {
            binding.title.text = item.noteTitle
            //공지사항 내용 확인 클릭 리스너
            binding.rootView.setOnClickListener {
                val intent = Intent(itemView.context, NoticeActivity::class.java).apply {
                    putExtra("userID", item.loginID)
                    putExtra("key", item.noteID)
                    putExtra("title", item.noteTitle)
                    putExtra("writer", item.userID)
                    putExtra("date", item.noteDate)
                    putExtra("content", item.noteContent)
                    putExtra("type", 0)
                }
                startActivity(itemView.context, intent, null)
            }
        }
    }
}
