package com.example.schoollifeproject.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.recyclerview.widget.RecyclerView
import com.example.schoollifeproject.R
import com.example.schoollifeproject.databinding.ContactsItemFileBinding
import com.example.schoollifeproject.model.FileModel

/**
 * 로드맵(마인드맵) File RecyclerView Adapter
 * 작성자 : 박동훈
 */
class ItemFileAdapter(private val itemList: MutableList<FileModel>) :
    RecyclerView.Adapter<ItemFileAdapter.ItemFileViewHolder>() {

    private lateinit var fileListener: OnFileClickListener
    private lateinit var fileDelListener: OnFileDelClickListener
    var mapEditable = true
    fun setOnFileListener(fileListener: (View, FileModel) -> Unit) {
        this.fileListener = object : OnFileClickListener {
            override fun onFileClick(view: View, fileModel: FileModel) {
                fileListener(view, fileModel)
            }
        }
    }

    fun setOnFileDelListener(fileDelListener: (View, FileModel) -> Unit) {
        this.fileDelListener = object : OnFileDelClickListener {
            override fun onFileDelClick(view: View, fileModel: FileModel) {
                fileDelListener(view, fileModel)
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
            if (!mapEditable)
                holder.delButton.visibility = View.INVISIBLE
        }

        holder.delButton.setOnClickListener {
            fileDelListener.onFileDelClick(holder.itemView, item)
        }

        holder.downButton.setOnClickListener {
            fileListener.onFileClick(holder.itemView, item)
        }
    }

    class ItemFileViewHolder(private val binding: ContactsItemFileBinding) :
        RecyclerView.ViewHolder(binding.root) {
        //게시글에 등록될 Text
        fun bind(item: FileModel) {
            binding.fileNameText.text = item.getFileName()

            if (item.getExt().compareTo("jpg", true) == 0 ||
                item.getExt().compareTo("png", true) == 0
            )
                binding.fileTypeImage.setImageResource(R.drawable.ic_item_file_image)
            else if (item.getExt().compareTo("ppt", true) == 0 ||
                item.getExt().compareTo("pptx", true) == 0 ||
                item.getExt().compareTo("hwp", true) == 0
            )
                binding.fileTypeImage.setImageResource(R.drawable.ic_item_file_docs)
            else if (item.getExt().compareTo("zip", true) == 0)
                binding.fileTypeImage.setImageResource(R.drawable.ic_item_file_zip)
        }

        val downButton: ImageButton = binding.fileDownloadButton
        val delButton: ImageButton = binding.fileDeleteButton

    }

    interface OnFileDelClickListener {
        fun onFileDelClick(item: View, fileModel: FileModel)
    }

    interface OnFileClickListener {
        fun onFileClick(item: View, fileModel: FileModel)
    }
}
