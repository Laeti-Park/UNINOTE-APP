package com.example.schoollifeproject.fragment

import android.app.Activity.RESULT_OK
import android.app.DownloadManager
import android.content.ContentValues
import android.content.Intent
import android.graphics.Color
import android.os.*
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.util.Log
import android.view.*
import android.widget.*
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.VERTICAL
import com.example.schoollifeproject.R
import com.example.schoollifeproject.adapter.ItemAdapter
import com.example.schoollifeproject.adapter.ItemFileAdapter
import com.example.schoollifeproject.databinding.FragmentMindMapBinding
import com.example.schoollifeproject.model.APIS
import com.example.schoollifeproject.model.FileModel
import com.example.schoollifeproject.model.ItemModel
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
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import okio.BufferedSink
import okio.source
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.*


/**
 * 로드맵(마인드맵) Fragment
 * 작성자 : 박동훈
 */
class MindMapFragment : Fragment() {
    private val TAG = this.javaClass.toString()
    private val api = APIS.create()
    val adapter: ItemAdapter = ItemAdapter()

    private lateinit var binding: FragmentMindMapBinding
    private val handler = Handler(Looper.getMainLooper())

    private lateinit var userID: String // 로그인한 유저 ID
    private lateinit var mapID: String // 선택한 맵의 유저 ID
    private var itemMaxNum = 1

    private var mapHit = 0
    private var mapRecommend = 0

    private var mapPublic = true
    private lateinit var mapPassword: String

    private lateinit var resultLauncher: ActivityResultLauncher<Intent>
    private lateinit var targetItem: NodeModel<ItemModel>
    private lateinit var targetItemID: String
    private var downloadId: Long = -1L
    private lateinit var downloadManager: DownloadManager

    private lateinit var warnPopupView: View
    private lateinit var warnPopupWindow: PopupWindow
    private lateinit var itemPopupView: View
    private lateinit var itemPopupWindow: PopupWindow
    private lateinit var filePopupView: View
    private lateinit var filePopupWindow: PopupWindow
    private lateinit var publicPopupView: View
    private lateinit var publicPopupWindow: PopupWindow

    fun newInstance(userID: String, mapID: String): MindMapFragment {
        val args = Bundle()
        args.putString("userID", userID)
        args.putString("mapID", mapID)

        val mindMapFragment = MindMapFragment()
        mindMapFragment.arguments = args

        return mindMapFragment
    }

    /**
     * 접속한 userID와 mapID가 다를 경우 수정 못하게 설정
     */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreate(savedInstanceState)
        binding = FragmentMindMapBinding.inflate(inflater, container, false)

        userID = arguments?.getString("userID").toString() // menuActivity를 통해 받은 userID
        mapID = arguments?.getString("mapID").toString() // menuActivity를 통해 받은 userID
        Log.d("$TAG", "userID: ${userID}, ${mapID}")

        if (userID != mapID) {
            adapter.mapEditable = false
            binding.publicButton.visibility = View.GONE
        }

        itemMaxNum = 1

        /** 팝업 윈도우 관련 설정
         * itemPopupWindow : 아이템 내용 설정
         * warnPopupWindow : 아이템 관련 경고
         * filePopupWindow : 아이템 파일 설정
         * publicPopupWindow : 공개/비공개 관련 설정
         */
        itemPopupView = LayoutInflater.from(context!!).inflate(R.layout.window_item_set, null)
        warnPopupView = LayoutInflater.from(context!!).inflate(R.layout.window_warning, null)
        filePopupView = LayoutInflater.from(context!!).inflate(R.layout.window_item_file, null)
        publicPopupView =
            LayoutInflater.from(context!!).inflate(R.layout.window_map_public_set, null)

