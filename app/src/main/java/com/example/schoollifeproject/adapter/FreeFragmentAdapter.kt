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
 * 자유 게시판 RecyclerView Adapter
 * 작성자 : 이준영
 * */
class FreeFragmentAdapter(private val itemList: MutableList<NoteListContacts>) :
    RecyclerView.Adapter<FreeFragmentAdapter.ContactsViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactsViewHolder {
        val binding =
            ContactsListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
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


    class ContactsViewHolder(private val binding: ContactsListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        //게시판에 등록될 text, listener
        fun bind(item: NoteListContacts) {
            binding.title.text = item.noteTitle
            binding.writer.text = item.userID
            binding.date.text = item.noteDate
            //게시글 내용확인 클릭리스너
            binding.rootView.setOnClickListener {
                val intent = Intent(itemView.context, NoticeActivity::class.java).apply {
                    putExtra("userID", item.loginID)
                    putExtra("key", item.noteID)
                    putExtra("title", item.noteTitle)
                    putExtra("writer", item.userID)
                    putExtra("date", item.noteDate)
                    putExtra("content", item.noteContent)
                    putExtra("type", 1)

                }
                startActivity(itemView.context, intent, null)
            }
        }
    }
}