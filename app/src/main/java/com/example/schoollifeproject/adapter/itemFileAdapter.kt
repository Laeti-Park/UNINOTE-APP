package com.example.schoollifeproject.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.schoollifeproject.databinding.ContactsItemFileBinding
import com.example.schoollifeproject.model.FileModel
import com.example.schoollifeproject.model.ItemInfo
import com.example.schoollifeproject.model.NoteReadContacts
import com.gyso.treeview.model.NodeModel

class ItemFileAdapter(private val itemList: MutableList<FileModel>) :
    RecyclerView.Adapter<ItemFileAdapter.ItemFileViewHolder>() {

    private val TAG = this.javaClass.toString()
    private lateinit var fileListener: OnFileClickListener

    fun setOnFileListener(fileListener: (View, FileModel) -> Unit) {
        this.fileListener = object : OnFileClickListener {
            override fun onFileClick(view: View, fileModel: FileModel) {
                fileListener(view, fileModel)
            }
        }
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemFileViewHolder {
        val binding =
            ContactsItemFileBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return ItemFileViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ItemFileViewHolder, position: Int) {
        val item = itemList[position]
        holder.apply {
            bind(item)
        }

        holder.itemView.setOnClickListener {
            Log.d("$TAG", "fileClickListener")
            fileListener.onFileClick(holder.itemView, item)
        }
    }

    class ItemFileViewHolder(private val binding: ContactsItemFileBinding) :
        RecyclerView.ViewHolder(binding.root) {
        //게시글에 등록될 Text
        fun bind(item: FileModel) {
            binding.fileNameText.text = item.getFileName()
        }
    }
    interface OnFileClickListener {
        fun onFileClick(item: View, fileModel: FileModel)
    }
}
