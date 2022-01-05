package com.example.schoollifeproject

import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.PopupMenu
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.schoollifeproject.databinding.ActivityMindMapBinding
import me.jagar.mindmappingandroidlibrary.Views.Item
import me.jagar.mindmappingandroidlibrary.Views.ItemLocation
import me.jagar.mindmappingandroidlibrary.Views.MindMappingView

class MindMapActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMindMapBinding
    private lateinit var mindMappingView: MindMappingView
    private lateinit var rootNode: Item
    private var childNode = ArrayList<Item>()
    private var childNodeNum = 0
    var nodeLocation = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMindMapBinding.inflate(layoutInflater)
        setContentView(binding.root)
        mindMappingView = binding.mindMappingView
        addRoot()
        nodeEvent()
    }

    private fun addRoot() {
        rootNode = Item(this, "Root", "Hello", true)
        mindMappingView.addCentralItem(rootNode, false)

        val freshman = Item(this, "1st", "Hello", true)
        val sophomore = Item(this, "2nd", "Hello", true)
        val junior = Item(this, "3rd", "Hello", true)
        val senior = Item(this, "4st", "Hello", true)

        mindMappingView.addItem(
            freshman, rootNode, 200, 15, ItemLocation.TOP, true, null)
        mindMappingView.addItem(
            sophomore, rootNode, 200, 15, ItemLocation.LEFT, true, null)
        mindMappingView.addItem(
            junior, rootNode, 200, 15, ItemLocation.RIGHT, true, null)
        mindMappingView.addItem(
            senior, rootNode, 200, 15, ItemLocation.BOTTOM, true, null)
    }

    private fun nodeEvent() {
        rootNode.setOnClickListener {
            bottomEvent(rootNode, true)
            popupEventR(rootNode)
        }
        mindMappingView.setOnItemClicked { item ->
            bottomEvent(item, false)
            popupEventR(item)
        }

    }

    private fun bottomEvent(node: Item, root: Boolean) {
        binding.bottomNavigationView.visibility = View.VISIBLE
        binding.bottomNavigationView.setOnNavigationItemSelectedListener {
            if (root) {
                when (it.itemId) {
                    R.id.childNodeAdd -> {
                        childNode!!.add(Item(this@MindMapActivity, "Child", "Hi", true))
                        mindMappingView!!.addItem(
                            childNode!![childNodeNum],
                            node,
                            150,
                            20,
                            setLocation(),
                            true,
                            null
                        )
                        childNode!![childNodeNum].x -= rootNode.x
                        if (setLocation() == 0 || setLocation() == 3) {
                            childNode!![childNodeNum].y -= rootNode.y
                        } else {
                            childNode!![childNodeNum].y = 0F
                        }
                        childNodeNum++
                        nodeLocation++
                        Log.d(
                            "XYZ",
                            "Child${childNode!![childNodeNum - 1].x} ${childNode!![childNodeNum - 1].y}"
                        )
                        binding.bottomNavigationView.visibility = View.INVISIBLE
                    }
                }
            } else {

            }
            true
        }
    }

    private fun setLocation(): Int {
        Toast.makeText(this, "$nodeLocation", Toast.LENGTH_SHORT).show()
        return if (nodeLocation in 1..3) {
            nodeLocation
        } else {
            nodeLocation = 0
            nodeLocation
        }
    }

    private fun popupEventR(node: Item) {
        val popUp = PopupMenu(node!!.context, node) //v는 클릭된 뷰를 의미
        popUp.menuInflater.inflate(R.menu.popup_menu, popUp.menu)
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
        val popUp = PopupMenu(node!!.context, node) //v는 클릭된 뷰를 의미
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