package com.gyso.treeview;

import android.util.Log;
import android.view.View;

import com.gyso.treeview.adapter.TreeViewAdapter;
import com.gyso.treeview.model.NodeModel;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.List;

public class TreeViewEditor {
    private final WeakReference<TreeViewAdapter<?>> adapterWeakReference;
    private final WeakReference<TreeViewContainer> containerWeakReference;
    protected TreeViewEditor(TreeViewContainer container){
        this.containerWeakReference = new WeakReference<>(container);
        this.adapterWeakReference = new WeakReference<>(container.getAdapter());
    }

    public TreeViewContainer getContainer() {
        return containerWeakReference.get();
    }

    private TreeViewAdapter<?> getAdapter() {
        return adapterWeakReference.get();
    }

    /**
     * let add node in window viewport
     */
    public void focusMidLocation() {
        TreeViewContainer container = getContainer();
        if (container != null) container.focusMidLocation();
    }

    /**
     * before you edit, requestMoveNodeByDragging(true), so than you can drag to move the node
     * @param wantEdit true for edit mode
     */
    public void requestMoveNodeByDragging(boolean wantEdit){
        TreeViewContainer container = getContainer();
        if (container!=null) {
            container.requestMoveNodeByDragging(wantEdit);
        }
    }

    /**
     * add child nodes
     * @param parent parent node should has been in tree model
     * @param childNodes new nodes that will be add to tree model
     */
    public void addChildNodes(NodeModel<?> parent, NodeModel<?>... childNodes) {
        TreeViewContainer container = getContainer();
        Log.d("Debug_Log", "TreeViewEditor/addChildNodes: " + container.getTranslationX() + " and " + container.getTranslationY());
        if(container!=null){
            container.onAddNodes(parent,childNodes);
        }
    }

    /**
     * remove node
     * @param nodeToRemove node to remove
     */
    public void removeNode(NodeModel<?> nodeToRemove){
        TreeViewContainer container = getContainer();
        if(container!=null){
            container.onRemoveNode(nodeToRemove);
        }
    }

    /**
     * remove children nodes by parent node
     * @param parentNode parent node to remove children
     */
    public void removeNodeChildren(NodeModel<?> parentNode){
        TreeViewContainer container = getContainer();
        if(container!=null){
            container.onRemoveChildNodes(parentNode);
        }
    }

    public interface  TraverseRelationshipCallback{
       <T> void callback(T root, T parent , T child);
       default  void callbackView(View rootView, View parentView , View childView){};
    }

    public interface OnNodeSelectedCallback{
        <T> void callback(T clickNode, List<T> selectedList);
    }
}
