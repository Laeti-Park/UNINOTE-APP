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
import androidx.core.view.GestureDetectorCompat
import com.example.schoollifeproject.databinding.ActivityMindMapBinding
import me.jagar.mindmappingandroidlibrary.Views.Item
import me.jagar.mindmappingandroidlibrary.Views.ItemLocation
import me.jagar.mindmappingandroidlibrary.Views.MindMappingView
import me.jagar.mindmappingandroidlibrary.Zoom.ZoomLayout
import java.util.*
import kotlin.collections.ArrayList

// 뷰 충돌 이벤트? + 삭제 기능 구현 + onefingerscroll

class MindMapActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMindMapBinding
    private lateinit var mindMappingView: MindMappingView
    private lateinit var rootNode: Item
    private lateinit var freshman: Item
    private lateinit var sophomore: Item
    private lateinit var junior: Item
    private lateinit var senior: Item
    private lateinit var zoomLayout: ZoomLayout
    private var childNode = ArrayList<Item>()
    private var childInfo = HashMap<Item, Int>()
    private var childNodeNum = 0
    private val MAX_DURATION = 500

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
    }

    private fun addRoot() {
        rootNode = Item(this, "Root", "Hello", true)
        mindMappingView.addCentralItem(rootNode, false)

        freshman = Item(this, "1st", "Hello", true)
        sophomore = Item(this, "2nd", "Hello", true)
        junior = Item(this, "3rd", "Hello", true)
        senior = Item(this, "4st", "Hello", true)

        mindMappingView.addItem(
            freshman, rootNode, 200, 15,
            ItemLocation.TOP, false, null
        )
        mindMappingView.addItem(
            sophomore, rootNode, 200, 15,
            ItemLocation.LEFT, false, null
        )
        mindMappingView.addItem(
            junior, rootNode, 200, 15,
            ItemLocation.RIGHT, false, null
        )
        mindMappingView.addItem(
            senior, rootNode, 200, 15,
            ItemLocation.BOTTOM, false, null
        )

        // 기본 생성 노드 클릭 이벤트
        rootNode.setOnTouchListener(nodeRootEvent(rootNode))
        freshman.setOnTouchListener(nodeParentEvent(freshman, ItemLocation.TOP))
        sophomore.setOnTouchListener(nodeParentEvent(sophomore, ItemLocation.LEFT))
        junior.setOnTouchListener(nodeParentEvent(junior, ItemLocation.RIGHT))
        senior.setOnTouchListener(nodeParentEvent(senior, ItemLocation.BOTTOM))
    }

    var startTime = 0L
    lateinit var itemA : Item
    lateinit var itemB : Item

    private fun nodeRootEvent(item: Item): View.OnTouchListener? {
        var clickCount = 0 // 클릭 카운트가 뷰 하나에만 적용되게

        return View.OnTouchListener { _, motionEvent ->
            when (motionEvent.action) {
                MotionEvent.ACTION_DOWN -> {
                    ++clickCount
                }
                MotionEvent.ACTION_UP -> {
                    if (clickCount == 1) {
                        itemA = item
                        startTime = System.currentTimeMillis()
                    } else if (clickCount == 2) {
                        itemB = item
                        val duration = System.currentTimeMillis() - startTime
                        Log.d("checkTouch", "$clickCount $startTime $duration")
                        Log.d("checkTouchItem", "$itemA $itemB")

                        if (itemA == itemB && duration <= MAX_DURATION) {
                            popupEventR(item)
                        }
                        clickCount = 0
                    } else {
                        clickCount = 0
                    }
                }
            }
            true
        }
        false
    }


    private fun nodeParentEvent(item: Item, position: Int): View.OnTouchListener? {
        var clickCount = 0 // 클릭 카운트가 뷰 하나에만 적용되게

        return View.OnTouchListener { _, motionEvent ->
            when (motionEvent.action) {
                MotionEvent.ACTION_DOWN -> {
                    ++clickCount
                }
                MotionEvent.ACTION_UP -> {
                    if(clickCount == 1) {
                        itemA = item
                        startTime = System.currentTimeMillis()
                        bottomEvent(item, position)
                    } else if(clickCount == 2) {
                        itemB = item
                        val duration = System.currentTimeMillis() - startTime
                        Log.d("checkTouch", "$clickCount $startTime $duration")
                        Log.d("checkTouchItem", "$itemA $itemB")

                        if(itemA == itemB && duration <= MAX_DURATION) {
                            popupEventR(item)
                        }
                        clickCount = 0
                    } else {
                        clickCount = 0
                    }
                }
            }
            true
        }
    }

    private fun nodeChildEvent(item: Item): View.OnTouchListener? {
        var longClick : Boolean
        var clickCount = 0 // 클릭 카운트가 뷰 하나에만 적용되게

        return View.OnTouchListener { _, motionEvent ->
            when (motionEvent.action) {
                MotionEvent.ACTION_DOWN -> {
                    longClick = false
                    Log.d("checkDown", "${item.javaClass.name}")
                    item.setOnLongClickListener {
                        longClick = true

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
                        if (longClick) {
                            mindMappingView.setOnDragListener { view, dragEvent ->
                                Log.d("checkDrag", "${item.javaClass.name}")
                                when (dragEvent.action) {
                                    DragEvent.ACTION_DRAG_STARTED -> {
                                        Log.d("checkDragStart", "${item.javaClass.name}")
                                    }
                                    DragEvent.ACTION_DROP -> { // 드롭 기능 구현해보자
                                        Log.d("checkDrop", "${item.javaClass.name} ${item.title.text}")
                                        if(view == junior) {
                                            Log.d("checkDrop", "YOOOOOOO")
                                        }
                                    }
                                }
                                true
                            }
                        } else {
                            ++clickCount
                        }
                        true
                    }
                }
                MotionEvent.ACTION_UP -> {
                    if(clickCount == 1) {
                        itemA = item
                        startTime = System.currentTimeMillis()
                    } else if(clickCount == 2) {
                        itemB = item
                        val duration = System.currentTimeMillis() - startTime
                        Log.d("checkTouch", "$clickCount $startTime $duration")
                        Log.d("checkTouchItem", "$itemA $itemB")

                        if(itemA == itemB && duration <= MAX_DURATION) {
                            popupEventR(item)
                        }
                        clickCount = 0
                    } else {
                        clickCount = 0
                    }
                }
            }
            false
        }
    }

    private fun bottomEvent(node: Item, position: Int) {
        binding.bottomNavigationView.visibility = View.VISIBLE
        binding.bottomNavigationView.run {
            setOnItemSelectedListener { item ->
                when (item.itemId) {
                    R.id.childNodeAdd -> {
                        childNode.add(
                            Item(
                                this@MindMapActivity,
                                "Child",
                                "Hi",
                                true
                            )
                        )
                        childInfo[childNode[childNodeNum]] = position
                        mindMappingView.addItem(
                            childNode[childNodeNum],
                            node,
                            150,
                            20,
                            position,
                            true,
                            null
                        )
                        val tempItem = childNode[childNodeNum]
                        childNode[childNodeNum].setOnTouchListener(nodeChildEvent(tempItem))
                        nodePosition(node, position)
                        binding.bottomNavigationView.visibility = View.INVISIBLE
                        true
                    }
                    else -> {
                        true
                    }
                }
            }
        }
    }

    private fun nodePosition(parent: Item, position: Int) {
        childNode[childNodeNum].x -= rootNode.x
        if (position == 1 || position == 2) {
            when {
                parent.leftChildItems.size > 1 -> {
                    childNode[childNodeNum].y -= rootNode.y
                }
                parent.rightChildItems.size > 1 -> {
                    childNode[childNodeNum].y -= rootNode.y
                }
            }
        } else {
            childNode[childNodeNum].y -= rootNode.y
        }
        childNodeNum++
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
                    deleteNode(node)
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

    private fun deleteNode(node: Item) {
        node.removeAllViews()
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

        // Set the touch point's position to be in the middle of the drag shadow.
        touch.set(width / 2, height / 2)
    }

    // Defines a callback that draws the drag shadow in a Canvas that the system
    // constructs from the dimensions passed to onProvideShadowMetrics().
    override fun onDrawShadow(canvas: Canvas) {

        // Draw the ColorDrawable on the Canvas passed in from the system.
        shadow.draw(canvas)
    }
}
