package me.jagar.mindmappingandroidlibrary.Views;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

public class Item extends LinearLayout {

    private Context context;
    private TextView title;
    private String itemId;
    private int location;
    boolean defaultStyle;
    private ArrayList<Item> topChildItems = new ArrayList<>();
    private ArrayList<Item> bottomChildItems = new ArrayList<>();
    private ArrayList<Item> rightChildItems = new ArrayList<>();
    private ArrayList<Item> leftChildItems = new ArrayList<>();
    private Connection connection;
    private Item parent;


    public Item(Context context, String title, String id, boolean defaultStyle){
        super(context);
        this.context = context;
        this.defaultStyle = defaultStyle;
        this.setTitle(title);
        this.addTextViews();
        this.itemId = id;

        if (title == null)
            this.title.setVisibility(GONE);
    }

    public Item(Context context) {
        super(context);
        this.context = context;
    }

    public Item(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public Item(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public TextView getTitle(){
        return this.title;
    }
    
    public void setTitle(String title){
        this.title = new TextView(context);
        this.getTitle().setText(title);
        this.getTitle().setTypeface(Typeface.DEFAULT_BOLD);
    }

    public String getItemId(){
        return this.itemId;
    }
    
    public void setItemId(String id){
        this.itemId = id;
    }
    public void setBorder(int color, int size){
        GradientDrawable drawable = (GradientDrawable)this.getBackground();
        drawable.setStroke(size, color);
    }

    public void addTopChild(Item item){
        topChildItems.add(item);
    }
    public ArrayList<Item> getTopChildItems(){
        return topChildItems;
    }
    public Item getTopChildByIndex(int index){
        return topChildItems.get(index);
    }

    public void addBottomChild(Item item){
        bottomChildItems.add(item);
    }
    public ArrayList<Item> getBottomChildItems(){
        return bottomChildItems;
    }
    public Item getBottomChildByIndex(int index){
        return bottomChildItems.get(index);
    }

    public void addRightChild(Item item){
        rightChildItems.add(item);
    }
    public ArrayList<Item> getRightChildItems(){
        return rightChildItems;
    }
    public Item getRightChildByIndex(int index){
        return rightChildItems.get(index);
    }

    public void addLeftChild(Item item){
        leftChildItems.add(item);
    }
    public ArrayList<Item> getLeftChildItems(){
        return leftChildItems;
    }
    public Item getLeftChildByIndex(int index){
        return leftChildItems.get(index);
    }

    private void addTextViews(){
        this.setOrientation(LinearLayout.VERTICAL);
        this.addView(title);

        if (defaultStyle)
            setDefaultStyle();

    }

    //If the item default style is true
    private void setDefaultStyle(){
        GradientDrawable shape = new GradientDrawable();
        shape.setColor(Color.GRAY);
        shape.setCornerRadius(100);
        this.setBackground(shape);
        this.setBorder(Color.BLACK, 5);
        this.title.setGravity(Gravity.CENTER);

        this.setPadding(50, 20, 50, 20);

    }

    public void addParent(Item parent){
        this.parent = parent;
    }

    public Item getParents(){
        return parent;
    }

    public void setLocation(int location){
        this.location = location;
    }

    public int getLocation(){
        return location;
    }

    public Connection getConnection() {
        return connection;
    }

    public void setConnection(Connection connection) {
        this.connection = connection;
    }
}
