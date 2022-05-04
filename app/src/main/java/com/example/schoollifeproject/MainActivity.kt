package com.example.schoollifeproject
import androidx.appcompat.app.AlertDialog
import android.content.DialogInterface

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.schoollifeproject.databinding.ActivityMainBinding
import com.example.schoollifeproject.model.APIS_login
import com.example.schoollifeproject.model.PostModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class MainActivity : AppCompatActivity() {

    val TAG: String = "MainActivity"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        val api_login = APIS_login.create()
        setContentView(binding.root)

        val btnLogin = binding.btnLogin
        val btnRegister = binding.btnRegister
        val btnNonLogin = binding.btnNonlogin


        btnLogin.setOnClickListener {
            Log.e("click", "11")
            val id = binding.editId.text.toString()
            val pw = binding.editPw.text.toString()

            val intent = Intent(this, MenuActivity::class.java)


            api_login.login_users(
                id
            ).enqueue(object : Callback<PostModel> {

                override fun onResponse(
                    call: Call<PostModel>,
                    response: Response<PostModel>
                ) {
                    Log.d("dbTestNoBody",response.toString())
                    Log.d("dbTestBody",response.body().toString())
                    if(!response.body().toString().isEmpty()) {
                        if(pw.equals( response.body()?.userPassword.toString())) {
                            intent.putExtra("ID", response.body()?.userID.toString())
                            intent.putExtra("name", response.body()?.userName.toString())
                            startActivity(intent)
                        }
                        else{
                            failDialog()
                        }
                    }
                }

                override fun onFailure(p0: Call<PostModel>, t: Throwable) {
                    Log.d("failedDBopen", t.message.toString())
                    failDialog()
                }
            })
        }

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

    fun failDialog(){
        var dialog = AlertDialog.Builder(this)

         dialog.setTitle("로그인 실패")
         dialog.setMessage("아이디와 비밀번호를 확인해주세요")

         val dialog_listener = object: DialogInterface.OnClickListener{
           override fun onClick(dialog: DialogInterface?, which: Int) {
             when(which){
               DialogInterface.BUTTON_POSITIVE ->
               Log.d(TAG, "확인 버튼 클릭")
             }
           }
         }

         dialog.setPositiveButton("확인", dialog_listener)
         dialog.show()
    }
}