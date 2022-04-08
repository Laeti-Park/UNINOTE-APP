package me.jagar.mindmappingandroidlibrary.Views;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import java.util.ArrayList;

public class MindMappingView extends RelativeLayout {

    private Context context;
    private ArrayList<Connection> topItems = new ArrayList<>();
    private ArrayList<Connection> leftItems = new ArrayList<>();
    private ArrayList<Connection> rightItems = new ArrayList<>();
    private ArrayList<Connection> bottomItems = new ArrayList<>();
    private int connectionWidth = 10, connectionArrowSize = 30, connectionCircRadius = 20, connectionArgSize = 30;
    private String connectionColor = "#000000";

    public MindMappingView(Context context) {
        super(context);
        this.context = context;
    }

    public MindMappingView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MindMappingView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
    }

    //Getters & Setters
    public String getConnectionColor() {
        return connectionColor;
    }

    public void setConnectionColor(String connectionColor) {
        this.connectionColor = connectionColor;
    }

    public int getConnectionWidth() {
        return connectionWidth;
    }

    public void setConnectionWidth(int connectionWidth) {
        this.connectionWidth = connectionWidth;
    }

    public int getConnectionArrowSize() {
        return connectionArrowSize;
    }

    public void setConnectionArrowSize(int connectionArrowSize) {
        this.connectionArrowSize = connectionArrowSize;
    }

    public int getConnectionCircRadius() {
        return connectionCircRadius;
    }

    public void setConnectionCircRadius(int connectionCircSize) {
        this.connectionCircRadius = connectionCircSize;
    }

    public int getConnectionArgSize() {
        return connectionArgSize;
    }

    public void setConnectionArgSize(int connectionArgSize) {
        this.connectionArgSize = connectionArgSize;
    }


    //Adding the root item
    @SuppressLint("ClickableViewAccessibility")
    public void addCentralItem(Item item, boolean dragAble) {

        item.setGravity(CENTER_IN_PARENT);
        this.setGravity(Gravity.CENTER);

        if (dragAble) {
            dragItem(item);
        }

        this.addView(item);
    }

    /*Make any item drag able, This will make issues with
    a simple call of OnClickListener on the Item objects so you set it off to call the normal onclicklistener
    the custom OnItemClicked*/

    @SuppressLint("ClickableViewAccessibility")
    private void dragItem(final Item item) {
        final float[] dX = new float[1];
        final float[] dY = new float[1];

        item.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {

                    case MotionEvent.ACTION_DOWN:
                        dX[0] = view.getX() - motionEvent.getRawX();
                        dY[0] = view.getY() - motionEvent.getRawY();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        view.animate()
                                .x(motionEvent.getRawX() + dX[0])
                                .y(motionEvent.getRawY() + dY[0])
                                .setDuration(0)
                                .start();
                        invalidate();

                        break;
                    default:
                        item.setPressed(false);
                        return false;
                }
                return true;
            }
        });

    }

    //Adding an item that has the parent already on the view
    public void addItem(Item item, Item parent, int distance, int spacing, int location,
                        boolean dragAble, ConnectionTextMessage connectionTextMessage) {
        item.setLocation(location);
        item.setGravity(CENTER_IN_PARENT);

        if (location == 0) {

            this.addView(item);

            Connection connection = new Connection(item, parent, connectionTextMessage);

            topItems.add(connection);
            item.addParent(parent);
            item.setConnection(connection);

            item.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
            parent.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
            item.setY(parent.getY() - (item.getMeasuredHeight() + distance));
            parent.addTopChild(item);

            if (parent.getTopChildItems().size() > 1) {
                Item lastChildItem = parent.getTopChildByIndex(parent.getTopChildItems().size() - 2);
                lastChildItem.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
                for (Item topItem : parent.getTopChildItems()) {
                    topItem.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
                    topItem.setX(topItem.getX() - (item.getMeasuredWidth() / 2 + spacing));
                }
                item.setX(lastChildItem.getX() + lastChildItem.getMeasuredWidth() + spacing);
            } else {
                item.setX(parent.getX());
            }

            if (dragAble)
                dragItem(item);

        } else if (location == 1) {
            this.addView(item);

            item.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
            parent.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
            item.setX(parent.getX() - (item.getMeasuredWidth() + distance));

            parent.addLeftChild(item);

            if (parent.getLeftChildItems().size() > 1) {
                Item lastChildItem = parent.getLeftChildByIndex(parent.getLeftChildItems().size() - 2);
                lastChildItem.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
                for (Item leftItem : parent.getLeftChildItems()) {
                    leftItem.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
                    leftItem.setY(leftItem.getY() - (item.getMeasuredHeight() / 2 + spacing));
                }
                item.setY(lastChildItem.getY() + lastChildItem.getMeasuredHeight() + spacing);
            }

            Connection connection = new Connection(item, parent);
            leftItems.add(connection);
            item.addParent(parent);
            item.setConnection(connection);

            if (dragAble)
                dragItem(item);

        } else if (location == 2) {
            this.addView(item);

            item.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
            parent.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
            item.setX(parent.getX() + (parent.getMeasuredWidth() + distance));

            parent.addRightChild(item);

            if (parent.getRightChildItems().size() > 1) {
                Item lastChildItem = parent.getRightChildByIndex(parent.getRightChildItems().size() - 2);
                lastChildItem.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
                for (Item rightItem : parent.getRightChildItems()) {
                    rightItem.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
                    rightItem.setY(rightItem.getY() - (item.getMeasuredHeight() / 2 + spacing));
                }
                item.setY(lastChildItem.getY() + lastChildItem.getMeasuredHeight() + spacing);
            }

            Connection connection = new Connection(item, parent);
            rightItems.add(connection);
            item.addParent(parent);
            item.setConnection(connection);

            if (dragAble)
                dragItem(item);

        } else if (location == 3) {

            this.addView(item);

            item.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
            parent.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
            item.setY(parent.getY() + (parent.getMeasuredHeight() + distance));

            parent.addBottomChild(item);

            if (parent.getBottomChildItems().size() > 1) {
                Item lastChildItem = parent.getBottomChildByIndex(parent.getBottomChildItems().size() - 2);
                lastChildItem.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
                for (Item bottomItem : parent.getBottomChildItems()) {
                    bottomItem.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
                    bottomItem.setX(bottomItem.getX() - (item.getMeasuredWidth() / 2 + spacing));
                }
                item.setX(lastChildItem.getX() + lastChildItem.getMeasuredWidth() + spacing);
            } else {
                item.setX(parent.getX());
            }

            Connection connection = new Connection(item, parent);
            bottomItems.add(connection);
            item.addParent(parent);
            item.setConnection(connection);

            if (dragAble)
                dragItem(item);
        }

    }

    public void deleteItem(Item item) {
        Item parent = item.getParents();
        int location = item.getLocation();

        if (location == 0) {
            this.topItems.remove(item.getConnection());
            parent.getTopChildItems().remove(item);
        } else if (location == 1) {
            this.leftItems.remove(item.getConnection());
            parent.getLeftChildItems().remove(item);
        } else if (location == 2) {
            this.rightItems.remove(item.getConnection());
            parent.getRightChildItems().remove(item);
        } else if (location == 3) {
            this.bottomItems.remove(item.getConnection());
            parent.getBottomChildItems().remove(item);
        }
        this.removeView(item);
        item.removeAllViews();
    }

    //Draw connections
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawTopLines(canvas);
        drawLeftLines(canvas);
        drawRightLines(canvas);
        drawBottomLines(canvas);
    }

    //Draw connections (default)
    private void drawTopLines(Canvas canvas) {

        for (Connection connection : topItems) {
            Item item = connection.getItem();
            Item parent = connection.getParent();
            int x1 = (int) (parent.getX() + parent.getWidth() / 2);
            int y1 = (int) (parent.getY());
            int x2 = (int) (item.getX() + item.getWidth() / 2);
            int y2 = (int) (item.getY() + item.getHeight());
            int radius = (int) (((item.getY() + item.getHeight()) - (parent.getY())) / 4);
            drawCurvedArrowTop(x1, y1, x2, y2, radius, canvas,
                    item.getX() > parent.getX(), item.getConnection().getConnectionTextMessage(), connection);
        }
    }

    private void drawLeftLines(Canvas canvas) {

        for (Connection connection : leftItems) {
            Item item = connection.getItem();
            Item parent = connection.getParent();
            int x1 = (int) (parent.getX());
            int y1 = (int) (parent.getY() + parent.getHeight() / 2);
            int x2 = (int) (item.getX() + item.getWidth());
            int y2 = (int) (item.getY() + item.getHeight() / 2);
            int radius = (int) (((item.getX() + item.getWidth()) - (parent.getX())) / 4);
            drawCurvedArrowLeft(x1, y1, x2, y2, radius, canvas,
                    (item.getY() + item.getHeight()) < (parent.getY() + parent.getHeight()),
                    item.getConnection().getConnectionTextMessage(), connection);
        }
    }

    private void drawRightLines(Canvas canvas) {

        for (Connection connection : rightItems) {
            Item item = connection.getItem();
            Item parent = connection.getParent();
            int x1 = (int) (parent.getX() + parent.getWidth());
            int y1 = (int) (parent.getY() + parent.getHeight() / 2);
            int x2 = (int) (item.getX());
            int y2 = (int) (item.getY() + item.getHeight() / 2);
            int radius = (int) (((parent.getX() + parent.getWidth()) - (item.getX())) / 4);
            drawCurvedArrowRight(x1, y1, x2, y2, radius, canvas,
                    (item.getY() + item.getHeight()) < (parent.getY() + parent.getHeight()),
                    item.getConnection().getConnectionTextMessage(), connection);
        }
    }

    private void drawBottomLines(Canvas canvas) {

        for (Connection connection : bottomItems) {
            Item item = connection.getItem();
            Item parent = connection.getParent();
            int x1 = (int) (parent.getX() + parent.getWidth() / 2);
            int y1 = (int) (parent.getY() + parent.getHeight());
            int x2 = (int) (item.getX() + item.getWidth() / 2);
            int y2 = (int) (item.getY());
            int radius = (int) (((parent.getY() + parent.getHeight()) - (item.getY())) / 4);
            drawCurvedArrowBottom(x1, y1, x2, y2, radius, canvas,
                    item.getX() > parent.getX(), item.getConnection().getConnectionTextMessage(), connection);
        }
    }

    private void drawCurvedArrowTop(int x1, int y1, int x2, int y2, int curveRadius, Canvas canvas, boolean right,
                                    ConnectionTextMessage connectionTextMessage, Connection connection) {

        int radius = connectionCircRadius;
        int arrowSize = connectionArrowSize;
        int lineWidth = connectionWidth;
        int argExt = connectionArgSize;
        String color = connectionColor;

        if (connection.getCircRadius() > 0)
            radius = connection.getCircRadius();
        else if (connection.getArrowSize() > 0)
            arrowSize = connection.getArrowSize();
        else if (connection.getWidth() > 0)
            lineWidth = connection.getWidth();
        else if (connection.getArgExt() > 0)
            argExt = connection.getArgExt();

        int y1_from_circ = y1 - radius;
        int y2_to_trg = y2 + arrowSize + argExt;
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(lineWidth);
        paint.setColor(Color.parseColor(color));
        paint.setStrokeCap(Paint.Cap.ROUND);

        final Path path = new Path();
        int midX = x1 + ((x2 - x1) / 2);
        int midY = y1_from_circ + ((y2_to_trg - y1_from_circ) / 2);
        float xDiff = midX - x1;
        float yDiff = midY - y1_from_circ;
        double angle = (Math.atan2(yDiff, xDiff) * (180 / Math.PI)) - 90;
        double angleRadians = Math.toRadians(angle);
        float pointX, pointY;
        if (right) {
            pointX = (float) (midX + curveRadius * Math.cos(angleRadians));
            pointY = (float) (midY + curveRadius * Math.sin(angleRadians));
        } else {
            pointX = (float) (midX - curveRadius * Math.cos(angleRadians));
            pointY = (float) (midY - curveRadius * Math.sin(angleRadians));
        }

        path.moveTo(x1, y1_from_circ);
        path.cubicTo(x1, y1_from_circ, pointX, pointY, x2, y2_to_trg);
        path.moveTo(x2, y2_to_trg);
        path.lineTo(x2, y2_to_trg - argExt);

        canvas.drawPath(path, paint);

        Paint paint2 = new Paint();
        paint2.setAntiAlias(true);
        paint2.setStyle(Paint.Style.FILL);
        paint2.setStrokeWidth(lineWidth);
        paint2.setColor(Color.parseColor(color));
        paint2.setStrokeCap(Paint.Cap.ROUND);

        Point point1 = new Point(x2 - arrowSize / 2, y2 + arrowSize);
        Point point2 = new Point(x2 + arrowSize / 2, y2 + arrowSize);
        Point point3 = new Point(x2, y2);

        Path path2 = new Path();
        path2.moveTo(x2, y2_to_trg);
        path2.lineTo(point1.x, point1.y);
        path2.lineTo(point2.x, point2.y);
        path2.lineTo(point3.x, point3.y);
        path2.lineTo(point1.x, point1.y);
        path2.close();
        canvas.drawPath(path2, paint2);

        canvas.drawCircle(x1, y1 - radius, radius, paint2);

        if (connectionTextMessage != null) {
            if (connectionTextMessage.getParent() != null)
                ((ViewGroup) connectionTextMessage.getParent()).removeView(connectionTextMessage);
            this.addView(connectionTextMessage);
            connectionTextMessage.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
            connectionTextMessage.setX(x2 - connectionTextMessage.getWidth() / 2);
            connectionTextMessage.setY(y2_to_trg);

        } else if (argExt > 0) {
            canvas.drawCircle(x2, y2_to_trg + radius, radius, paint2);
        }


    }

    private void drawCurvedArrowLeft(int x1, int y1, int x2, int y2, int curveRadius,
                                     Canvas canvas, boolean top, ConnectionTextMessage connectionTextMessage, Connection connection) {

        int radius = connectionCircRadius;
        int arrowSize = connectionArrowSize;
        int lineWidth = connectionWidth;
        int argExt = connectionArgSize;
        String color = connectionColor;

        if (connection.getCircRadius() > 0)
            radius = connection.getCircRadius();
        else if (connection.getArrowSize() > 0)
            arrowSize = connection.getArrowSize();
        else if (connection.getWidth() > 0)
            lineWidth = connection.getWidth();
        else if (connection.getArgExt() > 0)
            argExt = connection.getArgExt();

        int x1_from_circ = x1 - radius;
        int x2_to_trg = x2 + arrowSize + argExt;
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(lineWidth);
        paint.setColor(Color.parseColor(color));
        paint.setStrokeCap(Paint.Cap.ROUND);

        final Path path = new Path();
        int midX = x1_from_circ + ((x2_to_trg - x1_from_circ) / 2);
        int midY = y1 + ((y2 - y1) / 2);
        float xDiff = midX - x1_from_circ;
        float yDiff = midY - y1;
        double angle = (Math.atan2(yDiff, xDiff) * (180 / Math.PI)) - 90;
        double angleRadians = Math.toRadians(angle);
        float pointX, pointY;
        if (top) {
            pointX = (float) (midX + curveRadius * Math.cos(angleRadians));
            pointY = (float) (midY + curveRadius * Math.sin(angleRadians));
        } else {
            pointX = (float) (midX - curveRadius * Math.cos(angleRadians));
            pointY = (float) (midY - curveRadius * Math.sin(angleRadians));
        }

        path.moveTo(x1_from_circ, y1);
        path.cubicTo(x1_from_circ, y1, pointX, pointY, x2_to_trg, y2);
        path.moveTo(x2_to_trg, y2);
        path.lineTo(x2_to_trg - argExt, y2);
        path.close();

        canvas.drawPath(path, paint);

        Paint paint2 = new Paint();
        paint2.setAntiAlias(true);
        paint2.setStyle(Paint.Style.FILL);
        paint2.setStrokeWidth(lineWidth);
        paint2.setColor(Color.parseColor(color));
        paint2.setStrokeCap(Paint.Cap.ROUND);

        Point point1 = new Point(x2 + arrowSize, y2 - arrowSize / 2);
        Point point2 = new Point(x2 + arrowSize, y2 + arrowSize / 2);
        Point point3 = new Point(x2, y2);

        path.moveTo(x2_to_trg, y2);
        path.lineTo(point1.x, point1.y);
        path.lineTo(point2.x, point2.y);
        path.lineTo(point3.x, point3.y);
        path.lineTo(point1.x, point1.y);
        path.close();

        Path path2 = new Path();
        path2.moveTo(x2_to_trg, y2);
        path2.lineTo(point1.x, point1.y);
        path2.lineTo(point2.x, point2.y);
        path2.lineTo(point3.x, point3.y);
        path2.lineTo(point1.x, point1.y);
        path2.close();

        canvas.drawPath(path2, paint2);

        canvas.drawCircle(x1 - radius, y1, radius, paint2);

        if (connectionTextMessage != null) {
            if (connectionTextMessage.getParent() != null)
                ((ViewGroup) connectionTextMessage.getParent()).removeView(connectionTextMessage);
            this.addView(connectionTextMessage);
            connectionTextMessage.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
            connectionTextMessage.setX(x2_to_trg);
            connectionTextMessage.setY(y2 - connectionTextMessage.getHeight() / 2);

        } else if (argExt > 0) {
            canvas.drawCircle(x2_to_trg, y2, radius, paint2);
        }


    }

    private void drawCurvedArrowRight(int x1, int y1, int x2, int y2, int curveRadius,
                                      Canvas canvas, boolean top, ConnectionTextMessage connectionTextMessage, Connection connection) {

        int radius = connectionCircRadius;
        int arrowSize = connectionArrowSize;
        int lineWidth = connectionWidth;
        int argExt = connectionArgSize;
        String color = connectionColor;

        if (connection.getCircRadius() > 0)
            radius = connection.getCircRadius();
        else if (connection.getArrowSize() > 0)
            arrowSize = connection.getArrowSize();
        else if (connection.getWidth() > 0)
            lineWidth = connection.getWidth();
        else if (connection.getArgExt() > 0)
            argExt = connection.getArgExt();

        int x1_from_circ = x1 + radius;
        int x2_to_trg = x2 - arrowSize - argExt;
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(lineWidth);
        paint.setColor(Color.parseColor(color));
        paint.setStrokeCap(Paint.Cap.ROUND);

        final Path path = new Path();
        int midX = x1_from_circ + ((x2_to_trg - x1_from_circ) / 2);
        int midY = y1 + ((y2 - y1) / 2);
        float xDiff = midX - x1_from_circ;
        float yDiff = midY - y1;
        double angle = (Math.atan2(yDiff, xDiff) * (180 / Math.PI)) - 90;
        double angleRadians = Math.toRadians(angle);
        float pointX, pointY;
        if (top) {
            pointX = (float) (midX - curveRadius * Math.cos(angleRadians));
            pointY = (float) (midY - curveRadius * Math.sin(angleRadians));
        } else {
            pointX = (float) (midX + curveRadius * Math.cos(angleRadians));
            pointY = (float) (midY + curveRadius * Math.sin(angleRadians));
        }

        path.moveTo(x1_from_circ, y1);
        path.cubicTo(x1_from_circ, y1, pointX, pointY, x2_to_trg, y2);
        path.moveTo(x2_to_trg, y2);
        path.lineTo(x2_to_trg + argExt, y2);
        path.close();

        canvas.drawPath(path, paint);

        Paint paint2 = new Paint();
        paint2.setAntiAlias(true);
        paint2.setStyle(Paint.Style.FILL);
        paint2.setStrokeWidth(lineWidth);
        paint2.setColor(Color.parseColor(color));
        paint2.setStrokeCap(Paint.Cap.ROUND);

        Point point1 = new Point(x2 - arrowSize, y2 - arrowSize / 2);
        Point point2 = new Point(x2 - arrowSize, y2 + arrowSize / 2);
        Point point3 = new Point(x2, y2);

        path.moveTo(x2_to_trg, y2);
        path.lineTo(point1.x, point1.y);
        path.lineTo(point2.x, point2.y);
        path.lineTo(point3.x, point3.y);
        path.lineTo(point1.x, point1.y);
        path.close();

        Path path2 = new Path();
        path2.moveTo(x2_to_trg, y2);
        path2.lineTo(point1.x, point1.y);
        path2.lineTo(point2.x, point2.y);
        path2.lineTo(point3.x, point3.y);
        path2.lineTo(point1.x, point1.y);
        path2.close();

        canvas.drawPath(path2, paint2);

        canvas.drawCircle(x1 + radius, y1, radius, paint2);

        if (connectionTextMessage != null) {
            if (connectionTextMessage.getParent() != null)
                ((ViewGroup) connectionTextMessage.getParent()).removeView(connectionTextMessage);
            this.addView(connectionTextMessage);
            connectionTextMessage.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
            connectionTextMessage.setX(x2_to_trg - connectionTextMessage.getWidth());
            connectionTextMessage.setY(y2 - connectionTextMessage.getHeight() / 2);

        } else if (argExt > 0) {
            canvas.drawCircle(x2_to_trg, y2, radius, paint2);
        }


    }

    private void drawCurvedArrowBottom(int x1, int y1, int x2, int y2, int curveRadius,
                                       Canvas canvas, boolean right, ConnectionTextMessage connectionTextMessage, Connection connection) {

        int radius = connectionCircRadius;
        int arrowSize = connectionArrowSize;
        int lineWidth = connectionWidth;
        int argExt = connectionArgSize;
        String color = connectionColor;

        if (connection.getCircRadius() > 0)
            radius = connection.getCircRadius();
        else if (connection.getArrowSize() > 0)
            arrowSize = connection.getArrowSize();
        else if (connection.getWidth() > 0)
            lineWidth = connection.getWidth();
        else if (connection.getArgExt() > 0)
            argExt = connection.getArgExt();

        int y1_from_circ = y1 + radius;
        int y2_to_trg = y2 - arrowSize - argExt;
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(lineWidth);
        paint.setColor(Color.parseColor(color));
        paint.setStrokeCap(Paint.Cap.ROUND);

        final Path path = new Path();
        int midX = x1 + ((x2 - x1) / 2);
        int midY = y1_from_circ + ((y2_to_trg - y1_from_circ) / 2);
        float xDiff = midX - x1;
        float yDiff = midY - y1_from_circ;
        double angle = (Math.atan2(yDiff, xDiff) * (180 / Math.PI)) - 90;
        double angleRadians = Math.toRadians(angle);
        float pointX, pointY;
        if (right) {
            pointX = (float) (midX - curveRadius * Math.cos(angleRadians));
            pointY = (float) (midY - curveRadius * Math.sin(angleRadians));
        } else {
            pointX = (float) (midX + curveRadius * Math.cos(angleRadians));
            pointY = (float) (midY + curveRadius * Math.sin(angleRadians));
        }

        path.moveTo(x1, y1_from_circ);
        path.cubicTo(x1, y1_from_circ, pointX, pointY, x2, y2_to_trg);
        path.moveTo(x2, y2_to_trg);
        path.lineTo(x2, y2);
        path.close();
        canvas.drawPath(path, paint);

        Paint paint2 = new Paint();
        paint2.setAntiAlias(true);
        paint2.setStyle(Paint.Style.FILL);
        paint2.setStrokeWidth(lineWidth);
        paint2.setColor(Color.parseColor(color));
        paint2.setStrokeCap(Paint.Cap.ROUND);

        Point point1 = new Point(x2 - arrowSize / 2, y2 - arrowSize);
        Point point2 = new Point(x2 + arrowSize / 2, y2 - arrowSize);
        Point point3 = new Point(x2, y2);

        Path path2 = new Path();
        path2.moveTo(x2, y2_to_trg);
        path2.lineTo(point1.x, point1.y);
        path2.lineTo(point2.x, point2.y);
        path2.lineTo(point3.x, point3.y);
        path2.lineTo(point1.x, point1.y);
        path2.close();
        canvas.drawPath(path2, paint2);

        canvas.drawCircle(x1, y1 + radius, radius, paint2);

        if (connectionTextMessage != null) {
            if (connectionTextMessage.getParent() != null)
                ((ViewGroup) connectionTextMessage.getParent()).removeView(connectionTextMessage);
            this.addView(connectionTextMessage);
            connectionTextMessage.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
            connectionTextMessage.setX(x2 - connectionTextMessage.getWidth() / 2);
            connectionTextMessage.setY(y2_to_trg - connectionTextMessage.getHeight());

        } else if (argExt > 0) {
            canvas.drawCircle(x2, y2_to_trg, radius, paint2);
        }

    }

    public ArrayList<Connection> getTopItems() {
        return topItems;
    }

    public ArrayList<Connection> getBottomItems() {
        return bottomItems;
    }
}