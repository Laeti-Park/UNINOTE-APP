package com.example.schoollifeproject.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.schoollifeproject.databinding.SugContactsBinding
import com.example.schoollifeproject.model.SugContacts

class SugListAdapter(private val itemList: List<SugContacts>) :
    RecyclerView.Adapter<SugListAdapter.SugViewHolder>() {
    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SugViewHolder {

        val binding =
            SugContactsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SugViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SugViewHolder, position: Int) {

        val item = itemList[position]
        holder.apply {
            bind(item)
        }
    }


    class SugViewHolder(private val binding: SugContactsBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: SugContacts) {
            binding.title.text = item.title
            binding.sugTv.text = item.sug.toString()
        }
    }
}
