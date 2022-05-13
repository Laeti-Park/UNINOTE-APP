package com.gyso.treeview.model;

import java.io.Serializable;

public interface ITraversal<T> extends Serializable {
    void next(T next);
    default void finish(){}
}