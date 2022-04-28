package com.example.schoollifeproject

import android.graphics.*
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import com.example.schoollifeproject.databinding.ActivityMindMapBinding
import com.gyso.treeview.TreeViewEditor
import com.gyso.treeview.line.BaseLine
import com.gyso.treeview.line.StraightLine
import com.gyso.treeview.model.NodeModel
import com.gyso.treeview.model.TreeModel
import java.util.*
import java.util.concurrent.atomic.AtomicInteger
import android.os.SystemClock
import android.util.Log
import android.view.*
import android.widget.*
import androidx.core.view.isVisible
import androidx.core.view.size
import com.gyso.treeview.layout.*

import com.gyso.treeview.listener.TreeViewControlListener
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class MindMapActivity : AppCompatActivity() {
    val api = APIS_login.create()

    private lateinit var binding: ActivityMindMapBinding
    private val atomicInteger = AtomicInteger()
    private val handler = Handler()
    private var parentToRemoveChildren: NodeModel<ItemInfo>? = null
    private lateinit var userID : String
    private var itemMaxNum = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMindMapBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userID = intent.getStringExtra("ID").toString()
        Log.d("Debug_Log", "MindMapActivity/userID: ${userID}")

        //demo init
        initWidgets()
    }

    private fun initWidgets() {
        //1 customs adapter
        val adapter = ItemAdapter()

        //2 configure layout manager; unit dp
        val treeLayoutManager = getTreeLayoutManager()

        //3 view setting
        binding.mapView.adapter = adapter
        binding.mapView.setTreeLayoutManager(treeLayoutManager)

        //4 nodes data setting
        val root: NodeModel<ItemInfo> = NodeModel<ItemInfo>(ItemInfo("root", "root", null, null))
        val mapView: TreeModel<ItemInfo> = TreeModel(root)

        val grade1: NodeModel<ItemInfo> = NodeModel<ItemInfo>(ItemInfo("grade1", "1학년", null, null))
        val grade2: NodeModel<ItemInfo> = NodeModel<ItemInfo>(ItemInfo("grade2", "2학년", null, null))
        val grade3: NodeModel<ItemInfo> = NodeModel<ItemInfo>(ItemInfo("grade3", "3학년", null, null))
        val grade4: NodeModel<ItemInfo> = NodeModel<ItemInfo>(ItemInfo("grade4", "4학년", null, null))

        mapView.addNode(root, grade3, grade4, grade1, grade2)

        adapter.treeModel = mapView

        //5 get an editor. Note: an adapter must set before get an editor.
        val editor: TreeViewEditor = binding.mapView.editor
        val mapItems = HashMap<String, NodeModel<ItemInfo>>()
        api.item_load(userID).enqueue(object : Callback<List<ItemInfo>> {
            override fun onResponse(call: Call<List<ItemInfo>>, response: Response<List<ItemInfo>>) {
                Log.d("onResponse", "MindMapActivity item_load: 리스폰 성공")
                if(response.body() != null) {
                    for(i in response.body()!!) {
                        val item = NodeModel<ItemInfo>(i)
                        val childID = i.getItemID().split("_")[1]
                        mapItems.put(childID, item)
                    }

                    for(i in response.body()!!) {
                        Log.d("Debug_Log", "getItemID: ${i.getItemID()}")
                        val parentID = i.getItemID().split("_")[0]
                        val childID = i.getItemID().split("_")[1]
                        when(parentID) {
                            "grade1" -> {
                                editor.addChildNodes(grade1, mapItems.get(childID))
                            }
                            "grade2" -> {
                                editor.addChildNodes(grade2, mapItems.get(childID))
                            }
                            "grade3" -> {
                                editor.addChildNodes(grade3, mapItems.get(childID))
                            }
                            "grade4" -> {
                                editor.addChildNodes(grade4, mapItems.get(childID))
                            }
                            else -> {
                                editor.addChildNodes(mapItems.get(parentID), mapItems.get(childID))
                                Log.d("Debug_Log", "addChildNodes: $parentID $childID")
                            }
                        }
                        itemMaxNum = (if (i.getNum() != null) i.getNum()!! else throw NullPointerException("Expression 'i.getNum()' must not be null"))
                        itemMaxNum++
                        Log.d("Debug_Log", "getmapItems: ${mapItems}")
                        editor.focusMidLocation()
                    }
                }
            }

            override fun onFailure(call: Call<List<ItemInfo>>, t: Throwable) {
                Log.d("onFailure", "MindMapActivity item_load: 리스폰 실패 : $t")

            }
        })

        //6 you own others jobs
        itemEvent(editor, adapter)
    }

    private fun itemEvent(editor: TreeViewEditor, adapter: ItemAdapter) {
        editor.container.isAnimateAdd = true

        adapter.setOnItemListener { view, node ->
            val id = node.value.getItemID()
            if(id != "root") {
                val visible = id != "grade1" && id != "grade2" && id != "grade3" && id != "grade4"
                binding.bottomNavigationView.menu.findItem(R.id.bottomMenu2).isVisible = visible
                binding.bottomNavigationView.menu.findItem(R.id.bottomMenu3).isVisible = visible
                binding.bottomNavigationView.visibility = View.VISIBLE
                binding.bottomNavigationView.run {
                    setOnItemSelectedListener { item ->
                        when (item.itemId) {
                            R.id.bottomMenu1 -> {
                                val parent =
                                    if(visible) node.value.getItemID().split("_")[1]
                                    else node.value.getItemID().split("_")[0]
                                val item: NodeModel<ItemInfo> =
                                    NodeModel<ItemInfo>(ItemInfo(
                                        "${parent}_item${itemMaxNum}"
                                        , "ChildNode", "", itemMaxNum++))
                                Log.d("Debug_Log", "bottomMenu1_childNode: ${item.value.getItemID()}")
                                editor.addChildNodes(node, item)
                                saveDB(item, null, "insert")
                                binding.bottomNavigationView.isVisible = false
                                true
                            }
                            R.id.bottomMenu2 -> {
                                setItem(node, editor)
                                binding.bottomNavigationView.isVisible = false
                                true
                            }
                            R.id.bottomMenu3 -> {
                                val parentNode = node.getParentNode()
                                val parentName = node.value.getItemID().split("_")[0]
                                val parent =
                                    if(parentName != "grade1" &&
                                        parentName != "grade2" &&
                                        parentName != "grade3" &&
                                        parentName != "grade4") node.value.getItemID().split("_")[1]
                                    else node.value.getItemID().split("_")[0]
                                val children = node.getChildNodes()
                                saveDB(node, view, "delete")
                                editor.removeNode(node)
                                for (i in 0 until node.getChildNodes().size) {
                                    val child = children.pop().value
                                    val childID = child.getItemID().split("_")[1]
                                    val childNode: NodeModel<ItemInfo> =
                                        NodeModel<ItemInfo>(ItemInfo(
                                            "${parent}_$childID",
                                            "${child.getTitle()}", "${child.getContent()}", child.getNum()))
                                    saveDB(childNode, null, "update")
                                    Log.d("Debug_Log", "bottomMenu3_childNode: ${childNode.value.getItemID()}")
                                    editor.addChildNodes(parentNode, childNode)
                                }
                                binding.bottomNavigationView.isVisible = false
                                true
                            }
                            else -> {
                                true
                            }
                        }
                    }
                }
            }
        }

        adapter.setOnItemLongListener { item, node ->
            if(node.value.getItemID() != "root" &&
                node.value.getItemID() != "grade1" &&
                node.value.getItemID() != "grade2" &&
                node.value.getItemID() != "grade3" &&
                node.value.getItemID() != "grade4")
                    editor.requestMoveNodeByDragging(true)
        }

        adapter.setOnItemDoubleListener { item, node ->
            val id = node.value.getItemID()
            if(id != "root" && id != "grade1" && id != "grade2" && id != "grade3" && id != "grade4")
            editor.requestMoveNodeByDragging(false)
            setItem(node, editor)
        }

        //treeView control listener
        val token = Object()
        val dismissRun = Runnable {
            binding.scalePercent.visibility = View.GONE
        }

        binding.mapView.setTreeViewControlListener(object: TreeViewControlListener {
            override fun onScaling(state: Int, percent: Int) {
                binding.scalePercent.visibility = View.VISIBLE
                when (state) {
                    TreeViewControlListener.MAX_SCALE -> {
                        binding.scalePercent.text = "MAX"
                    }
                    TreeViewControlListener.MIN_SCALE -> {
                        binding.scalePercent.text = "MIN"
                    }
                    else -> {
                        binding.scalePercent.text = "$percent%"
                    }
                }
                handler.removeCallbacksAndMessages(token)
                handler.postAtTime(dismissRun, token, SystemClock.uptimeMillis() + 2000)
            }

            override fun onDragMoveNodesHit(
                draggingNode: NodeModel<*>?,
                hittingNode: NodeModel<*>?,
                draggingView: View?,
                hittingView: View?
            ) {
                if (draggingNode !=null && hittingNode != null) {
                    Log.d(
                        "Debug_Log",
                        "onDragMoveNodesHit: draging[${(draggingNode.value as ItemInfo).getItemID()}]" +
                                "hittingNode[${(hittingNode.value as ItemInfo).getItemID()}]"
                    )
                }
            }

            override fun onDragMoveNodesEnd(
                draggingNode: NodeModel<*>?,
                hittingNode: NodeModel<*>?,
                draggingView: View?,
                hittingView: View?
            ) {
                if (draggingNode != null && hittingNode != null) {
                    Log.d(
                        "Debug_Log",
                        "onDragMoveNodesEnd: draging[${(draggingNode.value as ItemInfo).getItemID()}]" +
                                "hittingNode[${(hittingNode.value as ItemInfo).getItemID()}]"
                    )
                    val dNode = draggingNode.value as ItemInfo
                    val hNode = hittingNode.value as ItemInfo
                    val parent =
                        if(hNode.getItemID() != "grade1" && hNode.getItemID() != "grade2" &&
                            hNode.getItemID() != "grade3" && hNode.getItemID() != "grade4")
                                hNode.getItemID().split("_")[1]
                        else hNode.getItemID().split("_")[0]
                    dNode.setItemID("${parent}_${dNode.getItemID().split("_")[1]}")
                    if (draggingView != null) {
                        saveDB(draggingNode as NodeModel<ItemInfo>, draggingView, "update")
                    }
                }
            }
        })

        binding.focusMidButton.setOnClickListener {
            editor.focusMidLocation()
        }

        binding.popularLayout.setOnClickListener {
            val visible: Boolean = binding.mapViews.visibility == View.VISIBLE &&
                    binding.mapRecommend.visibility == View.VISIBLE
            Log.d("Debug_Log", "popularLayout: $visible")
            if (!visible) {
                binding.mapViews.visibility = View.VISIBLE
                binding.mapRecommend.visibility = View.VISIBLE
            } else {
                binding.mapViews.visibility = View.GONE
                binding.mapRecommend.visibility = View.GONE
            }
        }
    }

    private fun saveDB(item: NodeModel<ItemInfo>, view: View?, mode: String) {

        val itemID = item.value.getItemID()
        val itemNum = item.value.getNum()
        val itemTitle = item.value.getTitle()
        val itemContent = item.value.getContent()
        // val itemX = "root 기준 디스턴스로 구하자"
        // val itemY = "root 기준 디스턴스로 구하자"

        if (itemNum != null) {
            api.item_save(
                itemID, userID, itemNum, itemTitle, itemContent, mode
            ).enqueue(object : Callback<PostModel> {
                override fun onResponse(call: Call<PostModel>, response: Response<PostModel>) {
                    Log.d("onResponse", "MindMapActivity item_save: 리스폰 성공")
                    if(response.body()?.error.toString() == "insert") {
                        Log.d("onResponse", "MindMapActivity item_save: 삽입 완료")
                    } else if(response.body()?.error.toString() == "update") {
                        Log.d("onResponse", "MindMapActivity item_save: 변경 완료")
                    } else if(response.body()?.error.toString() == "delete") {
                        Log.d("onResponse", "MindMapActivity item_save: 삭제 완료")
                    }
                }

                override fun onFailure(call: Call<PostModel>, t: Throwable) {
                    Log.d("onFailure", "MindMapActivity item_save: 리스폰 실패 : $t")

                }
            })
        }
    }

    private fun setItem(node: NodeModel<ItemInfo>, editor: TreeViewEditor) {
        val setWindow: View =
            LayoutInflater.from(this@MindMapActivity).inflate(R.layout.window_item_set,null)
        val itemSetWindow = PopupWindow(
            setWindow,
            ((applicationContext.resources.displayMetrics.widthPixels) * 0.8).toInt(),
            WindowManager.LayoutParams.WRAP_CONTENT
        )

        val setButton: Button = setWindow.findViewById(R.id.itemSetButton)
        val setTitle: EditText = setWindow.findViewById(R.id.setTitleView)
        val setContent: EditText = setWindow.findViewById(R.id.setContentView)

        setTitle.setText(node.value.getTitle())
        setContent.setText(node.value.getContent())

        itemSetWindow.isFocusable =true
        itemSetWindow.softInputMode = WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE
        itemSetWindow.update()
        itemSetWindow.showAtLocation(setWindow, Gravity.CENTER,0,0)

        itemSetWindow.isOutsideTouchable =true
        itemSetWindow.setTouchInterceptor {_, motionEvent ->
            if(motionEvent.action == MotionEvent.ACTION_OUTSIDE){
                itemSetWindow.dismiss()
            }
            false
        }

        setButton.setOnClickListener {
            val title = setTitle.text.toString()
            val content = setContent.text.toString()

            if (title != "" && content != "") {
                val view = editor.container.getTreeViewHolder(node).view
                node.value.setTitle(title)
                node.value.setContent(content)
                view.findViewById<TextView>(R.id.title).text = title
                saveDB(node, view, "update")
                editor.focusMidLocation()
                itemSetWindow.dismiss()
            } else {
                Toast.makeText(
                    this, "제목(내용)이 비어있습니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun getTreeLayoutManager(): TreeLayoutManager {
        val space_50dp = 30
        val space_20dp = 80
        val line = getLine()
        //return new RightTreeLayoutManager(this,space_50dp,space_20dp,line);
        //return new LeftTreeLayoutManager(this,space_50dp,space_20dp,line);
        //return new CompactRightTreeLayoutManager(this,space_50dp,space_20dp,line);
        //return CompactLeftTreeLayoutManager(this,space_50dp,space_20dp,line);
        return HorizonLeftAndRightLayoutManager(this,space_50dp,space_20dp,line);
        //return CompactHorizonLeftAndRightLayoutManager(this,space_50dp,space_20dp,line);
        //return new DownTreeLayoutManager(this,space_50dp,space_20dp,line);

        //return UpTreeLayoutManager(this, space_50dp, space_20dp, line)
        //return CompactDownTreeLayoutManager(this, space_50dp, space_20dp, line)
        //return new CompactUpTreeLayoutManager(this,space_50dp,space_20dp,line);
        //return new CompactVerticalUpAndDownLayoutManager(this,space_50dp,space_20dp,line);
        //return CompactVerticalUpAndDownLayoutManager(this, space_50dp, space_20dp, line)
        //return VerticalUpAndDownLayoutManager(this,space_50dp,space_20dp,line);
        //return CompactRingTreeLayoutManager(this,space_50dp,space_20dp,line);
        //return new ForceDirectedTreeLayoutManager(this,line);
    }

    private fun getLine(): BaseLine {
        //return new SmoothLine();
        return StraightLine(Color.parseColor("#055287"), 2)
        //return new PointedLine();
        //return new DashLine(Color.parseColor("#F1286C"),3);
        //return new AngledLine();
    }
}