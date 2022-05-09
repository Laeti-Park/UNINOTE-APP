package com.example.schoollifeproject

import android.Manifest
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.schoollifeproject.databinding.ActivityMainBinding
import com.example.schoollifeproject.model.APIS
import com.example.schoollifeproject.model.PostModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * 어플 실행 로그인 Activity
 * 작성자 : 이준영
 */
class MainActivity : AppCompatActivity() {

    val TAG: String = "MainActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

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

        val binding = ActivityMainBinding.inflate(layoutInflater)
        val api = APIS.create()
        setContentView(binding.root)

        val btnLogin = binding.btnLogin
        val btnRegister = binding.btnRegister
        val btnNonLogin = binding.btnNonlogin

        /**
         * 메인화면 로그인버튼 클릭리스너
         * */
        btnLogin.setOnClickListener {
            val id = binding.editId.text.toString()
            val pw = binding.editPw.text.toString()
            val intent = Intent(this, MenuActivity::class.java)

            /**
             * 레트로핏 서버 접근 id value를 이용해 DB에서 pw호출
             * id, pw를 비교 일치하는지 확인
             * */
            api.login_users(
                id
            ).enqueue(object : Callback<PostModel> {
                //접근성공
                override fun onResponse(
                    call: Call<PostModel>,
                    response: Response<PostModel>
                ) {
                    if (!response.body().toString().isEmpty()) {
                        //해당 아이디가 로그인상태인지 체크 후 로그인
                        when {
                            response.body()?.error.toString() == "error" -> failDialog("isLogin")
                            pw == response.body()?.userPassword.toString() -> {
                                intent.putExtra("ID", response.body()?.userID.toString())
                                intent.putExtra("name", response.body()?.userName.toString())
                                startActivity(intent)
                            }
                            else -> {
                                failDialog("fail")
                            }
                        }
                    }
                }

                override fun onFailure(p0: Call<PostModel>, t: Throwable) {
                    failDialog("fail")
                }

            })
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
    }

    /**
     * 로그인 실패 Dialog
     * */

    fun failDialog(error: String) {
        var dialog = AlertDialog.Builder(this)

        dialog.setTitle("로그인실패")
        if (error == "isLogin")
            dialog.setMessage("다른 브라우저에서 로그아웃해주세요")
        else if (error == "fail")
            dialog.setMessage("인터넷 연결을 확인해주세요")
        else
            dialog.setMessage("아이디와 비밀번호를 확인해주세요")

        val dialog_listener = DialogInterface.OnClickListener { dialog, which ->
            when (which) {
                DialogInterface.BUTTON_POSITIVE ->
                    Log.d(TAG, "확인 버튼 클릭")
            }
        }

        dialog.setPositiveButton("확인", dialog_listener)
        dialog.show()
    }
}