package com.example.schoollifeproject

import android.Manifest
import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.schoollifeproject.databinding.ActivityMainBinding
import com.example.schoollifeproject.model.APIS
import com.example.schoollifeproject.model.PostModel
import com.example.schoollifeproject.shared.Shared
import com.google.android.material.snackbar.Snackbar
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.system.exitProcess

/**
 * 어플 실행 로그인 Activity
 * 작성자 : 이준영
 */
class MainActivity : AppCompatActivity() {
    private val api = APIS.create()
    private var backWait: Long = 0

    private lateinit var btnDestroy: Button
    private lateinit var btnLogin: Button
    private lateinit var btnRegister: Button
    private lateinit var btnNonLogin: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        /**
         * 파일관리자 접근 권한 설정
         */
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(
                    applicationContext,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(
                    applicationContext, Manifest.permission.READ_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissions(
                    arrayOf(
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    ), 2
                )
            }
        }

        btnDestroy = binding.btnDestroy
        btnLogin = binding.btnLogin
        btnRegister = binding.btnRegister
        btnNonLogin = binding.btnNonlogin

        /**
         * 메인화면 로그인버튼 클릭리스너
         * */
        btnLogin.setOnClickListener {
            val id = binding.editId.text.toString()
            val pw = binding.editPw.text.toString()
            login(id, pw, 0)
        }

        /**
         * 회원가입, 비회원로그인 버튼 클릭리스너
         * */
        btnRegister.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        btnNonLogin.setOnClickListener {
            val intent = Intent(this, MenuActivity::class.java)
            intent.putExtra("ID", "비회원")
            intent.putExtra("loginCheck", 1)
            startActivity(intent)
        }

        btnDestroy.setOnClickListener {
            finishAffinity()
            exitProcess(0)
        }

        /**
         * 로그아웃하지않고 종료 시 자동로그인
         * */
        if (Shared.prefs.getString("id", "nothing") != "nothing") {
            val id = Shared.prefs.getString("id", "nothing")
            val pw = Shared.prefs.getString("pw", "nothing")
            login(id, pw, 1) // 타입 1 = 자동로그인
        }
    }


    /**
     * 레트로핏 서버 접근 id value를 이용해 DB에서 pw호출
     * id, pw를 비교 일치하는지 확인
     * type = 0:직접 로그인, 1:자동 로그인
     * */
    fun login(id: String, pw: String, type: Int) {
        val intent = Intent(this, MenuActivity::class.java)
        api.login_users(
            type, id
        ).enqueue(object : Callback<PostModel> {
            //접근 성공
            override fun onResponse(
                call: Call<PostModel>,
                response: Response<PostModel>
            ) {
                when {
                    response.body()?.error == "error" -> failDialog("isLogin") //다른 소프트웨어에서 로그인 중 일때 dialog 호출
                    pw == response.body()?.userPassword -> {                        //로그인 시도한 정보가 일치했을 때 메뉴로 진입
                        intent.putExtra("ID", response.body()?.userID)
                        intent.putExtra("name", response.body()?.userName)
                        intent.putExtra("PW", response.body()?.userPassword)
                        startActivity(intent)
                    }
                    else -> {                                                        //로그인 시도 정보가 일치하지 않았을 때 dialog 호출
                        failDialog("failed")
                    }
                }
            }

            //접근 실패
            override fun onFailure(p0: Call<PostModel>, t: Throwable) {
                failDialog("fail")                                              //서버연결 실패 dialog호출
            }
        })
    }

    /**
     * 로그인 실패 Dialog
     * */
    fun failDialog(error: String) {
        var dialog = AlertDialog.Builder(this)

        dialog.setTitle("로그인실패")

        when (error) {
            "isLogin" -> dialog.setMessage("다른 브라우저에서 로그아웃해주세요")
            "fail" -> dialog.setMessage("인터넷 연결을 확인해주세요")
            else -> dialog.setMessage("아이디와 비밀번호를 확인해주세요")
        }

        val dialog_listener = DialogInterface.OnClickListener { _, which ->
            when (which) {
                DialogInterface.BUTTON_POSITIVE ->
                    Log.d("Main", "확인 버튼 클릭")
            }
        }

        dialog.setPositiveButton("확인", dialog_listener)
        dialog.show()
    }

    override fun onBackPressed() {
        if (System.currentTimeMillis() - backWait < 2000) {
            finishAffinity()
            exitProcess(0)
        }
        backWait = System.currentTimeMillis()
        Toast.makeText(this, "한번 더 입력시 종료됩니다.", Toast.LENGTH_SHORT).show()
    }
}