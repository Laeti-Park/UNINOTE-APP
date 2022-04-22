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
import android.widget.Button
import android.widget.EditText
import android.widget.PopupWindow
import android.widget.Toast
import com.gyso.treeview.layout.*

import com.gyso.treeview.listener.TreeViewControlListener


class MindMapActivity : AppCompatActivity() {
    val api = APIS_login.create()

    val TAG = MainActivity::class.java.simpleName
    private lateinit var binding: ActivityMindMapBinding
    private lateinit var removeCache: Stack<NodeModel<ItemInfo>>
    private var targetNode: NodeModel<ItemInfo>? = null
    private val atomicInteger = AtomicInteger()
    private val handler = Handler()
    private var parentToRemoveChildren: NodeModel<ItemInfo>? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMindMapBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //demo init
        initWidgets()
    }

    /**
     * To use a tree view, you should do 6 steps as follows:
     * 1 customs adapter
     *
     * 2 configure layout manager. Space unit is dp.
     * You can custom you line by extends [BaseLine]
     *
     * 3 view setting
     *
     * 4 nodes data setting
     *
     * 5 if you want to edit the map, then get and use and tree view editor
     *
     * 6 you own others jobs
     */
    private fun initWidgets() {
        //1 customs adapter
        val adapter = ItemAdapter()
        //val adapter = ItemInfoTreeViewAdapter()

        //2 configure layout manager; unit dp
        val treeLayoutManager = getTreeLayoutManager()

        //3 view setting
        binding.mapView.adapter = adapter
        binding.mapView.setTreeLayoutManager(treeLayoutManager)

        //4 nodes data setting
        val root: NodeModel<ItemInfo> = NodeModel<ItemInfo>(ItemInfo("root", "root", null))
        val mapView: TreeModel<ItemInfo> = TreeModel(root)

        val grade1: NodeModel<ItemInfo> = NodeModel<ItemInfo>(ItemInfo("grade1", "1학년", null))
        val grade2: NodeModel<ItemInfo> = NodeModel<ItemInfo>(ItemInfo("grade2", "2학년", null))
        val grade3: NodeModel<ItemInfo> = NodeModel<ItemInfo>(ItemInfo("grade3", "3학년", null))
        val grade4: NodeModel<ItemInfo> = NodeModel<ItemInfo>(ItemInfo("grade4", "4학년", null))

        mapView.addNode(root, grade1, grade2, grade3, grade4)

        adapter.treeModel = mapView

        //5 get an editor. Note: an adapter must set before get an editor.
        val editor: TreeViewEditor = binding.mapView.editor

        //6 you own others jobs
        itemEvent(editor, adapter)
    }

    private fun itemEvent(editor: TreeViewEditor, adapter: ItemAdapter) {

        adapter.setOnItemListener { item, node ->
            Toast.makeText(this, "you click the head of $node $item", Toast.LENGTH_SHORT).show()
            targetNode = node
            binding.bottomNavigationView.visibility = View.VISIBLE
            binding.bottomNavigationView.run {
                setOnItemSelectedListener { item ->
                    when (item.itemId) {
                        R.id.bottomMenu1 -> {
                            setItem(editor, node)
                            true
                        }
                        R.id.bottomMenu2 -> {
                            true
                        }
                        R.id.bottomMenu3 -> {
                            true
                        }
                        else -> {
                            true
                        }
                    }
                }
            }
        }

        adapter.setOnItemLongListener { item, node ->
            editor.requestMoveNodeByDragging(true)
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
                Log.d(
                    "Debug_Tag",
                    "onDragMoveNodesHit: draging[$draggingNode]hittingNode[$hittingNode]"
                )
            }
        })
    }

    private fun setItem(editor: TreeViewEditor, node: NodeModel<ItemInfo>) {
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
                val item: NodeModel<ItemInfo> = NodeModel<ItemInfo>(ItemInfo("id", title, content))
                editor.addChildNodes(node, item)
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
/*

    fun doYourOwnJobs(editor: TreeViewEditor, adapter: ItemAdapter) {
        //drag to move node
        binding.dragEditModeRd.setOnCheckedChangeListener { v, isChecked ->
            editor.requestMoveNodeByDragging(isChecked)
        }

        //focus, means that tree view fill center in your window viewport
        binding.viewCenterBt.setOnClickListener { v -> editor.focusMidLocation() }

        //add some nodes
        binding.addNodesBt.setOnClickListener { v ->
            if (targetNode == null) {
                Toast.makeText(this, "Ohs, your targetNode is null", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val a: NodeModel<ItemInfo> =
                NodeModel<ItemInfo>(ItemInfo(R.drawable.ic_10,
                    "add-" + atomicInteger.getAndIncrement()))
            val b: NodeModel<ItemInfo> =
                NodeModel<ItemInfo>(ItemInfo(R.drawable.ic_11,
                    "add-" + atomicInteger.getAndIncrement()))
            val c: NodeModel<ItemInfo> =
                NodeModel<ItemInfo>(ItemInfo(R.drawable.ic_14,
                    "add-" + atomicInteger.getAndIncrement()))
            editor.addChildNodes(targetNode, a, b, c)


            //add to remove demo cache
            removeCache.push(targetNode)
            targetNode = b
        }

        //remove node
        binding.removeNodeBt.setOnClickListener { v ->
            if (removeCache.isEmpty()) {
                Toast.makeText(this,
                    "Ohs, demo removeCache is empty now!! Try to add some nodes firstly!!",
                    Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val toRemoveNode: NodeModel<ItemInfo>? = removeCache.pop()
            targetNode = toRemoveNode!!.getParentNode()
            editor.removeNode(toRemoveNode)
        }
        adapter.setOnItemListener { item, node ->
            val ItemInfo: ItemInfo = node.getValue()
            Toast.makeText(this, "you click the head of $ItemInfo", Toast.LENGTH_SHORT).show()
        }


        //treeView control listener
        val token = Any()
        val dismissRun = Runnable {
            binding.scalePercent.setVisibility(View.GONE)
        }
        binding.baseTreeView.setTreeViewControlListener(object : TreeViewControlListener {
            override fun onScaling(state: Int, percent: Int) {
                Log.e(TAG, "onScaling: $state  $percent")
                binding.scalePercent.setVisibility(View.VISIBLE)
                if (state == TreeViewControlListener.MAX_SCALE) {
                    binding.scalePercent.setText("MAX")
                } else if (state == TreeViewControlListener.MIN_SCALE) {
                    binding.scalePercent.setText("MIN")
                } else {
                    binding.scalePercent.setText("$percent%")
                }
                handler.removeCallbacksAndMessages(token)
                handler.postAtTime(dismissRun, token, SystemClock.uptimeMillis() + 2000)
            }

            fun onDragMoveNodesHit(
                draggingNode: NodeModel<*>,
                hittingNode: NodeModel<*>,
                draggingView: View?,
                hittingView: View?,
            ) {
                Log.e(TAG,
                    "onDragMoveNodesHit: draging[$draggingNode]hittingNode[$hittingNode]")
            }
        })
    }



    private fun setData(adapter: ItemInfoTreeViewAdapter) {
        //root
        val root: NodeModel<ItemInfo> = NodeModel<ItemInfo>(ItemInfo(R.drawable.ic_01, "-root-"))


        //child nodes
        val sub0: NodeModel<ItemInfo> = NodeModel<ItemInfo>(ItemInfo(R.drawable.ic_02, "sub00"))
        val sub1: NodeModel<ItemInfo> = NodeModel<ItemInfo>(ItemInfo(R.drawable.ic_03, "sub01"))
        val sub2: NodeModel<ItemInfo> = NodeModel<ItemInfo>(ItemInfo(R.drawable.ic_04, "sub02"))
        val sub3: NodeModel<ItemInfo> = NodeModel<ItemInfo>(ItemInfo(R.drawable.ic_05, "sub03"))
        val sub4: NodeModel<ItemInfo> = NodeModel<ItemInfo>(ItemInfo(R.drawable.ic_06, "sub04"))
        val sub5: NodeModel<ItemInfo> = NodeModel<ItemInfo>(ItemInfo(R.drawable.ic_07, "sub05"))
        val sub6: NodeModel<ItemInfo> = NodeModel<ItemInfo>(ItemInfo(R.drawable.ic_08, "sub06"))
        val sub7: NodeModel<ItemInfo> = NodeModel<ItemInfo>(ItemInfo(R.drawable.ic_09, "sub07"))
        val sub8: NodeModel<ItemInfo> = NodeModel<ItemInfo>(ItemInfo(R.drawable.ic_10, "sub08"))
        val sub9: NodeModel<ItemInfo> = NodeModel<ItemInfo>(ItemInfo(R.drawable.ic_11, "sub09"))
        val sub10: NodeModel<ItemInfo> = NodeModel<ItemInfo>(ItemInfo(R.drawable.ic_12, "sub10"))
        val sub11: NodeModel<ItemInfo> = NodeModel<ItemInfo>(ItemInfo(R.drawable.ic_13, "sub11"))
        val sub12: NodeModel<ItemInfo> = NodeModel<ItemInfo>(ItemInfo(R.drawable.ic_14, "sub12"))
        val sub13: NodeModel<ItemInfo> = NodeModel<ItemInfo>(ItemInfo(R.drawable.ic_15, "sub13"))
        val sub14: NodeModel<ItemInfo> = NodeModel<ItemInfo>(ItemInfo(R.drawable.ic_13, "sub14"))
        val sub15: NodeModel<ItemInfo> = NodeModel<ItemInfo>(ItemInfo(R.drawable.ic_14, "sub15"))
        val sub16: NodeModel<ItemInfo> = NodeModel<ItemInfo>(ItemInfo(R.drawable.ic_15, "sub16"))
        val sub17: NodeModel<ItemInfo> = NodeModel<ItemInfo>(ItemInfo(R.drawable.ic_08, "sub17"))
        val sub18: NodeModel<ItemInfo> = NodeModel<ItemInfo>(ItemInfo(R.drawable.ic_09, "sub18"))
        val sub19: NodeModel<ItemInfo> = NodeModel<ItemInfo>(ItemInfo(R.drawable.ic_10, "sub19"))
        val sub20: NodeModel<ItemInfo> = NodeModel<ItemInfo>(ItemInfo(R.drawable.ic_02, "sub20"))
        val sub21: NodeModel<ItemInfo> = NodeModel<ItemInfo>(ItemInfo(R.drawable.ic_03, "sub21"))
        val sub22: NodeModel<ItemInfo> = NodeModel<ItemInfo>(ItemInfo(R.drawable.ic_04, "sub22"))
        val sub23: NodeModel<ItemInfo> = NodeModel<ItemInfo>(ItemInfo(R.drawable.ic_05, "sub23"))
        val sub24: NodeModel<ItemInfo> = NodeModel<ItemInfo>(ItemInfo(R.drawable.ic_06, "sub24"))
        val sub25: NodeModel<ItemInfo> = NodeModel<ItemInfo>(ItemInfo(R.drawable.ic_07, "sub25"))
        val sub26: NodeModel<ItemInfo> = NodeModel<ItemInfo>(ItemInfo(R.drawable.ic_08, "sub26"))
        val sub27: NodeModel<ItemInfo> = NodeModel<ItemInfo>(ItemInfo(R.drawable.ic_09, "sub27"))
        val sub28: NodeModel<ItemInfo> = NodeModel<ItemInfo>(ItemInfo(R.drawable.ic_10, "sub28"))
        val sub29: NodeModel<ItemInfo> = NodeModel<ItemInfo>(ItemInfo(R.drawable.ic_11, "sub29"))
        val sub30: NodeModel<ItemInfo> = NodeModel<ItemInfo>(ItemInfo(R.drawable.ic_02, "sub30"))
        val sub31: NodeModel<ItemInfo> = NodeModel<ItemInfo>(ItemInfo(R.drawable.ic_03, "sub31"))
        val sub32: NodeModel<ItemInfo> = NodeModel<ItemInfo>(ItemInfo(R.drawable.ic_04, "sub32"))
        val sub33: NodeModel<ItemInfo> = NodeModel<ItemInfo>(ItemInfo(R.drawable.ic_05, "sub33"))
        val sub34: NodeModel<ItemInfo> = NodeModel<ItemInfo>(ItemInfo(R.drawable.ic_06, "sub34"))
        val sub35: NodeModel<ItemInfo> = NodeModel<ItemInfo>(ItemInfo(R.drawable.ic_07, "sub35"))
        val sub36: NodeModel<ItemInfo> = NodeModel<ItemInfo>(ItemInfo(R.drawable.ic_08, "sub36"))
        val sub37: NodeModel<ItemInfo> = NodeModel<ItemInfo>(ItemInfo(R.drawable.ic_09, "sub37"))
        val sub38: NodeModel<ItemInfo> = NodeModel<ItemInfo>(ItemInfo(R.drawable.ic_10, "sub38"))
        val sub39: NodeModel<ItemInfo> = NodeModel<ItemInfo>(ItemInfo(R.drawable.ic_11, "sub39"))
        val sub40: NodeModel<ItemInfo> = NodeModel<ItemInfo>(ItemInfo(R.drawable.ic_02, "sub40"))
        val sub41: NodeModel<ItemInfo> = NodeModel<ItemInfo>(ItemInfo(R.drawable.ic_03, "sub41"))
        val sub42: NodeModel<ItemInfo> = NodeModel<ItemInfo>(ItemInfo(R.drawable.ic_04, "sub42"))
        val sub43: NodeModel<ItemInfo> = NodeModel<ItemInfo>(ItemInfo(R.drawable.ic_05, "sub43"))
        val sub44: NodeModel<ItemInfo> = NodeModel<ItemInfo>(ItemInfo(R.drawable.ic_06, "sub44"))
        val sub45: NodeModel<ItemInfo> = NodeModel<ItemInfo>(ItemInfo(R.drawable.ic_07, "sub45"))
        val sub46: NodeModel<ItemInfo> = NodeModel<ItemInfo>(ItemInfo(R.drawable.ic_08, "sub46"))
        val sub47: NodeModel<ItemInfo> = NodeModel<ItemInfo>(ItemInfo(R.drawable.ic_09, "sub47"))
        val sub48: NodeModel<ItemInfo> = NodeModel<ItemInfo>(ItemInfo(R.drawable.ic_10, "sub48"))
        val sub49: NodeModel<ItemInfo> = NodeModel<ItemInfo>(ItemInfo(R.drawable.ic_11, "sub49"))
        val sub50: NodeModel<ItemInfo> = NodeModel<ItemInfo>(ItemInfo(R.drawable.ic_05, "sub50"))
        val sub51: NodeModel<ItemInfo> = NodeModel<ItemInfo>(ItemInfo(R.drawable.ic_07, "sub51"))
        val sub52: NodeModel<ItemInfo> = NodeModel<ItemInfo>(ItemInfo(R.drawable.ic_07, "sub52"))
        val sub53: NodeModel<ItemInfo> = NodeModel<ItemInfo>(ItemInfo(R.drawable.ic_07, "sub53"))

        //build relationship
        treeModel.addNode(root, sub0, sub1, sub3, sub4)
        treeModel.addNode(sub3, sub12, sub13)
        treeModel.addNode(sub1, sub2)
        treeModel.addNode(sub0, sub34, sub5, sub38, sub39)
        treeModel.addNode(sub4, sub6)
        treeModel.addNode(sub5, sub7, sub8)
        treeModel.addNode(sub6, sub9, sub10, sub11)
        treeModel.addNode(sub11, sub14, sub15)
        treeModel.addNode(sub10, sub40)
        treeModel.addNode(sub40, sub16)
        //treeModel.addNode(sub8,sub17,sub18,sub19,sub20,sub21,sub22,sub23,sub41,sub42,sub43,sub44);
        treeModel.addNode(sub9, sub47, sub48)
        //treeModel.addNode(sub16,sub24,sub25,sub26,sub27,sub28,sub29,sub30,sub46,sub45);
        treeModel.addNode(sub47, sub49)
        treeModel.addNode(sub12, sub37)
        treeModel.addNode(sub0, sub36)

        //treeModel.addNode(sub15,sub31,sub32,sub33,sub34,sub35,sub36,sub37);
        //treeModel.addNode(sub2,sub40,sub41,sub42,sub43,sub44,sub45,sub46);
        //mark

        //set data*/