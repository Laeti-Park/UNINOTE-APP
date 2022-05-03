package com.example.schoollifeproject.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.schoollifeproject.model.AnnoContacts
import com.example.schoollifeproject.databinding.AnnoContactsBinding

class AnnoListAdapter(private val itemList: List<AnnoContacts>) :
    RecyclerView.Adapter<AnnoListAdapter.AnnoViewHolder>() {
    override fun getItemCount(): Int {

        return itemList.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AnnoViewHolder {

        val binding =
            AnnoContactsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return return AnnoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AnnoViewHolder, position: Int) {

        val item = itemList[position]
        holder.apply {
            bind(item)
        }
    }


    class AnnoViewHolder(private val binding: AnnoContactsBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: AnnoContacts) {
            binding.title.text = item.title
        }
    }
}
