package com.example.schoollifeproject.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.schoollifeproject.databinding.ContactsNoticeReadBinding
import com.example.schoollifeproject.model.NoteReadContacts
/**
 * 게시글 RecyclerView Adapter
 * */
class NoteReadListAdapter(private val itemList: List<NoteReadContacts>) :
    RecyclerView.Adapter<NoteReadListAdapter.NoteReadViewHolder>() {
    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteReadViewHolder {
        val binding =
            ContactsNoticeReadBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NoteReadViewHolder(binding)
    }

    override fun onBindViewHolder(holder: NoteReadViewHolder, position: Int) {
        val item = itemList[position]
        holder.apply {
            bind(item)
        }
    }


    class NoteReadViewHolder(private val binding: ContactsNoticeReadBinding) :
        RecyclerView.ViewHolder(binding.root) {
        //게시글에 등록될 Text
        fun bind(item: NoteReadContacts) {
            binding.title.text = item.title
            binding.date.text = item.date
            binding.writer.text = item.writer
            binding.content.text = item.contents
        }
    }
}
