package com.gyso.treeview.listener;

import android.view.View;
import androidx.annotation.Nullable;
import com.gyso.treeview.model.NodeModel;

public interface TreeViewControlListener {
    int MIN_SCALE  = -1;
    int FREE_SCALE = 0;
    int MAX_SCALE  = 1;
    void onScaling(int state, int percent);
    void onDragMoveNodesHit(@Nullable NodeModel<?> draggingNode, @Nullable NodeModel<?> hittingNode, @Nullable View draggingView, @Nullable View hittingView);
    void onDragMoveNodesEnd(@Nullable NodeModel<?> draggingNode, @Nullable NodeModel<?> hittingNode, @Nullable View draggingView, @Nullable View hittingView);
}
