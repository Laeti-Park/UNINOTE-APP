package com.gyso.treeview.algorithm.force;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.RetentionPolicy.SOURCE;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

public class FNode {

    public static final int ROOT_NODE_LEVEL = 0;

    static final short DRAG_START = 2;
    static final short DRAG = 4;
    static final short DRAG_END = 6;

    private String text;
    private Object obj;
    private int level;

    public float x, y;
    float px, py;
    int weight;

    private float radius = 50f;
    private short state;

    public FNode(String text, float radius, int level) {
        this.text = text;
        this.radius = radius;
        this.level = level;
        x = y = -1f;
        weight = 1;
    }

    public String getText() {
        return text;
    }
    public int getLevel() {
        return level;
    }
    float getRadius() {
        return radius;
    }

    boolean isInside(float x, float y, float scale) {
        float left = (this.x - radius) * scale;
        float top = (this.y - radius) * scale;
        float right = (this.x + radius) * scale;
        float bottom = (this.y + radius) * scale;
        return x >= left && x <= right && y >= top && y <= bottom;
    }

    boolean isStable() {
        return state != 0;
    }

    void setDragState(@State short state) {
        switch (state) {
            case DRAG_START:
                this.state |= state;
                break;
            case DRAG_END:
                this.state &= ~state;
                break;
        }
    }

    @ShortDef({DRAG_START, DRAG, DRAG_END})
    @Retention(SOURCE)
    public @interface State {
    }

    @Retention(SOURCE)
    @Target({ANNOTATION_TYPE})
    public @interface ShortDef {
        short[] value() default {};

        boolean flag() default false;
    }

}
