package com.example.schoollifeproject.adapter

import android.graphics.Color
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.schoollifeproject.R
import com.example.schoollifeproject.databinding.ContactsMapNodeBinding
import com.example.schoollifeproject.model.ItemModel
import com.gyso.treeview.adapter.DrawInfo
import com.gyso.treeview.adapter.TreeViewAdapter
import com.gyso.treeview.adapter.TreeViewHolder
import com.gyso.treeview.line.BaseLine
import com.gyso.treeview.line.DashLine
import com.gyso.treeview.model.NodeModel


class ItemAdapter : TreeViewAdapter<ItemModel>() {
    private val dashLine = DashLine(Color.parseColor("#F06292"), 6)
    private lateinit var listener: OnItemClickListener
    private lateinit var longClickListener: OnItemLongClickListener
    private lateinit var doubleClicklistener: OnItemDoubleClickListener
    var mapEditable = true
    fun setOnItemListener(listener: (View, NodeModel<ItemModel>) -> Unit) {
        this.listener = object : OnItemClickListener {
            override fun onItemClick(item: View, node: NodeModel<ItemModel>) {
                listener(item, node)
            }
        }
    }

    fun setOnItemLongListener(longClickListener: (View, NodeModel<ItemModel>) -> Unit) {
        this.longClickListener = object : OnItemLongClickListener {
            override fun onItemLongClick(item: View, node: NodeModel<ItemModel>) {
                longClickListener(item, node)
            }
        }
    }

    fun setOnItemDoubleListener(doubleClickListener: (View, NodeModel<ItemModel>, b: Boolean) -> Unit) {
        this.doubleClicklistener = object : OnItemDoubleClickListener {
            override fun onItemDoubleClick(item: View, node: NodeModel<ItemModel>, b: Boolean) {
                doubleClickListener(item, node, b)
            }
        }
    }

    override fun onCreateViewHolder(
        viewGroup: ViewGroup,
        node: NodeModel<ItemModel>,
    ): TreeViewHolder<ItemModel> {
        val nodeBinding: ContactsMapNodeBinding =
            ContactsMapNodeBinding.inflate(LayoutInflater.from(viewGroup.context), viewGroup, false)
        return TreeViewHolder(nodeBinding.root, node)
    }

    override fun onBindViewHolder(holder: TreeViewHolder<ItemModel>) {
        val itemView = holder.view
        val node: NodeModel<ItemModel> = holder.node
        val nodeBack = itemView.findViewById<ConstraintLayout>(R.id.item_back)
        val contentView = itemView.findViewById<TextView>(R.id.content)
        val item: ItemModel = node.value
        var i = 0
        contentView.text = item.getContent()

            nodeBack.setOnClickListener { v ->
                i++
                val handler = Handler()
                val r = Runnable { i = 0 }

                if (i == 1) {
                    handler.postDelayed(r, 250);
                    if (mapEditable) {
                        listener.onItemClick(v, node)
                    }
                } else if (i == 2) {
                    i = 0;
                    if (mapEditable) {
                        doubleClicklistener.onItemDoubleClick(v, node, true)
                    } else {
                        doubleClicklistener.onItemDoubleClick(v, node, false)
                    }
                }
            }

            nodeBack.setOnLongClickListener { v ->
                if(mapEditable) {
                    longClickListener.onItemLongClick(v, node)
                }
                true
            }
    }

    override fun onDrawLine(drawInfo: DrawInfo): BaseLine? {
        return null
    }

    interface OnItemClickListener {
        fun onItemClick(item: View, node: NodeModel<ItemModel>)
    }

    interface OnItemLongClickListener {
        fun onItemLongClick(item: View, node: NodeModel<ItemModel>)
    }

    interface OnItemDoubleClickListener {
        fun onItemDoubleClick(item: View, node: NodeModel<ItemModel>, b: Boolean)
    }
}

