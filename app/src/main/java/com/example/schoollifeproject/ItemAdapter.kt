package com.example.schoollifeproject

import android.graphics.Color
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.schoollifeproject.databinding.NodeBaseLayoutBinding
import com.gyso.treeview.adapter.DrawInfo
import com.gyso.treeview.adapter.TreeViewAdapter
import com.gyso.treeview.adapter.TreeViewHolder
import com.gyso.treeview.line.BaseLine
import com.gyso.treeview.line.DashLine
import com.gyso.treeview.model.NodeModel

class ItemAdapter : TreeViewAdapter<ItemInfo>() {
    private val dashLine = DashLine(Color.parseColor("#F06292"), 6)
    private lateinit var listener: OnItemClickListener
    private lateinit var longClickListener: OnItemLongClickListener
    fun setOnItemListener(listener: (View, NodeModel<ItemInfo>) -> Unit) {
        this.listener = object: OnItemClickListener{
            override fun onItemClick(item: View, node: NodeModel<ItemInfo>) {
                listener(item, node)
            }
        }
    }
    fun setOnItemLongListener(longClickListener: (View, NodeModel<ItemInfo>) -> Unit) {
        this.longClickListener = object: OnItemLongClickListener{
            override fun onItemLongClick(item: View, node: NodeModel<ItemInfo>) {
                longClickListener(item, node)
            }
        }
    }

    override fun onCreateViewHolder(
        viewGroup: ViewGroup,
        node: NodeModel<ItemInfo>,
    ): TreeViewHolder<ItemInfo> {
        val nodeBinding: NodeBaseLayoutBinding =
            NodeBaseLayoutBinding.inflate(LayoutInflater.from(viewGroup.context), viewGroup, false)
        return TreeViewHolder(nodeBinding.root, node)
    }

    override fun onBindViewHolder(holder: TreeViewHolder<ItemInfo>) {
        //todo get view and node from holder, and then show by you
        val itemView = holder.view
        val node: NodeModel<ItemInfo> = holder.node
        val nodeBack = itemView.findViewById<ConstraintLayout>(R.id.item_back)
        val titleView = itemView.findViewById<TextView>(R.id.title)
        val item: ItemInfo = node.value
        titleView.text = item.getTitle()

        nodeBack.setOnClickListener { v ->
            if (listener != null) {
                listener.onItemClick(v, node)
            }
        }

        nodeBack.setOnLongClickListener { v ->
            if (longClickListener != null) {
                longClickListener.onItemLongClick(v, node)
            }
            true
        }
    }

    override fun onDrawLine(drawInfo: DrawInfo): BaseLine? {
        // TODO If you return an BaseLine, line will be draw by the return one instead of TreeViewLayoutManager's
//        TreeViewHolder<?> toHolder = drawInfo.getToHolder();
//        NodeModel<?> node = toHolder.getNode();
//        Object value = node.getValue();
//        if(value instanceof Animal){
//            Animal animal = (Animal) value;
//            if("sub4".compareToIgnoreCase(animal.name)<=0){
//                return dashLine;
//            }
//        }
        return null
    }

    interface OnItemClickListener {
        fun onItemClick(item: View, node: NodeModel<ItemInfo>)
    }

    interface OnItemLongClickListener {
        fun onItemLongClick(item: View, node: NodeModel<ItemInfo>)
    }
}

