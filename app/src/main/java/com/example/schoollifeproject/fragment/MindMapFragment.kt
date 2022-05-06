package com.example.schoollifeproject.fragment

import android.app.Activity.RESULT_OK
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.SystemClock
import android.util.Log
import android.view.*
import android.widget.*
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.net.toFile
import androidx.fragment.app.Fragment
import com.example.schoollifeproject.R
import com.example.schoollifeproject.adapter.ItemAdapter
import com.example.schoollifeproject.databinding.FragmentMindMapBinding
import com.example.schoollifeproject.model.APIS
import com.example.schoollifeproject.model.ItemInfo
import com.example.schoollifeproject.model.PostModel
import com.gyso.treeview.TreeViewEditor
import com.gyso.treeview.layout.CompactHorizonLeftAndRightLayoutManager
import com.gyso.treeview.layout.TreeLayoutManager
import com.gyso.treeview.line.BaseLine
import com.gyso.treeview.line.StraightLine
import com.gyso.treeview.listener.TreeViewControlListener
import com.gyso.treeview.model.NodeModel
import com.gyso.treeview.model.TreeModel
import okhttp3.MediaType
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

import okhttp3.MultipartBody
import okhttp3.RequestBody
import android.net.Uri
import android.webkit.MimeTypeMap
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.Request
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.FileOutputStream
import android.provider.MediaStore

import android.provider.DocumentsContract





class MindMapFragment : Fragment() {
    private val TAG = this.javaClass.toString()
    private val api = APIS.create()
    val adapter: ItemAdapter = ItemAdapter()

    private lateinit var binding: FragmentMindMapBinding
    private val handler = Handler()
    var mapContext: Context? = null

    private lateinit var userID: String // 로그인한 유저 ID
    private lateinit var mapID: String // 선택한 맵의 유저 ID
    private var itemMaxNum = 0

    private var mapHit = 0
    private var mapRecommend = 0

    private var mapPublic = true
    private lateinit var mapPassword: String

    private lateinit var resultLauncher: ActivityResultLauncher<Intent>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreate(savedInstanceState)
        binding = FragmentMindMapBinding.inflate(inflater, container, false)
        mapContext = context

        userID = arguments?.getString("ID").toString() // menuActivity를 통해 받은 userID
        mapID = arguments?.getString("mapID").toString() // menuActivity를 통해 받은 userID
        Log.d("$TAG", "userID: ${userID}, ${mapID}")
        if (userID != mapID) {
            adapter.mapEditable = false
            binding.publicButton.visibility = View.GONE
        }

        itemMaxNum = 0

