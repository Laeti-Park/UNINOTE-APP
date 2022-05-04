package com.example.schoollifeproject.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.schoollifeproject.databinding.NoticeReadContactsBinding
import com.example.schoollifeproject.model.NoteReadContacts

class NoteReadListAdapter (private val itemList: List<NoteReadContacts>) :
    RecyclerView.Adapter<NoteReadListAdapter.NoteReadViewHolder>() {
    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteReadViewHolder {

        val binding =
            NoticeReadContactsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NoteReadViewHolder(binding)
    }

    override fun onBindViewHolder(holder: NoteReadViewHolder, position: Int) {

        val item = itemList[position]
        holder.apply {
            bind(item)
        }
    }


    class NoteReadViewHolder(private val binding: NoticeReadContactsBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: NoteReadContacts) {
            binding.title.text = item.title
            /*binding.views.text = item.Views*/
            binding.writer.text = item.writer
            binding.content.text = item.contents
        }
    }
}
