package com.example.schoollifeproject

import android.content.ClipData
import android.content.ClipDescription
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Point
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.PopupMenu
import androidx.appcompat.app.AppCompatActivity
import com.example.schoollifeproject.databinding.ActivityMindMapBinding
import me.jagar.mindmappingandroidlibrary.Views.Connection
import me.jagar.mindmappingandroidlibrary.Views.Item
import me.jagar.mindmappingandroidlibrary.Views.MindMappingView
import me.jagar.mindmappingandroidlibrary.Zoom.ZoomLayout
import java.util.*
import kotlin.collections.ArrayList

// itemId 설정 / parent 아이디 / item 아이디 따로 표기

class MindMapActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMindMapBinding
    private lateinit var mindMappingView: MindMappingView
    private lateinit var rootNode: Item
    private lateinit var grade1: Item
    private lateinit var grade2: Item
    private lateinit var grade3: Item
    private lateinit var grade4: Item
    private lateinit var mItem : Item

    private lateinit var zoomLayout: ZoomLayout

    private var childNode = ArrayList<Item>()

    private var childNodeNum = ArrayList<Int>()
    private var childNodeNumMax : Int = 0

    private lateinit var detector : GestureDetector

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMindMapBinding.inflate(layoutInflater)
        setContentView(binding.root)
        mindMappingView = binding.mindMappingView
        zoomLayout = binding.zoomLayout

        val displaymetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displaymetrics)
        val screenWidth = displaymetrics.widthPixels
        val screenHeight = displaymetrics.heightPixels

        mindMappingView.layoutParams.width = (screenWidth * 2.5).toInt()
        mindMappingView.layoutParams.height = (screenHeight * 2.5).toInt()

        addRoot()

        detector = GestureDetector(this, object : GestureDetector.OnGestureListener, GestureDetector.OnDoubleTapListener {

                override fun onDown(event: MotionEvent): Boolean {
                    Log.d("DEBUG_TAG", "onDown")
                    return true
                }

                override fun onFling(
                    event1: MotionEvent,
                    event2: MotionEvent,
                    velocityX: Float,
                    velocityY: Float,
                ): Boolean {
                    Log.d("DEBUG_TAG", "onFling: $event1 $event2")
                    return true
                }

                override fun onLongPress(event: MotionEvent) {
                    Log.d("DEBUG_TAG", "onLongPress: $event")
                    // 롱 클릭 시 드래그 이벤트
                    if(mItem != rootNode && mItem != grade1 && mItem != grade2 && mItem != grade3 && mItem != grade4){
                        nodeDragEvent(mItem)
                    }
                }

                override fun onScroll(
                    event1: MotionEvent,
                    event2: MotionEvent,
                    distanceX: Float,
                    distanceY: Float,
                ): Boolean {
                    Log.d("DEBUG_TAG", "onScroll: $event1 $event2")
                    return true
                }

                override fun onShowPress(event: MotionEvent) {
                    Log.d("DEBUG_TAG", "onShowPress: $event")
                }

                override fun onSingleTapUp(event: MotionEvent): Boolean {
                    Log.d("DEBUG_TAG", "onSingleTapUp: $event")
                    return true
                }

                override fun onDoubleTap(event: MotionEvent): Boolean {
                    Log.d("DEBUG_TAG", "onDoubleTap: $event")
                    // 더블 클릭 시 팝업 메뉴
                    if(mItem == rootNode || mItem == grade1 ||
                        mItem == grade2 || mItem == grade3 || mItem == grade4){
                        popupEventR(mItem)
                    } else {
                        popupEvent(mItem)
                    }
                    return true
                }

                override fun onDoubleTapEvent(event: MotionEvent): Boolean {
                    Log.d("DEBUG_TAG", "onDoubleTapEvent: $event")
                    return true
                }

                override fun onSingleTapConfirmed(event: MotionEvent): Boolean {
                    Log.d("DEBUG_TAG", "onSingleTapConfirmed: ${mItem.itemId}")
                    Log.d("DEBUG_TAG", "grade2XY: ${grade1.x} ${grade1.y}")
                    // 한 번 클릭 시 하단 메뉴
                    bottomEvent(mItem)
                    return true
                }
            })
    }

    private fun addRoot() {
        rootNode = Item(this, "Root", "Root", true)
        mindMappingView.addCentralItem(rootNode, false)
        rootNode.setOnTouchListener{ _, motionEvent ->
            mItem = rootNode
            detector.onTouchEvent(motionEvent)
            true
        }

        grade1 = Item(this, "1학년", "grade1", true)
        grade2 = Item(this, "2학년", "grade2", true)
        grade3 = Item(this, "3학년", "grade3", true)
        grade4 = Item(this, "4학년", "grade4", true)

        mindMappingView.addItem(
            grade1, rootNode, 200, 15, 0, false, null)
        mindMappingView.addItem(
            grade2, rootNode, 200, 15, 1, false, null)
        Log.d("DEBUG_TAG", "grade2XY: ${grade1.x} ${grade1.y}")
        mindMappingView.addItem(
            grade3, rootNode, 200, 15, 2, false, null)
        mindMappingView.addItem(
            grade4, rootNode, 200, 15, 3, false, null)

        grade1.setOnTouchListener{ _, motionEvent ->
            mItem = grade1
            detector.onTouchEvent(motionEvent)
            true
        }
        grade2.setOnTouchListener{ _, motionEvent ->
            mItem = grade2
            detector.onTouchEvent(motionEvent)
            true
        }
        grade3.setOnTouchListener{ _, motionEvent ->
            mItem = grade3
            detector.onTouchEvent(motionEvent)
            true
        }
        grade4.setOnTouchListener{ _, motionEvent ->
            mItem = grade4
            detector.onTouchEvent(motionEvent)
            true
        }
    }

    private fun nodeDragEvent(item: Item) {
        Log.d("checkLongClick", "${item.javaClass.name}")
        val dragItem = ClipData.Item(item.tag as? CharSequence)
        val dragData = ClipData(
            item.tag as? CharSequence,
            arrayOf(ClipDescription.MIMETYPE_TEXT_INTENT),
            dragItem
        )
        val myShadow = MyDragShadowBuilder(item)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            item.startDragAndDrop(
                dragData,
                myShadow,
                null, 0
            )
        }
        mindMappingView.setOnDragListener { view, dragEvent ->
            Log.d("checkDrag", "${item.javaClass.name}")
            when (dragEvent.action) {
                DragEvent.ACTION_DRAG_STARTED -> {
                    Log.d("checkDragStart", "${item.javaClass.name}")
                }
                DragEvent.ACTION_DROP -> { // 드롭 기능 구현해보자
                    val map = view as MindMappingView
                    for (index in 0 until map.childCount) {
                        val i = (map.getChildAt(index) as Item)

                        val x = dragEvent.x
                        val y = dragEvent.y

                        if ((x>i.x - i.width && x<i.x + i.width) && (y>i.y - i.height && y<i.y + i.height)) {
                            if(index == 1 && item.location != 0) {
                                val it = Item(this@MindMapActivity, "${item.title}","d",true)

                                Log.d("DEBUG_TAG", "${it.title.text} ${it.itemId}")
                                mindMappingView.deleteItem(item)
                                addItem(grade1, it, true)
                            }
                        }
                    }
                }
            }
            true
        }
    }

    private fun addItem(parent: Item, child: Item, drag : Boolean) { // mindMappingView 아이템 추가
        childNode.add(child)
        mindMappingView.addItem(child, parent, 150,
            20, parent.location, true, null)
        child.setOnTouchListener{ _, motionEvent ->
            mItem = child
            detector.onTouchEvent(motionEvent)
            true
        }
        Log.d("node4", "${mindMappingView.bottomItems.size}")
        if (!drag) {
            nodeLocation(parent, child) // item 위치 설정
        }
        binding.bottomNavigationView.visibility = View.INVISIBLE
    }

    private fun bottomEvent(node: Item) {

        binding.bottomNavigationView.visibility = View.VISIBLE
        binding.bottomNavigationView.run {
            setOnItemSelectedListener { item ->
                when (item.itemId) {
                    R.id.childNodeAdd -> {
                        childNodeNum.add(childNodeNumMax)
                        val child = Item(this@MindMapActivity, "Child",
                            "${node.itemId}_item${childNodeNum[childNodeNumMax++]}", true)
                        addItem(node, child, false)
                        true
                    }
                    else -> {
                        true
                    }
                }
            }
        }
    }

    private fun nodeLocation(parent: Item, child: Item) { // item location 재설정
        child.x -= rootNode.x
        if (parent.location == 1 || parent.location == 2) {
            when {
                parent.leftChildItems.size > 1 -> {
                    child.y -= rootNode.y
                }
                parent.rightChildItems.size > 1 -> {
                    child.y -= rootNode.y
                }
            }
        } else {
            child.y -= rootNode.y
        }
    }

    private fun popupEventR(node: Item) {
        val popUp = PopupMenu(node.context, node) //v는 클릭된 뷰를 의미
        popUp.menuInflater.inflate(R.menu.popup_menu_root, popUp.menu)
        popUp.setOnMenuItemClickListener { items ->
            when (items.itemId) {
                R.id.popupMenuR1 -> { // 노드 내용 변경, Edit 버튼
                    editNode(node)
                }
                R.id.popupMenuR2 -> {
                }
                R.id.popupMenuR3 -> {
                }
                else -> {
                }
            }
            false
        }
        popUp.show() //Popup Menu 보이기

    }

    private fun popupEvent(node: Item) {
        val popUp = PopupMenu(node.context, node) //v는 클릭된 뷰를 의미
        popUp.menuInflater.inflate(R.menu.popup_menu, popUp.menu)
        popUp.setOnMenuItemClickListener { items ->
            when (items.itemId) {
                R.id.popupMenuR1 -> { // 노드 내용 변경, Edit 버튼
                    editNode(node)
                }
                R.id.popupMenu2 -> { // 노드 내용 삭제, Delete 버튼
                    // 마인드맵 아이템 삭제
                    mindMappingView.deleteItem(node)
                }
                R.id.popupMenuR2 -> {
                }
                R.id.popupMenuR3 -> {
                }
                else -> {
                }
            }
            false
        }
        popUp.show() //Popup Menu 보이기
    }

    private fun editNode(node: Item) {
        binding.editNode.setText(node.title.text)
        binding.editNode.visibility = View.VISIBLE
        binding.editNode.setOnKeyListener(object : View.OnKeyListener {
            override fun onKey(v: View?, keyCode: Int, event: KeyEvent): Boolean {
                //Enter key Action
                if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                    //키패드 내리기
                    val imm: InputMethodManager =
                        getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(
                        binding.editNode.windowToken, 0
                    )
                    //처리
                    node.title.text = binding.editNode.text
                    binding.editNode.visibility = View.INVISIBLE
                    return true
                }
                return false
            }
        })
    }
}

private class MyDragShadowBuilder(v: View) : View.DragShadowBuilder(v) {

    private val shadow = ColorDrawable(Color.LTGRAY)

    // Defines a callback that sends the drag shadow dimensions and touch point
    // back to the system.
    override fun onProvideShadowMetrics(size: Point, touch: Point) {

        // Set the width of the shadow to half the width of the original View.
        val width: Int = view.width

        // Set the height of the shadow to half the height of the original View.
        val height: Int = view.height

        // The drag shadow is a ColorDrawable. This sets its dimensions to be the
        // same as the Canvas that the system provides. As a result, the drag shadow
        // fills the Canvas.
        shadow.setBounds(0, 0, width, height)

        // Set the size parameter's width and height values. These get back to
        // the system through the size parameter.
        size.set(width, height)

        // Set the touch point's location to be in the middle of the drag shadow.
        touch.set(width / 2, height / 2)
    }

    // Defines a callback that draws the drag shadow in a Canvas that the system
    // constructs from the dimensions passed to onProvideShadowMetrics().
    override fun onDrawShadow(canvas: Canvas) {

        // Draw the ColorDrawable on the Canvas passed in from the system.
        shadow.draw(canvas)
    }
}
