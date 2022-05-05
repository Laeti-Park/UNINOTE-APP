package com.example.schoollifeproject.fragment

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.SystemClock
import android.util.Log
import android.view.*
import android.widget.*
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.example.schoollifeproject.model.APIS
import com.example.schoollifeproject.model.ItemInfo
import com.example.schoollifeproject.model.PostModel
import com.example.schoollifeproject.R
import com.example.schoollifeproject.adapter.ItemAdapter
import com.example.schoollifeproject.databinding.FragmentMindMapBinding
import com.gyso.treeview.TreeViewEditor
import com.gyso.treeview.layout.CompactHorizonLeftAndRightLayoutManager
import com.gyso.treeview.layout.TreeLayoutManager
import com.gyso.treeview.line.BaseLine
import com.gyso.treeview.line.StraightLine
import com.gyso.treeview.listener.TreeViewControlListener
import com.gyso.treeview.model.NodeModel
import com.gyso.treeview.model.TreeModel

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MindMapFragment : Fragment() {
    val api = APIS.create()
    val adapter = ItemAdapter()

    private lateinit var binding: FragmentMindMapBinding
    private val handler = Handler()
    var mapContext: Context? = null

    private lateinit var userID : String
    private lateinit var mapID : String
    private var itemMaxNum = 0

    private var mapHit = 0
    private var mapRecommend = 0

    private var mapPublic = true
    private lateinit var mapPassword: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        super.onCreate(savedInstanceState)
        binding = FragmentMindMapBinding.inflate(inflater, container,false)
        mapContext = context

        userID = arguments?.getString("ID").toString() // menuActivity를 통해 받은 userID
        mapID = arguments?.getString("mapID").toString() // menuActivity를 통해 받은 userID
        Log.d("Debug_Log", "MindMapActivity/userID: ${userID}")

        initWidgets()
        return binding.root
    }

    private fun initWidgets() {
        val treeLayoutManager = getTreeLayoutManager()

        binding.mapView.adapter = adapter
        binding.mapView.setTreeLayoutManager(treeLayoutManager)

        // 마인드맵 기본 구성
        val root: NodeModel<ItemInfo> = NodeModel<ItemInfo>(ItemInfo("root", "root", null, null))
        val mapView: TreeModel<ItemInfo> = TreeModel(root)

        val grade1: NodeModel<ItemInfo> = NodeModel<ItemInfo>(ItemInfo("grade1", "1학년", null, null))
        val grade2: NodeModel<ItemInfo> = NodeModel<ItemInfo>(ItemInfo("grade2", "2학년", null, null))
        val grade3: NodeModel<ItemInfo> = NodeModel<ItemInfo>(ItemInfo("grade3", "3학년", null, null))
        val grade4: NodeModel<ItemInfo> = NodeModel<ItemInfo>(ItemInfo("grade4", "4학년", null, null))
        mapView.addNode(root, grade3, grade4, grade1, grade2)
        adapter.treeModel = mapView

        // 마인드맵 추가/제거 관련 객체
        val editor: TreeViewEditor = binding.mapView.editor

        // DB에서 마인드맵 노드 불러오기
        api.item_load(userID).enqueue(object : Callback<List<ItemInfo>> {
            override fun onResponse(call: Call<List<ItemInfo>>, response: Response<List<ItemInfo>>) {
                val mapItems = HashMap<String, NodeModel<ItemInfo>>()
                Log.d("onResponse", "MindMapActivity item_load: 리스폰 성공")
                if(response.body() != null) {

                    for(i in response.body()!!) {
                        val item = NodeModel<ItemInfo>(i)
                        val childID = i.getItemID().split("_")[1]
                        mapItems[childID] = item
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

        // DB에서 마인드맵 공개여부 확인
        api.map_public(userID).enqueue(object : Callback<PostModel> {
            override fun onResponse(call: Call<PostModel>, response: Response<PostModel>) {
                Log.d("onResponse", "MindMapActivity map_public: 리스폰 성공 ${response.body()?.error.toString()}")
                if(response.body()?.error.toString() == "failed") {
                    mapPublic = true
                } else {
                    if (response.body()?.public == 0) {
                        mapPassword = response.body()?.mapPassword.toString()
                        binding.publicButton.setImageResource(R.drawable.ic_mindmap_private)
                        mapPublic = false
                    } else {
                        mapPublic = true
                    }
                }
            }

            override fun onFailure(call: Call<PostModel>, t: Throwable) {
                Log.d("onFailure", "MindMapActivity map_public: 리스폰 실패 : $t")
            }
        })

        // DB에서 조회수/추천수 불러오기
        api.map_popular(userID).enqueue(object : Callback<PostModel> {
            override fun onResponse(call: Call<PostModel>, response: Response<PostModel>) {
                Log.d("onResponse", "MindMapActivity map_popular: 리스폰 성공 ${response.body()?.error.toString()}")
                if (response.body()?.error.toString() == "ok") {
                    mapHit = response.body()?.mapHit!!+1
                    mapRecommend = response.body()?.mapRecommend!!

                    binding.mapHit.text = mapHit.toString()
                    binding.mapRecommend.text = mapRecommend.toString()
                } else {
                    mapHit = 0
                    mapRecommend = 0

                    binding.mapHit.text = mapHit.toString()
                    binding.mapRecommend.text = mapRecommend.toString()
                }
            }

            override fun onFailure(call: Call<PostModel>, t: Throwable) {
                Log.d("onFailure", "MindMapActivity map_popular: 리스폰 실패 : $t")
            }
        })

        itemEvent(editor, adapter)
    }

    // DB에서 마인드맵 노드 삽입/삭제/변경
    private fun saveDB(item: NodeModel<ItemInfo>, view: View?, mode: String) {

        // app에서 만든 경우
        val itemID = item.value.getItemID()
        val itemTop = "New"
        val itemLeft = "New"
        val itemContent = item.value.getContent()
        val itemCount = item.value.getNum()
        var itemWidth = "150px"
        var itemHeight =  "50px"
        val itemNote = item.value.getNote()
        if (view != null) {
            view.addOnLayoutChangeListener { _, i, i2, i3, i4, _, _, _, _ ->
                itemWidth = "${i3-i}px"
                itemHeight = "${i4-i2}px"
            }
        }
        Log.d("onResponse", "MindMapActivity item_save: $mode")
        if (itemCount != null) {
            api.item_save(
                itemID,
                itemTop,
                itemLeft,
                userID,
                itemContent,
                itemCount,
                itemWidth,
                itemHeight,
                itemNote,
                mode
            ).enqueue(object : Callback<PostModel> {
                override fun onResponse(call: Call<PostModel>, response: Response<PostModel>) {
                    Log.d("onResponse", "MindMapActivity item_save: 리스폰 성공")
                    if (response.body()?.error.toString() == "insert") {
                        Log.d("onResponse", "MindMapActivity item_save: 삽입 완료")
                    } else if (response.body()?.error.toString() == "update") {
                        Log.d("onResponse", "MindMapActivity item_save: 변경 완료")
                    } else if (response.body()?.error.toString() == "delete") {
                        Log.d("onResponse", "MindMapActivity item_save: 삭제 완료")
                    }
                }

                override fun onFailure(call: Call<PostModel>, t: Throwable) {
                    Log.d("onFailure", "MindMapActivity item_save: 리스폰 실패 : $t")

                }
            })
        }
    }

    private fun itemEvent(editor: TreeViewEditor, adapter: ItemAdapter) {

        editor.container.isAnimateAdd = true

        adapter.setOnItemListener { view, node ->
            Log.d("Debug_Log", "setOnItemListener: ${node.getValue().toString()}")
            val id = node.value.getItemID()
            if (id != "root") {
                val visible = id != "grade1" && id != "grade2" && id != "grade3" && id != "grade4"
                binding.bottomNavigationView.menu.findItem(R.id.bottomMenu2).isVisible = visible
                binding.bottomNavigationView.menu.findItem(R.id.bottomMenu3).isVisible = visible
                binding.bottomNavigationView.menu.findItem(R.id.bottomMenu4).isVisible = visible
                binding.bottomNavigationView.visibility = View.VISIBLE
                binding.bottomNavigationView.run {
                    setOnItemSelectedListener { item ->
                        when (item.itemId) {
                            R.id.bottomMenu1 -> {
                                val parent =
                                    if (visible) node.value.getItemID().split("_")[1]
                                    else node.value.getItemID().split("_")[0]
                                val item: NodeModel<ItemInfo> =
                                    NodeModel<ItemInfo>(
                                        ItemInfo(
                                            "${parent}_item${itemMaxNum}",
                                            "ChildNode",
                                            itemMaxNum++,
                                            ""
                                        )
                                    )
                                Log.d(
                                    "Debug_Log",
                                    "bottomMenu1_childNode: ${item.value.getItemID()}"
                                )
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
                                val children = node.getChildNodes()

                                for (i in children) {
                                    Log.d(
                                        "Debug_Log",
                                        "bottomMenu3_childNode: ${i.value.getItemID()}"
                                    )
                                    saveDB(i, null, "delete")
                                }

                                Log.d(
                                    "Debug_Log",
                                    "bottomMenu3_childNode: ${node.value.getItemID()}"
                                )
                                saveDB(node, view, "delete")
                                editor.removeNode(node)

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
            if (node.value.getItemID() != "root" &&
                node.value.getItemID() != "grade1" &&
                node.value.getItemID() != "grade2" &&
                node.value.getItemID() != "grade3" &&
                node.value.getItemID() != "grade4"
            )
                editor.requestMoveNodeByDragging(true)
        }

        adapter.setOnItemDoubleListener { item, node ->
            val id = node.value.getItemID()
            if (id != "root" && id != "grade1" && id != "grade2" && id != "grade3" && id != "grade4") {
                setItem(node, editor)
            }
        }

        //treeView control listener
        val token = Object()
        val dismissRun = Runnable {
            binding.scalePercent.visibility = View.GONE
        }

        binding.mapView.setTreeViewControlListener(object : TreeViewControlListener {
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
                if (draggingNode != null && hittingNode != null) {
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
                        if (hNode.getItemID() != "root" && hNode.getItemID() != "grade1" && hNode.getItemID() != "grade2" &&
                            hNode.getItemID() != "grade3" && hNode.getItemID() != "grade4"
                        )
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

        binding.publicButton.setOnClickListener {
            // 공개 버튼 클릭할 경우, 비공개로 전환하거나 공개로 다시 전환할 수 있다.
            val setWindow: View =
                LayoutInflater.from(mapContext).inflate(R.layout.window_map_public_set, null)
            val publicSetWindow = PopupWindow(
                setWindow,
                ((requireContext().resources.displayMetrics.widthPixels) * 0.8).toInt(),
                WindowManager.LayoutParams.WRAP_CONTENT
            )

            publicSetWindow.isFocusable = true
            publicSetWindow.softInputMode = WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE
            publicSetWindow.update()
            publicSetWindow.showAtLocation(setWindow, Gravity.CENTER, 0, 0)

            publicSetWindow.isOutsideTouchable = true
            publicSetWindow.setTouchInterceptor { _, motionEvent ->
                if (motionEvent.action == MotionEvent.ACTION_OUTSIDE) {
                    publicSetWindow.dismiss()
                }
                false
            }

            val setPublicText: TextView = setWindow.findViewById(R.id.passwordText)
            val setPublicButton: Button = setWindow.findViewById(R.id.publicSetButton)
            val setPassword: EditText = setWindow.findViewById(R.id.setPassword)

            if (mapPublic) {
                setPublicText.text = "패스워드를 입력해주세요."
            } else {
                setPublicText.text = "공개로 전환하시겠습니까?\n패스워드를 입력해주세요."
            }

            fun savePublic(i: Int, password: String) {
                api.map_update(userID, i, password).enqueue(object : Callback<PostModel> {
                    override fun onResponse(call: Call<PostModel>, response: Response<PostModel>) {
                        Log.d("onResponse", "MindMapActivity map_update: 리스폰 성공 ")
                    }

                    override fun onFailure(call: Call<PostModel>, t: Throwable) {
                        Log.d("onFailure", "MindMapActivity map_update: 리스폰 실패 : $t")
                    }
                })
            }

            // 마인드맵 비밀번호 설정
            setPublicButton.setOnClickListener {
                val password = setPassword.text.toString()
                if (mapPublic) {
                    when {
                        password != "" -> {
                            mapPassword = password
                            savePublic(0, password)
                            mapPublic = false
                            binding.publicButton.setImageResource(R.drawable.ic_mindmap_private)
                            publicSetWindow.dismiss()
                        }
                        else -> {
                            Toast.makeText(
                                mapContext, "패스워드가 비어있습니다.", Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                } else {
                    when {
                        password != mapPassword -> {
                            Toast.makeText(
                                mapContext, "패스워드가 다릅니다.", Toast.LENGTH_SHORT
                            ).show()
                        }
                        password != "" -> {
                            mapPassword = password
                            savePublic(1, "")
                            mapPublic = true
                            binding.publicButton.setImageResource(R.drawable.ic_mindmap_public)
                            publicSetWindow.dismiss()
                        }
                        else -> {
                            Toast.makeText(
                                mapContext, "패스워드가 비어있습니다.", Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            }
        }

        // 조회수, 추천수 띄워서 확인
        binding.popularLayout.setOnClickListener {
            val visible: Boolean = binding.mapHit.visibility == View.VISIBLE &&
                    binding.mapRecommend.visibility == View.VISIBLE
            Log.d("Debug_Log", "popularLayout: $visible")
            if (!visible) {
                binding.mapHit.visibility = View.VISIBLE
                binding.mapRecommend.visibility = View.VISIBLE
                binding.recommendButton.visibility = View.VISIBLE
            } else {
                binding.mapHit.visibility = View.GONE
                binding.mapRecommend.visibility = View.GONE
                binding.recommendButton.visibility = View.GONE
            }
        }

        binding.recommendButton.setOnClickListener {
            api.map_like(userID, mapID).enqueue(object : Callback<PostModel> {
                override fun onResponse(call: Call<PostModel>, response: Response<PostModel>) {
                    Log.d("onResponse", "MindMapActivity map_like: 리스폰 성공 ${response.body()?.error.toString()}")
                    if(response.body()?.error.toString() == "ok") {
                        mapRecommend = mapRecommend + 1
                        binding.mapRecommend.text = mapRecommend.toString()
                    }
                    if(response.body()?.error.toString() == "failed") {

                    }
                }

                override fun onFailure(call: Call<PostModel>, t: Throwable) {
                    Log.d("onFailure", "MindMapActivity map_popular: 리스폰 실패 : $t")
                }
            })
        }
    }

    private fun setItem(node: NodeModel<ItemInfo>, editor: TreeViewEditor) {
        val setWindow: View =
            LayoutInflater.from(mapContext).inflate(R.layout.window_item_set,null)
        val itemSetWindow = PopupWindow(
            setWindow,
            ((requireContext().resources.displayMetrics.widthPixels) * 0.8).toInt(),
            WindowManager.LayoutParams.WRAP_CONTENT
        )

        val setButton: Button = setWindow.findViewById(R.id.itemSetButton)
        val setContent: EditText = setWindow.findViewById(R.id.setContentView)
        val setNote: EditText = setWindow.findViewById(R.id.setNoteView)

        setContent.setText(node.value.getContent())
        setNote.setText(node.value.getNote())

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
            val content = setContent.text.toString()
            val note = setNote.text.toString()

            if (content != "" && note != "") {
                val view = editor.container.getTreeViewHolder(node).view
                node.value.setContent(content)
                node.value.setNote(note)
                view.findViewById<TextView>(R.id.content).text = content
                saveDB(node, view, "update")
                editor.focusMidLocation()
                itemSetWindow.dismiss()
            } else {
                Toast.makeText(
                    mapContext, "제목(내용)이 비어있습니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun getTreeLayoutManager(): TreeLayoutManager {
        val space_50dp = 30
        val space_20dp = 20
        val line = getLine()
        return CompactHorizonLeftAndRightLayoutManager(mapContext,space_50dp,space_20dp,line);
    }

    private fun getLine(): BaseLine {
        return StraightLine(Color.parseColor("#055287"), 2)
    }
}