package com.gyso.treeview.listener;


import com.gyso.treeview.model.NodeModel;

public interface TreeViewNotifier{
    void onDataSetChange();
    void onRemoveNode(NodeModel<?>nodeModel);
    void onRemoveChildNodes(NodeModel<?> parentNode);
    void onItemViewChange(NodeModel<?> nodeModel);
    void onAddNodes(NodeModel<?> parent, NodeModel<?>... childNodes);
}