        itemPopupWindow = PopupWindow(
            itemPopupView,
            ((context!!.resources.displayMetrics.widthPixels) * 0.9).toInt(),
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        warnPopupWindow = PopupWindow(
            warnPopupView,
            ((context!!.resources.displayMetrics.widthPixels) * 0.7).toInt(),
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        filePopupWindow = PopupWindow(
            filePopupView,
            ((context!!.resources.displayMetrics.widthPixels) * 0.9).toInt(),
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        publicPopupWindow = PopupWindow(
            publicPopupView,
            ((context!!.resources.displayMetrics.widthPixels) * 0.7).toInt(),
            WindowManager.LayoutParams.WRAP_CONTENT
        )

        itemPopupWindow.isOutsideTouchable = true
        warnPopupWindow.isOutsideTouchable = true
        filePopupWindow.isOutsideTouchable = true
        publicPopupWindow.isOutsideTouchable = true

        publicCheck()
        initWidgets()

        return binding.root
    }

    /**
     * mapID 체크해 DB에서 마인드맵 공개여부 확인
     */
    private fun publicCheck() {
        api.map_public(mapID).enqueue(object : Callback<PostModel> {
            override fun onResponse(call: Call<PostModel>, response: Response<PostModel>) {
                Log.d(
                    "$TAG",
                    "map_public: 리스폰 성공 ${response.body()?.error.toString()}, ${adapter.mapEditable}"
                )
                if (response.body()?.error.toString() == "failed") {
                    mapPublic = true
                } else {
                    if (response.body()?.public == 0) {
                        mapPassword = response.body()?.mapPassword.toString()
                        binding.publicButton.setImageResource(R.drawable.ic_mindmap_private)
                        mapPublic = false

                        if (!adapter.mapEditable) {
                            binding.mapView.visibility = View.INVISIBLE
                            publicSet(false)
                        }
                    } else {
                        mapPublic = true
                    }
                }
            }

            override fun onFailure(call: Call<PostModel>, t: Throwable) {
                Log.d("$TAG", "map_public: 리스폰 실패 : $t")
            }
        })
    }


    /**
     * 로드맵 기본 구성
     */
    private fun initWidgets() {
        val treeLayoutManager = getTreeLayoutManager()

        binding.mapView.adapter = adapter
        binding.mapView.setTreeLayoutManager(treeLayoutManager)

        /**
         * 루트와 학년별 아이템 생성
         */
        val root: NodeModel<ItemModel> =
            NodeModel<ItemModel>(ItemModel("root", "UNINOTE", null, null))
        val mapView: TreeModel<ItemModel> = TreeModel(root)

        val grade1: NodeModel<ItemModel> =
            NodeModel<ItemModel>(ItemModel("grade1", "1학년", null, null))
        grade1.value.setPosition(true)
        val grade2: NodeModel<ItemModel> =
            NodeModel<ItemModel>(ItemModel("grade2", "2학년", null, null))
        grade2.value.setPosition(true)
        val grade3: NodeModel<ItemModel> =
            NodeModel<ItemModel>(ItemModel("grade3", "3학년", null, null))
        grade3.value.setPosition(false)
        val grade4: NodeModel<ItemModel> =
            NodeModel<ItemModel>(ItemModel("grade4", "4학년", null, null))
        grade4.value.setPosition(false)
        mapView.addNode(root, grade3, grade4, grade1, grade2)
        adapter.treeModel = mapView

        /**
         * 로드맵 추가/제거 관련 객체
         */
        val editor: TreeViewEditor = binding.mapView.editor

        /**
         * mapID 체크해 DB에서 로드맵 아이템 불러오기
         */

        api.item_load(mapID).enqueue(object : Callback<List<ItemModel>> {
            override fun onResponse(
                call: Call<List<ItemModel>>,
                response: Response<List<ItemModel>>
            ) {
                val mapItems = HashMap<String, NodeModel<ItemModel>>()
                Log.d("${this.javaClass}", "item_load: 리스폰 성공")
                if (response.body() != null) {

                    for (i in response.body()!!) {
                        Log.d("$TAG", "item_load/itemID: ${i.getItemID()}")
                        i.setPosition(i.getItemID()[i.getItemID().length - 1].toString() == "L")
                        val last = if (i.getPosition()) "L" else "R"
                        val item = NodeModel<ItemModel>(i)
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
                        itemMaxNum =
                            (if (i.getNum() != null) i.getNum()!! else throw NullPointerException("Expression 'i.getNum()' must not be null"))
                        itemMaxNum++
                        Log.d("$TAG", "item_load/itemMaxNum: ${itemMaxNum}")
                    }
                    editor.focusMidLocation()
                }
            }

            override fun onFailure(call: Call<List<ItemModel>>, t: Throwable) {
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
        handler.postDelayed({
            editor.focusMidLocation()
        }, 1000)

        itemEvent(editor, adapter)
    }

    /**
     * DB에서 마인드맵 노드 삽입/삭제/변경
     */
    private fun saveDB(item: NodeModel<ItemModel>, view: View?, mode: String) {

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
                targetItemID,
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

    /**
     * 로드맵 아이템 정보 수정 (BottomNavigationMenu 2 OR adapter.setOnItemDoubleListener)
     */
    private fun setItem(node: NodeModel<ItemModel>, editor: TreeViewEditor, b: Boolean) {


        val setButton: Button = itemPopupView.findViewById(R.id.itemSetButton)
        val setContent: EditText = itemPopupView.findViewById(R.id.setContentView)
        val setNote: EditText = itemPopupView.findViewById(R.id.setNoteView)
        val backButton: ImageButton = itemPopupView.findViewById(R.id.backButton)
        val fileButton: Button = itemPopupView.findViewById(R.id.fileButton)

        if (!b) {
            setContent.isEnabled = false
            setNote.isEnabled = false
            setButton.visibility = View.GONE
        }

        setContent.setText(node.value.getContent())
        setNote.setText(node.value.getNote())

        itemPopupWindow.isFocusable = true
        itemPopupWindow.softInputMode = WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE
        itemPopupWindow.update()
        itemPopupWindow.showAtLocation(itemPopupView, Gravity.CENTER, 0, 0)

        itemPopupWindow.setTouchInterceptor { _, motionEvent ->
            if (motionEvent.action == MotionEvent.ACTION_OUTSIDE) {
                itemPopupWindow.dismiss()
            }
            false
        }

        setButton.setOnClickListener {
            val content = setContent.text.toString()
            val note = setNote.text.toString()

            val view = editor.container.getTreeViewHolder(node).view
            node.value.setContent(content)
            node.value.setNote(note)
            view.findViewById<TextView>(R.id.content).text = content
            saveDB(node, view, "update")
            editor.focusMidLocation()
            itemPopupWindow.dismiss()
        }

        backButton.setOnClickListener {
            itemPopupWindow.dismiss()
        }

        fileButton.setOnClickListener {
            itemPopupWindow.dismiss()
            saveFileDB(node, editor, b)
        }
    }

    /**
     * 로드맵 아이템 파일 정보 수정
     */
    private fun saveFileDB(
        node: NodeModel<ItemModel>,
        editor: TreeViewEditor,
        mapEditable: Boolean
    ) {
        val fileList: MutableList<FileModel> = mutableListOf()
        val fileAdapter = ItemFileAdapter(fileList)
        fileAdapter.mapEditable = mapEditable

        fun reload() {
            api.item_file_load(mapID, targetItem.value.getItemID())
                .enqueue(object : Callback<List<FileModel>> {
                    override fun onResponse(
                        call: Call<List<FileModel>>,
                        response: Response<List<FileModel>>
                    ) {
                        Log.d(
                            "$TAG",
                            "item_file_load: 리스폰 완료 ${response.body()!!.size}"
                        )
                        val list = mutableListOf<FileModel>()
                        for (i in 0 until response.body()!!.size) {
                            Log.d(
                                "$TAG",
                                "item_file_load/fileName: ${response.body()!![i].getFileName()}, ${response.body()!![i].getFileRealName()}"
                            )
                            val ar: List<String> = response.body()!![i].getFileName().split('.')
                            val ext = ar[ar.size - 1]

                            val contacts = (
                                    FileModel(
                                        response.body()!![i].getFileName(),
                                        response.body()!![i].getFileRealName(),
                                        ext
                                    )
                                    )
                            list.add(contacts)
                        }
                        fileList.addAll(list)

                        Log.d(
                            "$TAG",
                            "item_file_load/filesize: ${fileList.size}"
                        )
                        fileAdapter.notifyDataSetChanged()
                    }

                    override fun onFailure(call: Call<List<FileModel>>, t: Throwable) {
                        Log.d("$TAG", "item_file_load: 리스폰 실패")
                    }
                })
        }

        reload()

        val fileListView = filePopupView.findViewById<RecyclerView>(R.id.fileListView)
        val fileAddButton = filePopupView.findViewById<Button>(R.id.fileAddButton)
        val backButton = filePopupView.findViewById<ImageButton>(R.id.backButton)
        val infoButton = filePopupView.findViewById<Button>(R.id.infoButton)
        val fileButton = filePopupView.findViewById<Button>(R.id.fileButton)

        if (!adapter.mapEditable) {
            fileAddButton.visibility = View.GONE
        }

        val mLayoutManager: RecyclerView.LayoutManager =
            LinearLayoutManager(context!!)
        fileListView.setLayoutManager(mLayoutManager)

        val dividerItemDecoration = DividerItemDecoration(context!!, VERTICAL)
        fileListView.addItemDecoration(dividerItemDecoration)

        fileListView.adapter = fileAdapter

        filePopupWindow.isFocusable = true
        filePopupWindow.softInputMode = WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE
        filePopupWindow.update()
        filePopupWindow.showAtLocation(filePopupView, Gravity.CENTER, 0, 0)

        filePopupWindow.setTouchInterceptor { _, motionEvent ->
            if (motionEvent.action == MotionEvent.ACTION_OUTSIDE) {
                filePopupWindow.dismiss()
            }
            false
        }

        fileAddButton.setOnClickListener {
            resultLauncher.launch(Intent(Intent.ACTION_GET_CONTENT).apply {
                type = "*/*"
                putExtra(
                    Intent.EXTRA_MIME_TYPES, arrayOf(
                        "application/x-mspowerpoint",
                        "application/vnd.openxmlformats-officedocument.presentationml.presentation",
                        "application/zip",
                        "image/jpg",
                        "image/jpeg",
                        "image/png",
                        "application/vnd.hancom.hwp",
                        "application/haansofthwp",
                        "application/x-hwp",
                        "application/vnd.hancom.hwpx",
                        "application/haansofthwpx"
                    )
                )
            })
        }

        backButton.setOnClickListener {
            filePopupWindow.dismiss()
        }

        infoButton.setOnClickListener {
            filePopupWindow.dismiss()
            setItem(node, editor, mapEditable)
        }

        fileButton.setOnClickListener {
            reload()
        }

        fileAdapter.setOnFileDelListener { view, fileModel ->

            warnPopupWindow.isFocusable = true
            warnPopupWindow.softInputMode =
                WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE
            warnPopupWindow.update()
            warnPopupWindow.showAtLocation(warnPopupView, Gravity.CENTER, 0, 0)

            warnPopupWindow.setTouchInterceptor { _, motionEvent ->
                if (motionEvent.action == MotionEvent.ACTION_OUTSIDE) {
                    warnPopupWindow.dismiss()
                }
                false
            }

            val delMessage: TextView = warnPopupView.findViewById(R.id.warningText)
            val checkButton: Button = warnPopupView.findViewById(R.id.checkButton)
            val cancelButton: Button =
                warnPopupView.findViewById(R.id.cancelButton)

            delMessage.text = "파일을 삭제하시겠습니까?"

            checkButton.setOnClickListener {
                api.item_file_del(userID, targetItem.value.getItemID(), fileModel.getFileRealName())
                    .enqueue(object : Callback<PostModel> {
                        override fun onResponse(
                            call: Call<PostModel>,
                            response: Response<PostModel>
                        ) {
                            Log.d("$TAG", "item_file_del: 리스폰 성공 ${response.body()!!.error}")
                            filePopupWindow.dismiss()
                            saveFileDB(targetItem, editor, adapter.mapEditable)
                        }

                        override fun onFailure(call: Call<PostModel>, t: Throwable) {
                            Log.d("$TAG", "item_file_del: 리스폰 실패 ${t}")
                        }
                    })
                warnPopupWindow.dismiss()
                reload()
            }
            cancelButton.setOnClickListener {
                warnPopupWindow.dismiss()
            }
        }

        fileAdapter.setOnFileListener { view, fileModel ->
            Log.d("$TAG", "fileClickListener: ${fileModel.getFileRealName()}")

            val uri =
                "/upload/$mapID/${targetItem.value.getItemID()}/${fileModel.getFileRealName()}"
            api.item_file_down(uri).enqueue(object : Callback<ResponseBody> {
                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {
                    Log.d("$TAG", "item_file_down: 리스폰 성공")

                    val mimeType = "${fileModel.getFileRealName()}".substring(
                        "${fileModel.getFileRealName()}".indexOf(".") + 1,
                        "${fileModel.getFileRealName()}".length
                    )

                    try {
                        var inputStream: InputStream? = null
                        var outputStream: OutputStream? = null
                        try {
                            val fileReader = ByteArray(4096)
                            var fileSizeDownloaded: Long = 0
                            inputStream = response.body()!!.byteStream()
                            outputStream =
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                                    val resolver = context!!.contentResolver
                                    val uri = resolver.insert(
                                        MediaStore.Files.getContentUri("external"),
                                        ContentValues().apply {
                                            put(
                                                MediaStore.MediaColumns.DISPLAY_NAME,
                                                "${fileModel.getFileRealName()}"
                                            )
                                            put(
                                                MediaStore.MediaColumns.MIME_TYPE,
                                                "application/$mimeType"
                                            )
                                            put(
                                                MediaStore.MediaColumns.RELATIVE_PATH,
                                                Environment.DIRECTORY_DOWNLOADS + File.separator + "UNINOTE"
                                            )
                                        })
                                    uri?.let { resolver.openOutputStream(it) }
                                } else {
                                    val folderPath =
                                        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).path + File.separator + "UNINOTE"
                                    val folder = File(folderPath)
                                    if (!folder.exists()) {
                                        folder.mkdirs()
                                    }
                                    val file =
                                        File(folderPath + File.separator + "${fileModel.getFileRealName()}")
                                    if (file.exists()) {
                                        file.delete()
                                    }
                                    FileOutputStream(file)
                                }
                            if (outputStream != null) {
                                while (true) {
                                    val read: Int? = inputStream?.read(fileReader)
                                    if (read == -1) {
                                        break
                                    }
                                    outputStream?.write(fileReader, 0, read!!)
                                    fileSizeDownloaded += read?.toLong()!!
                                }
                                outputStream?.flush()
                            }
                        } catch (e: IOException) {
                            Log.d("$TAG", "IOException: ${e.printStackTrace()}")
                        } finally {
                            inputStream?.close()
                            outputStream?.close()
                        }
                    } catch (e: IOException) {
                        Log.d("$TAG", "IOException: ${e.printStackTrace()}")
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    Log.d("$TAG", "item_file_down: 리스폰 실패 $t")
                }

            })
            Toast.makeText(
                context!!,
                "다운로드가 시작되었습니다.",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    /**
     * item 관련 이벤트
     */
    private fun itemEvent(editor: TreeViewEditor, adapter: ItemAdapter) {

        /**
         * file intent 설정 초기화
         */
        resultLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult(),
            ActivityResultCallback { result ->
                if (result.resultCode == RESULT_OK) {
                    val intent = result.data
                    val uri = intent!!.data
                    Log.d("$TAG", "resultLauncher?: ${uri}")

                    lateinit var multiPartFile: MultipartBody.Part
                    lateinit var fileName: String

                    context!!.contentResolver.query(uri!!, null, null, null, null)?.let {
                        if (it.moveToNext()) {
                            fileName = it.getString(it.getColumnIndex(OpenableColumns.DISPLAY_NAME))

                            val requestBody = object : RequestBody() {
                                override fun contentType(): MediaType? {
                                    return context!!.contentResolver.getType(uri)
                                        ?.toMediaType()
                                }

                                override fun writeTo(sink: BufferedSink) {
                                    sink.writeAll(
                                        context!!.contentResolver.openInputStream(uri)
                                            ?.source()!!
                                    )
                                }
                            }
                            it.close()
                            multiPartFile =
                                MultipartBody.Part.createFormData("media", fileName, requestBody)
                        } else {
                            it.close()
                            null
                        }
                    }

                    Log.d(
                        "$TAG",
                        "resultlauncher/fileInfo: $multiPartFile!!.body.contentType() $fileName"
                    )

                    val ar: List<String> = fileName.split('.')
                    val ext = ar[ar.size - 1]

                    val progressBar = filePopupView.findViewById<LinearLayout>(R.id.progressBar)
                    progressBar.visibility = View.VISIBLE

                    // zip jpg png hwp pptx ppt
                    if (ext.compareTo("zip", true) == 0 || ext.compareTo("jpg", true) == 0 ||
                        ext.compareTo("png", true) == 0 || ext.compareTo("hwp", true) == 0 ||
                        ext.compareTo("ppt", true) == 0 || ext.compareTo("pptx", true) == 0
                    ) {
                        api.item_file_save(multiPartFile, userID, targetItem.value.getItemID())
                            .enqueue(object : Callback<String> {
                                override fun onResponse(
                                    call: Call<String>,
                                    response: Response<String>
                                ) {
                                    Log.d(
                                        "$TAG",
                                        "item_file_save: 리스폰 완료 ${response.body()}"
                                    )
                                    progressBar.visibility = View.GONE
                                    filePopupWindow.dismiss()
                                    saveFileDB(targetItem, editor, adapter.mapEditable)
                                }

                                override fun onFailure(call: Call<String>, t: Throwable) {
                                    Log.d("$TAG", "item_file_save: 리스폰 실패 $t")
                                    filePopupWindow.dismiss()
                                    progressBar.visibility = View.GONE
                                }
                            })
                    } else {
                        Toast.makeText(
                            context!!,
                            "지원하지 않는 파일 형식입니다.\n지원하는 확장명 : .zip .jpg .png .hwp .pptx .ppt",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
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
            Log.d("$TAG", "setOnItemListener: ${node.value.getItemID()}")
            val id = node.value.getItemID()
            if (id != "root") {
                targetItem = node
                targetItemID = node.value.getItemID()
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
                                val item: NodeModel<ItemModel> =
                                    NodeModel<ItemModel>(
                                        ItemModel(
                                            "${parent}_item${itemMaxNum}$last",
                                            "",
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
                                binding.bottomNavigationView.visibility = View.GONE
                                true
                            }
                            R.id.bottomMenu2 -> {
                                setItem(node, editor, true)
                                "setOnItemListener/bottomMenu2: ${node.value.getItemID()}"
                                binding.bottomNavigationView.visibility = View.GONE
                                true
                            }
                            R.id.bottomMenu3 -> {

                                warnPopupWindow.isFocusable = true
                                warnPopupWindow.softInputMode =
                                    WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE
                                warnPopupWindow.update()
                                warnPopupWindow.showAtLocation(warnPopupView, Gravity.CENTER, 0, 0)


                                warnPopupWindow.setTouchInterceptor { _, motionEvent ->
                                    if (motionEvent.action == MotionEvent.ACTION_OUTSIDE) {
                                        warnPopupWindow.dismiss()
                                    }
                                    false
                                }

                                val checkButton: Button =
                                    warnPopupView.findViewById(R.id.checkButton)
                                val cancelButton: Button =
                                    warnPopupView.findViewById(R.id.cancelButton)

                                checkButton.setOnClickListener {
                                    val children = node.getChildNodes()

                                    for (i in children) {
                                        Log.d(
                                            "$TAG",
                                            "setOnItemListener/bottomMenu3: ${i.value.getItemID()}"
                                        )
                                        saveDB(i, null, "delete")
                                    }
                                    saveDB(node, null, "delete")
                                    handler.postDelayed({
                                        editor.removeNode(node)
                                    }, 1000)

                                    warnPopupWindow.dismiss()
                                }
                                cancelButton.setOnClickListener {
                                    warnPopupWindow.dismiss()
                                }
                                binding.bottomNavigationView.visibility = View.GONE
                                true
                            }
                            R.id.bottomMenu4 -> {
                                Log.d(
                                    "$TAG",
                                    "setOnItemListener/bottomMenu4: ${node} $targetItem"
                                )
                                saveFileDB(node, editor, adapter.mapEditable)
                                binding.bottomNavigationView.visibility = View.GONE
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
            targetItem = node
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
            publicSet(this.adapter.mapEditable)
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
                        "onDragMoveNodesHit: draging[${(draggingNode.value as ItemModel).getItemID()}]" +
                                "hittingNode[${(hittingNode.value as ItemModel).getItemID()}]"
                    )
                }
            }

            /**
             * Drag & Drop 완료한 두 아이템 정보 확인 후 setItem으로 넘긴다.
             */
            override fun onDragMoveNodesEnd(
                draggingNode: NodeModel<*>?,
                hittingNode: NodeModel<*>?,
                draggingView: View?,
                hittingView: View?
            ) {
                if (draggingNode != null && hittingNode != null) {
                    Log.d(
                        "$TAG",
                        "onDragMoveNodesEnd: draging[${(draggingNode.value as ItemModel).getItemID()}]" +
                                "hittingNode[${(hittingNode.value as ItemModel).getItemID()}]"
                    )
                    val dNode = draggingNode as NodeModel<ItemModel>
                    val hNode = hittingNode as NodeModel<ItemModel>

                    val hLast = if ((hittingNode.value as ItemModel).getPosition()) "L" else "R"
                    targetItemID = dNode.value.getItemID()
                    val parent =
                        if (hNode.value.getItemID() != "root" && hNode.value.getItemID() != "grade1" && hNode.value.getItemID() != "grade2" &&
                            hNode.value.getItemID() != "grade3" && hNode.value.getItemID() != "grade4"
                        )
                            hNode.value.getItemID().split("_")[1].split("$hLast")[0]
                        else hNode.value.getItemID().split("_")[0]
                    dNode.value.setItemID(
                        "${parent}_${
                            dNode.value.getItemID().split("_")[1].substring(
                                0,
                                dNode.value.getItemID().split("_")[1].length - 1
                            )
                        }$hLast"
                    )
                    Log.d("$TAG", "dNodeItemID: ${dNode.value.getItemID()}")
                    if (draggingView != null) {
                        Log.d(
                            "$TAG",
                            "dNodeItemID/targetItem.value.getItemID(): ${targetItemID}"
                        )
                        saveDB(draggingNode, draggingView, "update")
                    }
                }
            }
        })

        /**
         * 중앙 정렬 버튼
         */
        binding.focusMidButton.setOnClickListener {
            editor.focusMidLocation()
        }

        /**
         * 조회수, 추천수 확인 버튼
         */
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

        /**
         * 추천 버튼
         */
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
                        Toast.makeText(
                            context!!, "이미 추천했습니다.", Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(call: Call<PostModel>, t: Throwable) {
                    Log.d("$TAG", "map_like: 리스폰 실패 : $t")
                }
            })
        }
    }

    /**
     * 공개/비공개 설정
     */
    private fun publicSet(b: Boolean) {

        publicPopupWindow.isFocusable = true
        publicPopupWindow.softInputMode = WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE

        publicPopupWindow.update()
        publicPopupWindow.showAtLocation(publicPopupView, Gravity.CENTER, 0, 0)

        publicPopupWindow.setTouchInterceptor { _, motionEvent ->
            if (motionEvent.action == MotionEvent.ACTION_OUTSIDE) {
                publicPopupWindow.dismiss()
            }
            false
        }

        val setPublicText: TextView = publicPopupView.findViewById(R.id.passwordText)
        val setPublicButton: Button = publicPopupView.findViewById(R.id.pwButton)
        val setPassword: EditText = publicPopupView.findViewById(R.id.setPassword)

        if (!mapPublic && b) {
            setPassword.visibility = View.GONE
            setPublicText.text = "공개로 전환하시겠습니까?"
        } else {
            setPublicText.text = "패스워드를 입력하세요.(10자)"
        }

        fun savePublic(i: Int, password: String) {
            api.map_update(userID, i, password).enqueue(object : Callback<PostModel> {
                override fun onResponse(
                    call: Call<PostModel>,
                    response: Response<PostModel>
                ) {
                    Log.d("$TAG", "map_update: 리스폰 성공 ${response.body()!!.error}")
                }

                override fun onFailure(call: Call<PostModel>, t: Throwable) {
                    Log.d("$TAG", "map_update: 리스폰 실패 : $t")
                }
            })
        }

        setPublicButton.setOnClickListener {
            val password = setPassword.text.toString()
            if (b) {
                if (mapPublic) {
                    when {
                        password != "" -> {
                            mapPassword = password
                            savePublic(0, password)
                            mapPublic = false
                            binding.publicButton.setImageResource(R.drawable.ic_mindmap_private)
                            publicPopupWindow.dismiss()
                        }
                        else -> {
                            Toast.makeText(
                                context!!, "패스워드가 비어있습니다.", Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                } else {
                    mapPassword = password
                    savePublic(1, "")
                    mapPublic = true
                    binding.publicButton.setImageResource(R.drawable.ic_mindmap_public)
                    publicPopupWindow.dismiss()
                }
            } else {
                when {
                    password != mapPassword || password == "" -> {
                        Toast.makeText(
                            context!!, "비밀번호가 틀렸습니다.", Toast.LENGTH_SHORT
                        ).show()
                    }
                    else -> {
                        Toast.makeText(
                            context!!, "패스워드 입력을 완료했습니다.", Toast.LENGTH_SHORT
                        ).show()
                        binding.mapView.visibility = View.VISIBLE
                        publicPopupWindow.dismiss()
                    }
                }
            }
        }
    }

    private fun getTreeLayoutManager(): TreeLayoutManager {
        val space_50dp = 30
        val space_20dp = 80
        val line = getLine()
        return CompactHorizonLeftAndRightLayoutManager(context!!, space_50dp, space_20dp, line)
    }

    private fun getLine(): BaseLine {
        return StraightLine(Color.parseColor("#055287"), 2)
    }
}