        initWidgets()
        return binding.root
    }

    private fun initWidgets() {
        val treeLayoutManager = getTreeLayoutManager()

        binding.mapView.adapter = adapter
        binding.mapView.setTreeLayoutManager(treeLayoutManager)

        /**
         * 마인드맵 기본 구성
         */
        val root: NodeModel<ItemInfo> = NodeModel<ItemInfo>(ItemInfo("root", "root", null, null))
        val mapView: TreeModel<ItemInfo> = TreeModel(root)

        val grade1: NodeModel<ItemInfo> = NodeModel<ItemInfo>(ItemInfo("grade1", "1학년", null, null))
        grade1.value.setPosition(true)
        val grade2: NodeModel<ItemInfo> = NodeModel<ItemInfo>(ItemInfo("grade2", "2학년", null, null))
        grade2.value.setPosition(true)
        val grade3: NodeModel<ItemInfo> = NodeModel<ItemInfo>(ItemInfo("grade3", "3학년", null, null))
        grade3.value.setPosition(false)
        val grade4: NodeModel<ItemInfo> = NodeModel<ItemInfo>(ItemInfo("grade4", "4학년", null, null))
        grade4.value.setPosition(false)
        mapView.addNode(root, grade3, grade4, grade1, grade2)
        adapter.treeModel = mapView

        /**
         * 마인드맵 추가/제거 관련 객체
         */
        val editor: TreeViewEditor = binding.mapView.editor

        /**
         * mapID 체크해 DB에서 마인드맵 공개여부 확인
         */
        api.map_public(mapID).enqueue(object : Callback<PostModel> {
            override fun onResponse(call: Call<PostModel>, response: Response<PostModel>) {
                Log.d(
                    "$TAG",
                    "map_public: 리스폰 성공 ${response.body()?.error.toString()}"
                )
                if (response.body()?.error.toString() == "failed") {
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
                Log.d("$TAG", "map_public: 리스폰 실패 : $t")
            }
        })

        /**
         * mapID 체크해 DB에서 마인드맵 노드 불러오기
         */
        api.item_load(mapID).enqueue(object : Callback<List<ItemInfo>> {
            override fun onResponse(
                call: Call<List<ItemInfo>>,
                response: Response<List<ItemInfo>>
            ) {
                val mapItems = HashMap<String, NodeModel<ItemInfo>>()
                Log.d("${this.javaClass}", "item_load: 리스폰 성공")
                if (response.body() != null) {

                    for (i in response.body()!!) {
                        Log.d("$TAG", "item_load/itemID: ${i.getItemID()}")
                        i.setPosition(i.getItemID()[i.getItemID().length - 1].toString() == "L")
                        val last = if (i.getPosition()) "L" else "R"
                        val item = NodeModel<ItemInfo>(i)
                        val childID = i.getItemID().split("_")[1].split("$last")[0]
                        mapItems[childID] = item
                    }

                    for (i in response.body()!!) {
                        val parentID = i.getItemID().split("_")[0]
                        val last = if (i.getPosition()) "L" else "R"
                        val childID = i.getItemID().split("_")[1].split("$last")[0]
                        Log.d("$TAG", "item_load/nodesInfo: ${parentID}, ${childID}")

                        when (parentID) {
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
                            }
                        }
                        itemMaxNum = (if (i.getNum() != null) i.getNum()!! else throw NullPointerException("Expression 'i.getNum()' must not be null"))
                        itemMaxNum++
                        Log.d("$TAG", "item_load/itemMaxNum: ${itemMaxNum}")
                        editor.focusMidLocation()
                    }
                }
            }

            override fun onFailure(call: Call<List<ItemInfo>>, t: Throwable) {
                Log.d("$TAG", "item_load: 리스폰 실패 : $t")
            }
        })

        /**
         * mapID 체크해 DB에서 조회수/추천수 불러오기
         */
        api.map_popular(mapID).enqueue(object : Callback<PostModel> {
            override fun onResponse(call: Call<PostModel>, response: Response<PostModel>) {
                Log.d(
                    "$TAG",
                    "map_popular: 리스폰 성공 ${response.body()?.error.toString()}"
                )
                if (response.body()?.error.toString() == "ok") {
                    mapHit = response.body()?.mapHit!! + 1
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
                Log.d("$TAG", "map_popular: 리스폰 실패 : $t")
            }
        })

        itemEvent(editor, adapter)
    }

    /**
     * DB에서 마인드맵 노드 삽입/삭제/변경
     */
    private fun saveDB(item: NodeModel<ItemInfo>, view: View?, mode: String) {

        // TODO: TOP이랑 LEFT값이 있는 경우 APP으로 설정하게 수정
        val itemID = item.value.getItemID()
        val itemTop = "APP"
        val itemLeft = "APP"
        val itemContent = item.value.getContent()
        val itemCount = item.value.getNum()
        var itemWidth = "150px"
        var itemHeight = "50px"
        val itemNote = item.value.getNote()
        if (view != null) {
            view.addOnLayoutChangeListener { _, i, i2, i3, i4, _, _, _, _ ->
                itemWidth = "${i3 - i}px"
                itemHeight = "${i4 - i2}px"
            }
        }
        Log.d("$TAG", "item_save: $mode")
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
                    Log.d("$TAG", "item_save: 리스폰 성공 $mode")
                    if (response.body()?.error.toString() == "insert") {
                        Log.d("$TAG", "item_save: 삽입 완료")
                    } else if (response.body()?.error.toString() == "update") {
                        Log.d("$TAG", "item_save: 변경 완료")
                    } else if (response.body()?.error.toString() == "delete") {
                        Log.d("$TAG", "item_save: 삭제 완료")
                    }
                }

                override fun onFailure(call: Call<PostModel>, t: Throwable) {
                    Log.d("$TAG", "item_save: 리스폰 실패 : $t")

                }
            })
        }
    }

    private fun saveFileDB() {

        val setWindow: View =
            LayoutInflater.from(mapContext).inflate(R.layout.window_item_file, null)
        val FileSetWindow = PopupWindow(
            setWindow,
            ((requireContext().resources.displayMetrics.widthPixels) * 0.8).toInt(),
            WindowManager.LayoutParams.WRAP_CONTENT
        )

        FileSetWindow.isFocusable = true
        FileSetWindow.softInputMode = WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE
        FileSetWindow.update()
        FileSetWindow.showAtLocation(setWindow, Gravity.CENTER, 0, 0)

        FileSetWindow.isOutsideTouchable = true
        FileSetWindow.setTouchInterceptor { _, motionEvent ->
            if (motionEvent.action == MotionEvent.ACTION_OUTSIDE) {
                FileSetWindow.dismiss()
            }
            false
        }

        val fileAddButton = setWindow.findViewById<Button>(R.id.fileAddButton)
        val fileSetButton = setWindow.findViewById<Button>(R.id.fileSetButton)

        fileAddButton.setOnClickListener {

            resultLauncher.launch(Intent(Intent.ACTION_GET_CONTENT).apply {

            })
        }
    }
        //파일 생성
        //img_url은 이미지의 경로
        //파일 생성
        //img_url은 이미지의 경로
        /*
        val file: File = File(img_url)
        val requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file)
        val body = MultipartBody.Part.createFormData("uploaded_file", file.name, requestFile)
        val resultCall: Call<PostModel> = APIS_login.upload_image(body)
        resultCall.enqueue(object : Callback<PostModel> {
            override fun onResponse(call: Call<PostModel>, response: Response<PostModel>) {

            }
            override fun onFailure(call: Call<PostModel>, t: Throwable) {

            }
        })*/

    private fun itemEvent(editor: TreeViewEditor, adapter: ItemAdapter) {
        lateinit var formId: MultipartBody.Part
        lateinit var formFile: MultipartBody.Part
        lateinit var formType: MultipartBody.Part

        editor.container.isAnimateAdd = true

        /**
         * file intent 설정 초기화
         */
        resultLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult(),
            ActivityResultCallback { result ->
                if (result.resultCode == RESULT_OK) {
                    val intent = result.data
                    val uri = intent!!.data
                    val imagePath = getRealPathFromURI(uri!!)
                    val file = File(imagePath)
                    formFile = FormDataUtil.getImageBody("media", file)

                        api.map_files(formFile).enqueue(object : Callback<String?> {
                            override fun onResponse(
                                call: Call<String?>,
                                response: Response<String?>
                            ) {
                                Log.e("uploadChat()", "성공 : ${response.body()}")
                            }

                            override fun onFailure(call: Call<String?>, t: Throwable) {
                                Log.e("uploadChat()", "에러 : " + t.message)
                            }
                        })
                }
            })

        /**
         * 노드 클릭 시 이벤트 실행
         * 하단 메뉴 1 : 노드 추가
         * 하단 메뉴 2 : 노드 수정
         * 하단 메뉴 3 : 노드 삭제, 하위 노드도 함께 삭제된다.
         * 하단 메뉴 4 : 노드 파일 설정
         */
        adapter.setOnItemListener { view, node ->
            Log.d("$TAG", "setOnItemListener: ${node.getValue().getItemID()}")
            val id = node.value.getItemID()
            if (id != "root") {
                val visible =
                    id != "grade1" && id != "grade2" && id != "grade3" && id != "grade4"
                binding.bottomNavigationView.menu.findItem(R.id.bottomMenu2).isVisible = visible
                binding.bottomNavigationView.menu.findItem(R.id.bottomMenu3).isVisible = visible
                binding.bottomNavigationView.menu.findItem(R.id.bottomMenu4).isVisible = visible
                binding.bottomNavigationView.visibility = View.VISIBLE
                binding.bottomNavigationView.run {
                    setOnItemSelectedListener { item ->
                        when (item.itemId) {
                            R.id.bottomMenu1 -> {
                                val last = if (node.value.getPosition()) "L" else "R"
                                val parent =
                                    if (visible) node.value.getItemID()
                                        .split("_")[1].split("$last")[0]
                                    else node.value.getItemID().split("_")[0]
                                val item: NodeModel<ItemInfo> =
                                    NodeModel<ItemInfo>(
                                        ItemInfo(
                                            "${parent}_item${itemMaxNum}$last",
                                            "ChildNode",
                                            itemMaxNum++,
                                            ""
                                        )
                                    )
                                Log.d(
                                    "$TAG",
                                    "setOnItemListener/bottomMenu1:  ${item.value.getItemID()}"
                                )
                                editor.addChildNodes(node, item)
                                saveDB(item, null, "insert")
                                binding.bottomNavigationView.visibility = View.INVISIBLE
                                true
                            }
                            R.id.bottomMenu2 -> {
                                setItem(node, editor, true)
                                "setOnItemListener/bottomMenu2: ${node.value.getItemID()}"
                                binding.bottomNavigationView.visibility = View.INVISIBLE
                                true
                            }
                            R.id.bottomMenu3 -> {
                                val warnWindow: View =
                                    LayoutInflater.from(mapContext)
                                        .inflate(R.layout.window_warning, null)
                                val warnSetWindow = PopupWindow(
                                    warnWindow,
                                    ((requireContext().resources.displayMetrics.widthPixels) * 0.7).toInt(),
                                    WindowManager.LayoutParams.WRAP_CONTENT
                                )

                                warnSetWindow.isFocusable = true
                                warnSetWindow.softInputMode =
                                    WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE
                                warnSetWindow.update()
                                warnSetWindow.showAtLocation(warnWindow, Gravity.CENTER, 0, 0)

                                warnSetWindow.isOutsideTouchable = true
                                warnSetWindow.setTouchInterceptor { _, motionEvent ->
                                    if (motionEvent.action == MotionEvent.ACTION_OUTSIDE) {
                                        warnSetWindow.dismiss()
                                    }
                                    false
                                }

                                val checkButton: Button = warnWindow.findViewById(R.id.checkButton)
                                val cancelButton: Button =
                                    warnWindow.findViewById(R.id.cancelButton)

                                checkButton.setOnClickListener {
                                    val children = node.getChildNodes()

                                    for (i in children) {
                                        Log.d(
                                            "$TAG",
                                            "setOnItemListener/bottomMenu3: ${i.value.getItemID()}"
                                        )
                                        saveDB(i, null, "delete")
                                    }
                                    saveDB(node, view, "delete")
                                    editor.removeNode(node)

                                    binding.bottomNavigationView.visibility = View.INVISIBLE
                                    warnSetWindow.dismiss()
                                }
                                cancelButton.setOnClickListener {
                                    warnSetWindow.dismiss()
                                }
                                true
                            }
                            R.id.bottomMenu4 -> {
                                resultLauncher.launch(Intent(Intent.ACTION_GET_CONTENT).apply {
                                    type ="image/*"
                                })
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

        /**
         * 노드 롱클릭 시 이벤트 실행
         * 루트/학년 하위 노드인 경우 노드 Drag & Drop 실행
         */
        adapter.setOnItemLongListener { item, node ->
            if (node.value.getItemID() != "root" &&
                node.value.getItemID() != "grade1" &&
                node.value.getItemID() != "grade2" &&
                node.value.getItemID() != "grade3" &&
                node.value.getItemID() != "grade4"
            )
                editor.requestMoveNodeByDragging(true)
        }

        /**
         * 노드 더블클릭 시 이벤트 실행
         * 노드 제목/내용 설정
         */
        adapter.setOnItemDoubleListener { item, node, b ->
            val id = node.value.getItemID()
            if (id != "root" && id != "grade1" && id != "grade2" && id != "grade3" && id != "grade4") {
                setItem(node, editor, b)
            }
        }

        /**
         * 공개 버튼 클릭 시
         * 공개/비공개 전환 가능, 비공개인 경우 패스워드 입력/설정
         */
        binding.publicButton.setOnClickListener {
            val setWindow: View =
                LayoutInflater.from(mapContext).inflate(R.layout.window_map_public_set, null)
            val publicSetWindow = PopupWindow(
                setWindow,
                ((requireContext().resources.displayMetrics.widthPixels) * 0.6).toInt(),
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
            val setPublicButton: Button = setWindow.findViewById(R.id.pwButton)
            val setPassword: EditText = setWindow.findViewById(R.id.setPassword)

            if (mapPublic) {
                setPublicText.text = "패스워드를 입력하세요.(10자)"
            } else {
                setPassword.visibility = View.GONE
                setPublicText.text = "공개로 전환하시겠습니까?"
            }

            fun savePublic(i: Int, password: String) {
                api.map_update(userID, i, password).enqueue(object : Callback<PostModel> {
                    override fun onResponse(
                        call: Call<PostModel>,
                        response: Response<PostModel>
                    ) {
                        Log.d("$TAG", "map_update: 리스폰 성공 ")
                    }

                    override fun onFailure(call: Call<PostModel>, t: Throwable) {
                        Log.d("$TAG", "map_update: 리스폰 실패 : $t")
                    }
                })
            }

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
                    mapPassword = password
                    savePublic(1, "")
                    mapPublic = true
                    binding.publicButton.setImageResource(R.drawable.ic_mindmap_public)
                    publicSetWindow.dismiss()
                }
            }
        }

        //treeView control listener
        val token = Object()
        val dismissRun = Runnable {
            binding.scalePercent.visibility = View.GONE
        }

        /**
         * 줌인/아웃, 노드 Drag & Drop 정보 확인
         * Drop된 경우 DB에 노드 정보 변경
         */
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
                        "$TAG",
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
                        "$TAG",
                        "onDragMoveNodesEnd: draging[${(draggingNode.value as ItemInfo).getItemID()}]" +
                                "hittingNode[${(hittingNode.value as ItemInfo).getItemID()}]"
                    )
                    val dNode = draggingNode.value as ItemInfo
                    val hNode = hittingNode.value as ItemInfo

                    val dLast = if ((draggingNode.value as ItemInfo).getPosition()) "L" else "R"
                    val hLast = if ((hittingNode.value as ItemInfo).getPosition()) "L" else "R"
                    val parent =
                        if (hNode.getItemID() != "root" && hNode.getItemID() != "grade1" && hNode.getItemID() != "grade2" &&
                            hNode.getItemID() != "grade3" && hNode.getItemID() != "grade4"
                        )
                            hNode.getItemID().split("_")[1].split("$hLast")[0]
                        else hNode.getItemID().split("_")[0]
                    dNode.setItemID(
                        "${parent}_${
                            dNode.getItemID().split("_")[1].split("$dLast")[0]
                        }$hLast"
                    )
                    if (draggingView != null) {
                        saveDB(draggingNode as NodeModel<ItemInfo>, draggingView, "update")
                    }
                }
            }
        })

        binding.focusMidButton.setOnClickListener {
            editor.focusMidLocation()
        }

        // 조회수, 추천수 띄워서 확인
        binding.popularLayout.setOnClickListener {
            val visible: Boolean = binding.mapHit.visibility == View.VISIBLE &&
                    binding.mapRecommend.visibility == View.VISIBLE
            Log.d("$TAG", "popularLayout: $visible")
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
                    Log.d(
                        "$TAG",
                        "map_like: 리스폰 성공 ${response.body()?.error.toString()}"
                    )
                    if (response.body()?.error.toString() == "ok") {
                        mapRecommend = mapRecommend + 1
                        binding.mapRecommend.text = mapRecommend.toString()
                    }
                    if (response.body()?.error.toString() == "failed") {

                    }
                }

                override fun onFailure(call: Call<PostModel>, t: Throwable) {
                    Log.d("$TAG", "map_like: 리스폰 실패 : $t")
                }
            })
        }
    }

    private fun setItem(node: NodeModel<ItemInfo>, editor: TreeViewEditor, b: Boolean) {

        val setWindow: View =
            LayoutInflater.from(mapContext).inflate(R.layout.window_item_set, null)
        val itemSetWindow = PopupWindow(
            setWindow,
            ((requireContext().resources.displayMetrics.widthPixels) * 0.8).toInt(),
            WindowManager.LayoutParams.WRAP_CONTENT
        )

        val setButton: Button = setWindow.findViewById(R.id.itemSetButton)
        val setContent: EditText = setWindow.findViewById(R.id.setContentView)
        val setNote: EditText = setWindow.findViewById(R.id.setNoteView)

        if (!b) {
            setContent.isEnabled = false
            setNote.isEnabled = false
        }

        setContent.setText(node.value.getContent())
        setNote.setText(node.value.getNote())

        itemSetWindow.isFocusable = true
        itemSetWindow.softInputMode = WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE
        itemSetWindow.update()
        itemSetWindow.showAtLocation(setWindow, Gravity.CENTER, 0, 0)

        itemSetWindow.isOutsideTouchable = true
        itemSetWindow.setTouchInterceptor { _, motionEvent ->
            if (motionEvent.action == MotionEvent.ACTION_OUTSIDE) {
                itemSetWindow.dismiss()
            }
            false
        }

        setButton.setOnClickListener {
            if (b) {
                val content = setContent.text.toString()
                val note = setNote.text.toString()

                if (content != "") {
                    val view = editor.container.getTreeViewHolder(node).view
                    node.value.setContent(content)
                    node.value.setNote(note)
                    view.findViewById<TextView>(R.id.content).text = content
                    saveDB(node, view, "update")
                    editor.focusMidLocation()
                    itemSetWindow.dismiss()
                } else {
                    Toast.makeText(
                        mapContext, "제목(내용)이 비어있습니다.", Toast.LENGTH_SHORT
                    ).show()
                }
            } else {
                itemSetWindow.dismiss()
            }
        }
    }

    private fun getTreeLayoutManager(): TreeLayoutManager {
        val space_50dp = 30
        val space_20dp = 20
        val line = getLine()
        return CompactHorizonLeftAndRightLayoutManager(mapContext, space_50dp, space_20dp, line);
    }

    private fun getLine(): BaseLine {
        return StraightLine(Color.parseColor("#055287"), 2)
    }
    private fun getRealPathFromURI(contentUri: Uri): String? {
        if (contentUri.path!!.startsWith("/storage")) {
            return contentUri.path
        }
        val id = DocumentsContract.getDocumentId(contentUri).split(":").toTypedArray()[1]
        val columns = arrayOf(MediaStore.Files.FileColumns.DATA)
        val selection = MediaStore.Files.FileColumns._ID + " = " + id
        val cursor: Cursor? = activity!!.contentResolver.query(
            MediaStore.Files.getContentUri("external"),
            columns,
            selection,
            null,
            null
        )
        try {
            val columnIndex: Int = cursor!!.getColumnIndex(columns[0])
            if (cursor.moveToFirst()) {
                return cursor.getString(columnIndex)
            }
        } finally {
            cursor!!.close()
        }
        return null
    }
}

object FormDataUtil {

    fun getBody(key: String, value: Any): MultipartBody.Part {
        return MultipartBody.Part.createFormData(key, value.toString())
    }

    fun getImageBody(key: String, file: File): MultipartBody.Part {
        return MultipartBody.Part.createFormData(
            key,
            filename = file.name,
            body = file.asRequestBody("image/*".toMediaType())
        )
    }

    fun getVideoBody(key: String, file: File): MultipartBody.Part {
        return MultipartBody.Part.createFormData(
            name = key,
            filename = file.name,
            body = file.asRequestBody("video/*".toMediaType())
        )
    }
}