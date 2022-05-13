package com.gyso.treeview.line;

import com.gyso.treeview.adapter.DrawInfo;

public abstract class BaseLine {
    /**
     * this method will be invoke when the tree view is onDispatchDraw
    */
    public abstract void draw(DrawInfo drawInfo);
}